package de.othr.event_hub.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import de.othr.event_hub.config.AccountUserDetails;
import de.othr.event_hub.dto.EventFormDto;
import de.othr.event_hub.dto.WeatherDto;
import de.othr.event_hub.dto.WeatherResponse;
import de.othr.event_hub.model.ChatMembership;
import de.othr.event_hub.model.ChatRoom;
import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.EventInvitation;
import de.othr.event_hub.model.EventParticipant;
import de.othr.event_hub.model.Friendship;
import de.othr.event_hub.model.Payment;
import de.othr.event_hub.model.Rating;
import de.othr.event_hub.model.User;
import de.othr.event_hub.model.enums.ChatMembershipRole;
import de.othr.event_hub.model.enums.ChatRoomType;
import de.othr.event_hub.model.enums.EventInvitationStatus;
import de.othr.event_hub.model.enums.NotificationType;
import de.othr.event_hub.service.ChatMembershipService;
import de.othr.event_hub.service.ChatRoomService;
import de.othr.event_hub.service.EmailService;
import de.othr.event_hub.service.EventFavouriteService;
import de.othr.event_hub.service.EventInvitationService;
import de.othr.event_hub.service.EventParticipantService;
import de.othr.event_hub.service.EventService;
import de.othr.event_hub.service.FriendshipService;
import de.othr.event_hub.service.LocationCoordinates;
import de.othr.event_hub.service.LocationService;
import de.othr.event_hub.service.NotificationService;
import de.othr.event_hub.service.PaymentService;
import de.othr.event_hub.service.PdfService;
import de.othr.event_hub.service.RatingService;
import de.othr.event_hub.service.WeatherService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/events")
public class EventController {

    private final ChatMembershipService chatMembershipService;
    private final ChatRoomService chatRoomService;
    private final EventService eventService;
    private final EventFavouriteService eventFavouriteService;
    private final EventParticipantService eventParticipantService;
    private final FriendshipService friendshipService;
    private final EmailService emailService;
    private final RatingService ratingService;
    private final EventInvitationService eventInvitationService;
    private final PdfService pdfService;
    private final PaymentService paymentService;
    private final LocationService locationService;
    private final WeatherService weatherService;
    private final NotificationService notificationService;

    public EventController(
            ChatMembershipService chatMembershipService,
            ChatRoomService chatRoomService,
            EventService eventService,
            EventFavouriteService eventFavouriteService,
            EventParticipantService eventParticipantService,
            FriendshipService friendshipService,
            EmailService emailService,
            RatingService ratingService,
            EventInvitationService eventInvitationService,
            PdfService pdfService,
            PaymentService paymentService,
            LocationService locationService,
            WeatherService weatherService,
            NotificationService notificationService) {
        super();
        this.chatMembershipService = chatMembershipService;
        this.chatRoomService = chatRoomService;
        this.eventService = eventService;
        this.eventFavouriteService = eventFavouriteService;
        this.eventParticipantService = eventParticipantService;
        this.friendshipService = friendshipService;
        this.emailService = emailService;
        this.ratingService = ratingService;
        this.eventInvitationService = eventInvitationService;
        this.pdfService = pdfService;
        this.paymentService = paymentService;
        this.locationService = locationService;
        this.weatherService = weatherService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public String listEvents(
            Model model,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "time") String sort,
            @RequestParam(required = false, defaultValue = "asc") String direction,
            @RequestParam(required = false, defaultValue = "false") boolean onlyFavourites,
            @RequestParam(required = false, defaultValue = "discover") String tab,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "9") int size,
            @AuthenticationPrincipal AccountUserDetails user,
            HttpServletRequest request) {
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
        Page<Event> eventsPage;
        String tabValue = tab == null ? "discover" : tab;
        boolean mineTab = "mine".equalsIgnoreCase(tabValue);
        if (mineTab) {
            if (user == null || user.getUser() == null) {
                request.getSession(true).setAttribute("loginRedirect", "/events?tab=mine");
                return "redirect:/login?redirect=/events?tab=mine";
            }
            eventsPage = eventService.getParticipatingEvents(keyword, user.getUser(), pageable);
        } else if (onlyFavourites && user != null) {
            eventsPage = eventService.getFavouriteEvents(keyword, user.getUser(), pageable);
        } else {
            eventsPage = eventService.getEvents(keyword, pageable);
            tabValue = "discover";
            mineTab = false;
        }

        List<Event> events = eventsPage.getContent();
        model.addAttribute("events", events);
        model.addAttribute("currentPage", eventsPage.getNumber() + 1);
        model.addAttribute("totalPages", eventsPage.getTotalPages());
        model.addAttribute("totalItems", eventsPage.getTotalElements());
        model.addAttribute("pageSize", safeSize);
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("onlyFavourites", !mineTab && onlyFavourites);
        model.addAttribute("tab", tabValue);
        model.addAttribute("sort", sortValue);
        model.addAttribute("direction", sortDirection.name().toLowerCase());
        if (user != null && user.getUser() != null) {
            Set<Long> favouriteIds = new HashSet<>();
            Set<Long> participantIds = new HashSet<>();
            for (Event event : events) {
                if (eventFavouriteService.isEventFavourite(event, user.getUser())) {
                    favouriteIds.add(event.getId());
                }
                if (eventParticipantService.existsParticipant(event, user.getUser())) {
                    participantIds.add(event.getId());
                }
            }
            model.addAttribute("favouriteEventIds", favouriteIds);
            model.addAttribute("participantEventIds", participantIds);
        }

        return "events/events-all";
    }

    @GetMapping("/new")
    public String showCreateForm(
            Model model,
            @AuthenticationPrincipal AccountUserDetails details,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        if (details == null || details.getUser() == null) {
            request.getSession(true).setAttribute("loginRedirect", "/events/new");
            return "redirect:/login?redirect=/events/new";
        }
        boolean canCreate = hasAuthority(details, "ADMIN") || hasAuthority(details, "ORGANISATOR");
        if (!canCreate) {
            redirectAttributes.addFlashAttribute("error", "Nur Organisatoren oder Admins können Events erstellen.");
            return "redirect:/events";
        }
        if (!model.containsAttribute("eventForm")) {
            model.addAttribute("eventForm", new EventFormDto());
        }
        return "events/event-form";
    }

    @PostMapping
    public String createEvent(
            @Valid @ModelAttribute("eventForm") EventFormDto eventForm,
            BindingResult result,
            @AuthenticationPrincipal AccountUserDetails details,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        if (details == null || details.getUser() == null) {
            request.getSession(true).setAttribute("loginRedirect", "/events/new");
            return "redirect:/login?redirect=/events/new";
        }
        boolean canCreate = hasAuthority(details, "ADMIN") || hasAuthority(details, "ORGANISATOR");
        if (!canCreate) {
            redirectAttributes.addFlashAttribute("error", "Nur Organisatoren oder Admins können Events erstellen.");
            return "redirect:/events";
        }
        if (eventForm.isPaid() && (eventForm.getCosts() == null || eventForm.getCosts() <= 0)) {
            result.rejectValue("costs", "event.costs.required", "Bitte geben Sie einen Preis an.");
        }
        LocationCoordinates coordinates = null;
        if (!result.hasFieldErrors("location")) {
            coordinates = locationService.findCoordinates(eventForm.getLocation()).orElse(null);
            if (coordinates == null) {
                result.rejectValue("location", "event.location.invalid", "Ort wurde nicht gefunden.");
            }
        }

        if (result.hasErrors()) {
            return "events/event-form";
        }

        Event event = new Event();
        event.setName(eventForm.getName().trim());
        event.setLocation(eventForm.getLocation().trim());
        if (coordinates != null) {
            event.setLatitude(coordinates.latitude());
            event.setLongitude(coordinates.longitude());
        }
        event.setDurationMinutes(eventForm.getDurationMinutes());
        event.setMaxParticipants(eventForm.getMaxParticipants());
        event.setDescription(cleanDescription(eventForm.getDescription()));
        event.setEventTime(eventForm.getEventTime());
        event.setCosts(eventForm.isPaid() ? (eventForm.getCosts() == null ? 0.0 : eventForm.getCosts()) : 0.0);
        event.setOrganizer(details.getUser());

        Event createdEvent = eventService.createEvent(event);
        EventParticipant organizerParticipant = new EventParticipant();
        organizerParticipant.setEvent(createdEvent);
        organizerParticipant.setUser(details.getUser());
        organizerParticipant.setOrganizer(true);
        organizerParticipant.setJoinedAt(LocalDateTime.now());
        eventParticipantService.createParticipant(organizerParticipant);

        // create chat room
        LocalDateTime now = LocalDateTime.now();
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setType(ChatRoomType.EVENT);
        chatRoom.setOwner(details.getUser());
        chatRoom.setCreatedAt(now);
        chatRoom.setEvent(createdEvent);
        chatRoom = chatRoomService.createChatRoom(chatRoom);

        // update event
        createdEvent.setEventChatRoom(chatRoom);
        eventService.updateEvent(createdEvent);

        // configure chat membership
        ChatMembership chatMembership = new ChatMembership();
        chatMembership.setChatRoom(chatRoom);
        chatMembership.setUser(details.getUser());
        chatMembership.setRole(ChatMembershipRole.CHATADMIN);
        chatMembership.setJoinedAt(now);
        chatMembershipService.createChatMembership(chatMembership);

        return "redirect:/events/" + createdEvent.getId();
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal AccountUserDetails details,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (details == null || details.getUser() == null) {
            request.getSession(true).setAttribute("loginRedirect", "/events/" + id + "/edit");
            return "redirect:/login?redirect=/events/" + id + "/edit";
        }
        boolean isOrganizer = event.getOrganizer() != null && event.getOrganizer().equals(details.getUser());
        boolean isAdmin = hasAuthority(details, "ADMIN");
        if (!isOrganizer && !isAdmin) {
            redirectAttributes.addFlashAttribute("error", "Nur der Organisator oder Admin kann das Event bearbeiten.");
            return "redirect:/events/" + id;
        }
        EventFormDto form = new EventFormDto();
        form.setName(event.getName());
        form.setLocation(event.getLocation());
        form.setDurationMinutes(event.getDurationMinutes());
        form.setMaxParticipants(event.getMaxParticipants());
        form.setPaid(event.getCosts() > 0);
        form.setCosts(event.getCosts());
        form.setDescription(event.getDescription());
        form.setEventTime(event.getEventTime());
        model.addAttribute("eventForm", form);
        model.addAttribute("eventId", id);
        return "events/event-edit";
    }

    @PostMapping("/{id}/edit")
    public String updateEvent(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("eventForm") EventFormDto eventForm,
            BindingResult result,
            @AuthenticationPrincipal AccountUserDetails details,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request,
            Model model) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (details == null || details.getUser() == null) {
            request.getSession(true).setAttribute("loginRedirect", "/events/" + id + "/edit");
            return "redirect:/login?redirect=/events/" + id + "/edit";
        }
        boolean isOrganizer = event.getOrganizer() != null && event.getOrganizer().equals(details.getUser());
        boolean isAdmin = hasAuthority(details, "ADMIN");
        if (!isOrganizer && !isAdmin) {
            redirectAttributes.addFlashAttribute("error", "Nur der Organisator oder Admin kann das Event bearbeiten.");
            return "redirect:/events/" + id;
        }
        if (eventForm.isPaid() && (eventForm.getCosts() == null || eventForm.getCosts() <= 0)) {
            result.rejectValue("costs", "event.costs.required", "Bitte geben Sie einen Preis an.");
        }
        LocationCoordinates coordinates = null;
        if (!result.hasFieldErrors("location")) {
            coordinates = locationService.findCoordinates(eventForm.getLocation()).orElse(null);
            if (coordinates == null) {
                result.rejectValue("location", "event.location.invalid", "Ort wurde nicht gefunden.");
            }
        }
        if (result.hasErrors()) {
            model.addAttribute("eventId", id);
            return "events/event-edit";
        }
        event.setName(eventForm.getName().trim());
        event.setLocation(eventForm.getLocation().trim());
        if (coordinates != null) {
            event.setLatitude(coordinates.latitude());
            event.setLongitude(coordinates.longitude());
        }
        event.setDurationMinutes(eventForm.getDurationMinutes());
        event.setMaxParticipants(eventForm.getMaxParticipants());
        event.setDescription(cleanDescription(eventForm.getDescription()));
        event.setEventTime(eventForm.getEventTime());
        event.setCosts(eventForm.isPaid() ? (eventForm.getCosts() == null ? 0.0 : eventForm.getCosts()) : 0.0);
        eventService.updateEvent(event);
        redirectAttributes.addFlashAttribute("success", "Event wurde aktualisiert.");
        return "redirect:/events/" + id;
    }

    @GetMapping("/{id}")
    public String showEventDetails(
            @PathVariable("id") Long id,
            @RequestParam(required = false, defaultValue = "1") int participantsPage,
            @RequestParam(required = false, defaultValue = "10") int participantsSize,
            @RequestParam(required = false, defaultValue = "1") int ratingPage,
            @RequestParam(required = false, defaultValue = "5") int ratingSize,
            @RequestParam(required = false) String ratingKeyword,
            @AuthenticationPrincipal AccountUserDetails details,
            Model model) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("event", event);
        boolean isParticipant = false;
        boolean isOrganizer = false;
        boolean isAdmin = false;
        boolean canViewPayments = false;
        if (details != null) {
            isParticipant = eventParticipantService.existsParticipant(event, details.getUser());
            isOrganizer = event.getOrganizer() != null && event.getOrganizer().equals(details.getUser());
            isAdmin = hasAuthority(details, "ADMIN");
            // Payments only for organizer/admin
            canViewPayments = isOrganizer || isAdmin;
            int safePage = Math.max(participantsPage, 1);
            int safeSize = participantsSize < 1 ? 10 : participantsSize;
            Pageable pageable = PageRequest.of(
                    safePage - 1,
                    safeSize,
                    Sort.by(Sort.Direction.DESC, "organizer").and(Sort.by(Sort.Direction.DESC, "joinedAt")));
            Page<EventParticipant> participants = eventParticipantService.getParticipants(event, pageable);
            model.addAttribute("participants", participants.getContent());
            model.addAttribute("participantsPage", participants.getNumber() + 1);
            model.addAttribute("participantsTotalPages", participants.getTotalPages());
            model.addAttribute("participantsTotalItems", participants.getTotalElements());
            model.addAttribute("participantsSize", safeSize);
            if (canViewPayments) {
                Map<Long, Double> paidAmounts = new HashMap<>();
                for (EventParticipant participant : participants.getContent()) {
                    if (participant.getUser() != null) {
                        paidAmounts.put(
                                participant.getUser().getId(),
                                paymentService.getTotalPaidAmountForEventAndUser(event, participant.getUser()));
                    }
                }
                model.addAttribute("participantPaidAmounts", paidAmounts);
            }
            if (isParticipant) {
                model.addAttribute("friends", getFriends(details.getUser()));
                ratingService.getRatingByEventAndUser(event, details.getUser())
                        .ifPresent(rating -> model.addAttribute("userRating", rating));
            }

            // check if event is favourited by user
            boolean isFavourite = eventFavouriteService.isEventFavourite(event, details.getUser());
            model.addAttribute("isFavourite", isFavourite);
        }
        model.addAttribute("isParticipant", isParticipant);
        model.addAttribute("isOrganizer", isOrganizer);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("canViewPayments", canViewPayments);

        int safeRatingPage = Math.max(ratingPage, 1);
        int safeRatingSize = ratingSize < 1 ? 5 : ratingSize;
        Pageable ratingPageable = PageRequest.of(
                safeRatingPage - 1,
                safeRatingSize,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Rating> ratingsPage = ratingService.getRatings(event, ratingKeyword, ratingPageable);
        model.addAttribute("ratings", ratingsPage.getContent());
        model.addAttribute("ratingPage", ratingsPage.getNumber() + 1);
        model.addAttribute("ratingTotalPages", ratingsPage.getTotalPages());
        model.addAttribute("ratingTotalItems", ratingsPage.getTotalElements());
        model.addAttribute("ratingSize", safeRatingSize);
        model.addAttribute("ratingKeyword", ratingKeyword == null ? "" : ratingKeyword);
        model.addAttribute("ratingAverage", ratingService.getAverageRating(event));
        model.addAttribute("ratingCount", ratingService.countRatings(event));

        boolean forecastAvailable = weatherService.isForecastAvailable(event.getEventTime());
        if (forecastAvailable) {
            try {
                WeatherResponse weatherResponse = weatherService.getEventWeather(
                        event.getLatitude(),
                        event.getLongitude(),
                        event.getEventTime());
                WeatherDto weatherDto = weatherService.mapToDto(weatherResponse, event.getEventTime());
                model.addAttribute("weatherDto", weatherDto);
            } catch (Exception e) {
                System.err.println("Weather API failed: " + e.getMessage());
            }
        }

        return "events/event-detail";
    }

    @PostMapping("/{id}/join")
    public String joinEvent(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal AccountUserDetails details,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (details == null || details.getUser() == null) {
            request.getSession(true).setAttribute("loginRedirect", "/events/" + id);
            return "redirect:/login?redirect=/events/" + id;
        }
        // Admins cannot join events
        if (hasAuthority(details, "ADMIN")) {
            redirectAttributes.addFlashAttribute("error", "Admins k\u00f6nnen nicht an Events teilnehmen.");
            return "redirect:/events/" + id;
        }
        if (eventParticipantService.existsParticipant(event, details.getUser())) {
            redirectAttributes.addFlashAttribute("info", "Du nimmst bereits teil.");
            return "redirect:/events/" + id;
        }
        if (event.getMaxParticipants() != null
                && eventParticipantService.countParticipants(event) >= event.getMaxParticipants()) {
            redirectAttributes.addFlashAttribute("error", "Dieses Event ist bereits ausgebucht.");
            return "redirect:/events/" + id;
        }
        if (event.getCosts() > 0) {
            return "redirect:/events/" + id + "/payments";
        }

        LocalDateTime now = LocalDateTime.now();

        EventParticipant participant = new EventParticipant();
        participant.setEvent(event);
        participant.setUser(details.getUser());
        participant.setOrganizer(false);
        participant.setJoinedAt(now);
        eventParticipantService.createParticipant(participant);

        // join chat room for event
        ChatMembership chatMembership = new ChatMembership();
        chatMembership.setChatRoom(event.getEventChatRoom());
        chatMembership.setUser(details.getUser());
        chatMembership.setRole(ChatMembershipRole.MEMBER);
        chatMembership.setJoinedAt(now);
        chatMembershipService.createChatMembership(chatMembership);

        redirectAttributes.addFlashAttribute(
                "success",
                "Du hast dich zum Event \"" + event.getName() + "\" angemeldet.");
        return "redirect:/events/" + id;
    }

    @PostMapping("/{id}/ratings")
    public String saveRating(
            @PathVariable("id") Long id,
            @RequestParam("stars") int stars,
            @RequestParam(name = "comment", required = false) String comment,
            @AuthenticationPrincipal AccountUserDetails details,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (details == null || details.getUser() == null) {
            request.getSession(true).setAttribute("loginRedirect", "/events/" + id);
            return "redirect:/login?redirect=/events/" + id;
        }
        if (!eventParticipantService.existsParticipant(event, details.getUser())) {
            redirectAttributes.addFlashAttribute("error", "Nur Teilnehmer können bewerten.");
            return "redirect:/events/" + id;
        }
        if (stars < 1 || stars > 5) {
            redirectAttributes.addFlashAttribute("error", "Bitte wähle 1 bis 5 Sterne.");
            return "redirect:/events/" + id;
        }
        Rating rating = ratingService.getRatingByEventAndUser(event, details.getUser())
                .orElseGet(Rating::new);
        rating.setEvent(event);
        rating.setUser(details.getUser());
        rating.setStars(stars);
        rating.setComment(comment == null || comment.isBlank() ? null : comment.trim());
        if (rating.getId() == null) {
            ratingService.createRating(rating);
        } else {
            ratingService.updateRating(rating);
        }
        redirectAttributes.addFlashAttribute("success", "Bewertung gespeichert.");
        return "redirect:/events/" + id;
    }

    @PostMapping("/{id}/ratings/delete")
    public String deleteRating(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal AccountUserDetails details,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (details == null || details.getUser() == null) {
            request.getSession(true).setAttribute("loginRedirect", "/events/" + id);
            return "redirect:/login?redirect=/events/" + id;
        }
        Rating rating = ratingService.getRatingByEventAndUser(event, details.getUser()).orElse(null);
        if (rating != null) {
            ratingService.deleteRating(rating);
            redirectAttributes.addFlashAttribute("success", "Bewertung gelöscht.");
        }
        return "redirect:/events/" + id;
    }

    @PostMapping("/{id}/invite")
    public String inviteFriend(
            @PathVariable("id") Long id,
            @RequestParam(name = "friendIds", required = false) List<Long> friendIds,
            @RequestParam(name = "redirect", required = false) String redirect,
            @AuthenticationPrincipal AccountUserDetails details,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        if (details == null || details.getUser() == null) {
            request.getSession(true).setAttribute("loginRedirect", "/events/" + id);
            return "redirect:/login?redirect=/events/" + id;
        }
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        List<User> friends = getFriends(details.getUser());
        if (friendIds == null || friendIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Bitte mindestens einen Freund auswählen.");
            return buildSafeRedirect(redirect, id);
        }
        int sentCount = 0;
        for (Long friendId : friendIds) {
            User friend = friends.stream()
                    .filter(user -> user.getId().equals(friendId))
                    .findFirst()
                    .orElse(null);
            if (friend != null) {
                EventInvitation invitation = eventInvitationService
                        .getInvitationByEventAndInvitee(event, friend)
                        .orElse(null);
                if (invitation == null) {
                    invitation = new EventInvitation();
                    invitation.setEvent(event);
                    invitation.setInviter(details.getUser());
                    invitation.setInvitee(friend);
                    invitation.setStatus(EventInvitationStatus.PENDING);
                    eventInvitationService.createInvitation(invitation);
                } else if (invitation.getStatus() == EventInvitationStatus.DECLINED) {
                    invitation.setStatus(EventInvitationStatus.PENDING);
                    invitation.setRespondedAt(null);
                    eventInvitationService.updateInvitation(invitation);
                }
                if (emailService.sendEventInvitation(friend, event, details.getUser())) {
                    sentCount++;
                }
            }
        }
        if (sentCount > 0) {
            redirectAttributes.addFlashAttribute("success", "Einladung versendet an " + sentCount + " Freund(e).");
        } else {
            redirectAttributes.addFlashAttribute("error", "Einladung konnte nicht versendet werden.");
        }
        return buildSafeRedirect(redirect, id);
    }

    @PostMapping("/{id}/leave")
    public String leaveEvent(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal AccountUserDetails details,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (details == null || details.getUser() == null) {
            request.getSession(true).setAttribute("loginRedirect", "/events/" + id);
            return "redirect:/login?redirect=/events/" + id;
        }
        if (event.getOrganizer() != null && event.getOrganizer().equals(details.getUser())) {
            redirectAttributes.addFlashAttribute("error", "Organisatoren können sich nicht abmelden.");
            return "redirect:/events/" + id;
        }
        if (!eventParticipantService.existsParticipant(event, details.getUser())) {
            redirectAttributes.addFlashAttribute("info", "Du bist nicht angemeldet.");
            return "redirect:/events/" + id;
        }
        eventParticipantService.deleteParticipant(event, details.getUser());

        // delete chat membership
        chatMembershipService.deleteChatMembershipByChatRoomAndUser(event.getEventChatRoom(), details.getUser());

        redirectAttributes.addFlashAttribute(
                "success",
                "Du hast dich vom Event \"" + event.getName() + "\" abgemeldet.");
        return "redirect:/events/" + id;
    }

    @GetMapping("/{id}/participants/pdf")
    public ResponseEntity<byte[]> downloadParticipantsPdf(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal AccountUserDetails details,
            HttpServletRequest request) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (details == null || details.getUser() == null) {
            request.getSession(true).setAttribute("loginRedirect", "/events/" + id);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, "/login?redirect=/events/" + id)
                    .build();
        }
        boolean isOrganizer = event.getOrganizer() != null && event.getOrganizer().equals(details.getUser());
        boolean isAdmin = hasAuthority(details, "ADMIN");
        if (!isOrganizer && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<EventParticipant> participants = eventParticipantService.getAllParticipants(event);
        participants.sort((a, b) -> {
            int orgCompare = Boolean.compare(b.isOrganizer(), a.isOrganizer());
            if (orgCompare != 0) {
                return orgCompare;
            }
            if (a.getJoinedAt() == null || b.getJoinedAt() == null) {
                return 0;
            }
            return b.getJoinedAt().compareTo(a.getJoinedAt());
        });
        byte[] pdfBytes = pdfService.generateEventParticipantsPdf(event, participants);
        String filename = "participants-event-" + id + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @PostMapping("/{id}/cancel")
    public String cancelEvent(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal AccountUserDetails details,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        Event event = eventService.getEventById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (details == null || details.getUser() == null) {
            request.getSession(true).setAttribute("loginRedirect", "/events/" + id);
            return "redirect:/login?redirect=/events/" + id;
        }
        boolean isOrganizer = event.getOrganizer() != null && event.getOrganizer().equals(details.getUser());
        boolean isAdmin = hasAuthority(details, "ADMIN");
        if (!isOrganizer && !isAdmin) {
            redirectAttributes.addFlashAttribute("error", "Nur der Organisator oder Admin kann das Event absagen.");
            return "redirect:/events/" + id;
        }
        User organizer = event.getOrganizer() != null ? event.getOrganizer() : details.getUser();

        List<EventParticipant> participants = eventParticipantService.getAllParticipants(event);
        String message = "Das Event '" + event.getName() + "' wurde leider abgesagt.";
        String link = "/events";
        // send email and in app notification for event cancellation
        for (EventParticipant participant : participants) {
            emailService.sendEventCancellation(participant.getUser(), event, details.getUser());
            notificationService.createNotification(participant.getId(), NotificationType.EVENT_CANCELLATION, message,
                    link);
        }

        // set payment event to null
        for (Payment payment : event.getPayments()) {
            payment.setEvent(null);
        }
        event.getPayments().clear();

        eventInvitationService.deleteInvitationsByEvent(event);
        ratingService.deleteRatingsByEvent(event);

        eventParticipantService.deleteParticipants(event);
        eventService.deleteEvent(event);
        redirectAttributes.addFlashAttribute("success", "Event wurde abgesagt.");
        return "redirect:/events";
    }

    @PostMapping("/{id}/toggleFavourite")
    public String toggleFavouriteStatus(@PathVariable("id") Long id,
            @AuthenticationPrincipal AccountUserDetails details) {
        User user = details.getUser();
        Event event = eventService.getEventById(id).get();

        boolean isFavourite = eventFavouriteService.isEventFavourite(event, user);
        if (isFavourite) {
            eventFavouriteService.deleteEventFavourite(event, user);
        } else {
            eventFavouriteService.addEventFavourite(event, user);
        }

        return "redirect:/events/" + id;
    }

    private String buildSafeRedirect(String redirect, Long eventId) {
        if (redirect != null && redirect.startsWith("/")) {
            return "redirect:" + redirect;
        }
        return "redirect:/events/" + eventId;
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

    private boolean hasAuthority(AccountUserDetails details, String authority) {
        if (details == null || details.getAuthorities() == null || authority == null) {
            return false;
        }
        return details.getAuthorities().stream()
                .anyMatch(granted -> granted.getAuthority().equalsIgnoreCase(authority));
    }
}
