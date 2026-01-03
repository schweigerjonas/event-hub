package de.othr.event_hub.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.othr.event_hub.config.AccountUserDetails;
import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.EventInvitation;
import de.othr.event_hub.model.EventParticipant;
import de.othr.event_hub.model.enums.EventInvitationStatus;
import de.othr.event_hub.service.EventInvitationService;
import de.othr.event_hub.service.EventParticipantService;
import de.othr.event_hub.service.EventService;

@Controller
@RequestMapping("/invitations")
public class EventInvitationController {

    private final EventInvitationService invitationService;
    private final EventService eventService;
    private final EventParticipantService participantService;

    public EventInvitationController(
        EventInvitationService invitationService,
        EventService eventService,
        EventParticipantService participantService
    ) {
        super();
        this.invitationService = invitationService;
        this.eventService = eventService;
        this.participantService = participantService;
    }

    @GetMapping
    public String listInvitations(
        @RequestParam(required = false, defaultValue = "incoming") String tab,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false) String keyword,
        @AuthenticationPrincipal AccountUserDetails details,
        Model model
    ) {
        if (details == null || details.getUser() == null) {
            return "redirect:/login?redirect=/invitations";
        }
        int safePage = Math.max(page, 1);
        int safeSize = size < 1 ? 10 : size;
        Pageable pageable = PageRequest.of(safePage - 1, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<EventInvitation> invitations;
        if ("sent".equalsIgnoreCase(tab)) {
            invitations = invitationService.getOutgoingInvitations(details.getUser(), keyword, pageable);
        } else {
            invitations = invitationService.getIncomingInvitations(details.getUser(), keyword, pageable);
            tab = "incoming";
        }
        model.addAttribute("tab", tab);
        model.addAttribute("invitations", invitations.getContent());
        model.addAttribute("currentPage", invitations.getNumber() + 1);
        model.addAttribute("totalPages", invitations.getTotalPages());
        model.addAttribute("totalItems", invitations.getTotalElements());
        model.addAttribute("pageSize", safeSize);
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        return "invitations/invitations-all";
    }

    @PostMapping("/{id}/accept")
    public String acceptInvitation(
        @PathVariable("id") Long id,
        @AuthenticationPrincipal AccountUserDetails details,
        RedirectAttributes redirectAttributes
    ) {
        EventInvitation invitation = invitationService.getInvitationById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (details == null || details.getUser() == null || !invitation.getInvitee().equals(details.getUser())) {
            return "redirect:/login?redirect=/invitations";
        }
        if (hasAuthority(details, "ADMIN")) {
            redirectAttributes.addFlashAttribute("error", "Admins k\u00f6nnen nicht an Events teilnehmen.");
            return "redirect:/invitations";
        }
        if (invitation.getStatus() != EventInvitationStatus.PENDING) {
            redirectAttributes.addFlashAttribute("error", "Einladung ist nicht mehr aktiv.");
            return "redirect:/invitations";
        }
        Event event = eventService.getEventById(invitation.getEvent().getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (event.getMaxParticipants() != null
            && participantService.countParticipants(event) >= event.getMaxParticipants()) {
            redirectAttributes.addFlashAttribute("error", "Dieses Event ist bereits ausgebucht.");
            return "redirect:/invitations";
        }
        if (!participantService.existsParticipant(event, details.getUser())) {
            EventParticipant participant = new EventParticipant();
            participant.setEvent(event);
            participant.setUser(details.getUser());
            participant.setOrganizer(false);
            participant.setJoinedAt(LocalDateTime.now());
            participantService.createParticipant(participant);
        }
        invitation.setStatus(EventInvitationStatus.ACCEPTED);
        invitation.setRespondedAt(LocalDateTime.now());
        invitationService.updateInvitation(invitation);
        redirectAttributes.addFlashAttribute("success", "Einladung angenommen.");
        return "redirect:/invitations";
    }

    @PostMapping("/{id}/decline")
    public String declineInvitation(
        @PathVariable("id") Long id,
        @AuthenticationPrincipal AccountUserDetails details,
        RedirectAttributes redirectAttributes
    ) {
        EventInvitation invitation = invitationService.getInvitationById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (details == null || details.getUser() == null || !invitation.getInvitee().equals(details.getUser())) {
            return "redirect:/login?redirect=/invitations";
        }
        invitation.setStatus(EventInvitationStatus.DECLINED);
        invitation.setRespondedAt(LocalDateTime.now());
        invitationService.updateInvitation(invitation);
        redirectAttributes.addFlashAttribute("info", "Einladung abgelehnt.");
        return "redirect:/invitations";
    }

    private boolean hasAuthority(AccountUserDetails details, String authority) {
        if (details == null || details.getAuthorities() == null || authority == null) {
            return false;
        }
        return details.getAuthorities().stream()
            .anyMatch(granted -> granted.getAuthority().equalsIgnoreCase(authority));
    }
}
