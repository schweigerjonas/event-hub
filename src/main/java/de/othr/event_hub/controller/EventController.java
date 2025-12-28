package de.othr.event_hub.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;

import de.othr.event_hub.config.AccountUserDetails;
import de.othr.event_hub.dto.EventFormDto;
import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.EventParticipant;
import de.othr.event_hub.model.Friendship;
import de.othr.event_hub.model.User;
import de.othr.event_hub.service.EmailService;
import de.othr.event_hub.service.EventService;
import de.othr.event_hub.service.EventParticipantService;
import de.othr.event_hub.service.FriendshipService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final EventParticipantService eventParticipantService;
    private final FriendshipService friendshipService;
    private final EmailService emailService;

    public EventController(
        EventService eventService,
        EventParticipantService eventParticipantService,
        FriendshipService friendshipService,
        EmailService emailService
    ) {
        super();
        this.eventService = eventService;
        this.eventParticipantService = eventParticipantService;
        this.friendshipService = friendshipService;
        this.emailService = emailService;
    }

    @GetMapping
    public String listEvents(
        Model model,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false, defaultValue = "time") String sort,
        @RequestParam(required = false, defaultValue = "asc") String direction,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "9") int size
    ) {
        int safePage = Math.max(page, 1);
        int safeSize = size < 1 ? 9 : size;
        String sortValue = sort == null ? "time" : sort;
        String sortField;
        switch (sortValue) {
            case "name":
                sortField = "name";
                break;
            case "costs":
                sortField = "costs";
                break;
            default:
                sortField = "eventTime";
                sortValue = "time";
                break;
        }

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(safePage - 1, safeSize, Sort.by(sortDirection, sortField));
        Page<Event> eventsPage = eventService.getEvents(keyword, pageable);

        model.addAttribute("events", eventsPage.getContent());
        model.addAttribute("currentPage", eventsPage.getNumber() + 1);
        model.addAttribute("totalPages", eventsPage.getTotalPages());
        model.addAttribute("totalItems", eventsPage.getTotalElements());
        model.addAttribute("pageSize", safeSize);
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("sort", sortValue);
        model.addAttribute("direction", sortDirection.name().toLowerCase());

        return "events/events-all";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("eventForm")) {
            model.addAttribute("eventForm", new EventFormDto());
        }
        return "events/event-form";
    }

    @PostMapping
    public String createEvent(
        @Valid @ModelAttribute("eventForm") EventFormDto eventForm,
        BindingResult result,
        @AuthenticationPrincipal AccountUserDetails details
    ) {
        if (eventForm.isPaid() && (eventForm.getCosts() == null || eventForm.getCosts() <= 0)) {
            result.rejectValue("costs", "event.costs.required", "Bitte geben Sie einen Preis an.");
        }

        if (result.hasErrors()) {
            return "events/event-form";
        }

        Event event = new Event();
        event.setName(eventForm.getName().trim());
        event.setLocation(eventForm.getLocation().trim());
        event.setDurationMinutes(eventForm.getDurationMinutes());
        event.setMaxParticipants(eventForm.getMaxParticipants());
        event.setDescription(cleanDescription(eventForm.getDescription()));
        event.setEventTime(eventForm.getEventTime());
        event.setCosts(eventForm.isPaid() ? (eventForm.getCosts() == null ? 0.0 : eventForm.getCosts()) : 0.0);
        event.setOrganizer(details.getUser());

        Event createdEvent = eventService.createEvent(event);
        return "redirect:/events/" + createdEvent.getId();
    }

    @GetMapping("/{id}")
    public String showEventDetails(
        @PathVariable("id") Long id,
        @RequestParam(required = false, defaultValue = "1") int participantsPage,
        @RequestParam(required = false, defaultValue = "10") int participantsSize,
        @AuthenticationPrincipal AccountUserDetails details,
        Model model
    ) {
        Event event = eventService.getEventById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("event", event);
        boolean isParticipant = false;
        if (details != null) {
            isParticipant = eventParticipantService.existsParticipant(event, details.getUser());
            int safePage = Math.max(participantsPage, 1);
            int safeSize = participantsSize < 1 ? 10 : participantsSize;
            Pageable pageable = PageRequest.of(safePage - 1, safeSize, Sort.by(Sort.Direction.DESC, "joinedAt"));
            Page<EventParticipant> participants = eventParticipantService.getParticipants(event, pageable);
            model.addAttribute("participants", participants.getContent());
            model.addAttribute("participantsPage", participants.getNumber() + 1);
            model.addAttribute("participantsTotalPages", participants.getTotalPages());
            model.addAttribute("participantsTotalItems", participants.getTotalElements());
            model.addAttribute("participantsSize", safeSize);
            model.addAttribute("friends", getFriends(details.getUser()));
        }
        model.addAttribute("isParticipant", isParticipant);
        return "events/event-detail";
    }

    @PostMapping("/{id}/join")
    public String joinEvent(
        @PathVariable("id") Long id,
        @AuthenticationPrincipal AccountUserDetails details,
        RedirectAttributes redirectAttributes,
        HttpServletRequest request
    ) {
        Event event = eventService.getEventById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (details == null || details.getUser() == null) {
            request.getSession(true).setAttribute("loginRedirect", "/events/" + id);
            return "redirect:/login?redirect=/events/" + id;
        }
        if (eventParticipantService.existsParticipant(event, details.getUser())) {
            redirectAttributes.addFlashAttribute("info", "Du nimmst bereits teil.");
            return "redirect:/events/" + id;
        }
        if (event.getMaxParticipants() != null && eventParticipantService.countParticipants(event) >= event.getMaxParticipants()) {
            redirectAttributes.addFlashAttribute("error", "Dieses Event ist bereits ausgebucht.");
            return "redirect:/events/" + id;
        }
        if (event.getCosts() > 0) {
            return "redirect:/events/" + id + "/payments";
        }

        EventParticipant participant = new EventParticipant();
        participant.setEvent(event);
        participant.setUser(details.getUser());
        participant.setJoinedAt(LocalDateTime.now());
        eventParticipantService.createParticipant(participant);
        redirectAttributes.addFlashAttribute("success", "Du bist angemeldet.");
        return "redirect:/events/" + id;
    }

    @PostMapping("/{id}/invite")
    public String inviteFriend(
        @PathVariable("id") Long id,
        @RequestParam(name = "friendIds", required = false) List<Long> friendIds,
        @AuthenticationPrincipal AccountUserDetails details,
        RedirectAttributes redirectAttributes,
        HttpServletRequest request
    ) {
        if (details == null || details.getUser() == null) {
            request.getSession(true).setAttribute("loginRedirect", "/events/" + id);
            return "redirect:/login?redirect=/events/" + id;
        }
        Event event = eventService.getEventById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        List<User> friends = getFriends(details.getUser());
        if (friendIds == null || friendIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Bitte mindestens einen Freund auswählen.");
            return "redirect:/events/" + id;
        }
        int sentCount = 0;
        for (Long friendId : friendIds) {
            User friend = friends.stream()
                .filter(user -> user.getId().equals(friendId))
                .findFirst()
                .orElse(null);
            if (friend != null && emailService.sendEventInvitation(friend, event, details.getUser())) {
                sentCount++;
            }
        }
        if (sentCount > 0) {
            redirectAttributes.addFlashAttribute("success", "Einladung versendet an " + sentCount + " Freund(e).");
        } else {
            redirectAttributes.addFlashAttribute("error", "Einladung konnte nicht versendet werden.");
        }
        return "redirect:/events/" + id;
    }

    @PostMapping("/{id}/leave")
    public String leaveEvent(
        @PathVariable("id") Long id,
        @AuthenticationPrincipal AccountUserDetails details,
        RedirectAttributes redirectAttributes,
        HttpServletRequest request
    ) {
        Event event = eventService.getEventById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (details == null || details.getUser() == null) {
            request.getSession(true).setAttribute("loginRedirect", "/events/" + id);
            return "redirect:/login?redirect=/events/" + id;
        }
        if (!eventParticipantService.existsParticipant(event, details.getUser())) {
            redirectAttributes.addFlashAttribute("info", "Du bist nicht angemeldet.");
            return "redirect:/events/" + id;
        }
        eventParticipantService.deleteParticipant(event, details.getUser());
        redirectAttributes.addFlashAttribute("success", "Du bist abgemeldet.");
        return "redirect:/events/" + id;
    }

    private String cleanDescription(String description) {
        if (description == null) {
            return null;
        }
        String trimmed = description.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private List<User> getFriends(User currentUser) {
        List<Friendship> friendships = friendshipService.findActiveFriendshipsByUser(currentUser);
        return friendships.stream()
            .map(friendship -> friendship.getRequestor().equals(currentUser)
                ? friendship.getAddressee()
                : friendship.getRequestor())
            .sorted((a, b) -> a.getUsername().compareToIgnoreCase(b.getUsername()))
            .toList();
    }
}
