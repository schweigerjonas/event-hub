package de.othr.event_hub.api;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.othr.event_hub.dto.EventApiDto;
import de.othr.event_hub.dto.EventParticipantApiDto;
import de.othr.event_hub.model.ChatMembership;
import de.othr.event_hub.model.ChatRoom;
import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.EventParticipant;
import de.othr.event_hub.model.Payment;
import de.othr.event_hub.model.User;
import de.othr.event_hub.model.enums.ChatMembershipRole;
import de.othr.event_hub.model.enums.ChatRoomType;
import de.othr.event_hub.service.EventParticipantService;
import de.othr.event_hub.service.EventInvitationService;
import de.othr.event_hub.service.EventService;
import de.othr.event_hub.service.ChatMembershipService;
import de.othr.event_hub.service.ChatRoomService;
import de.othr.event_hub.service.LocationCoordinates;
import de.othr.event_hub.service.LocationService;
import de.othr.event_hub.service.RatingService;
import de.othr.event_hub.service.UserService;

@RestController
@RequestMapping("/api/events")
public class EventApiController {

    private final EventService eventService;
    private final EventParticipantService participantService;
    private final EventInvitationService eventInvitationService;
    private final RatingService ratingService;
    private final ChatRoomService chatRoomService;
    private final ChatMembershipService chatMembershipService;
    private final UserService userService;
    private final LocationService locationService;

    public EventApiController(
        EventService eventService,
        EventParticipantService participantService,
        EventInvitationService eventInvitationService,
        RatingService ratingService,
        ChatRoomService chatRoomService,
        ChatMembershipService chatMembershipService,
        UserService userService,
        LocationService locationService
    ) {
        this.eventService = eventService;
        this.participantService = participantService;
        this.eventInvitationService = eventInvitationService;
        this.ratingService = ratingService;
        this.chatRoomService = chatRoomService;
        this.chatMembershipService = chatMembershipService;
        this.userService = userService;
        this.locationService = locationService;
    }

    @GetMapping
    public ResponseEntity<List<EventApiDto>> getEvents(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false, defaultValue = "time") String sort,
        @RequestParam(required = false, defaultValue = "asc") String direction,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int size
    ) {
        // map sort param to entity field
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
        // normalize paging params
        int safePage = Math.max(page, 1);
        int safeSize = size < 1 ? 10 : size;
        Pageable pageable = PageRequest.of(safePage - 1, safeSize, Sort.by(sortDirection, sortField));
        Page<Event> events = eventService.getEvents(keyword, pageable);
        List<EventApiDto> dtos = events.getContent().stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/all")
    public ResponseEntity<List<EventApiDto>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        List<EventApiDto> dtos = events.stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventApiDto> getEvent(@PathVariable Long id) {
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(eventOpt.get()));
    }

    @PostMapping
    public ResponseEntity<EventApiDto> createEvent(@RequestBody EventApiDto dto) {
        if (!canCreateEvents()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            return ResponseEntity.badRequest().header("X-Error", "name_required").build();
        }
        if (dto.getLocation() == null || dto.getLocation().isBlank()) {
            return ResponseEntity.badRequest().header("X-Error", "location_required").build();
        }
        if (dto.getDurationMinutes() == null || dto.getDurationMinutes() <= 0) {
            return ResponseEntity.badRequest().header("X-Error", "duration_invalid").build();
        }
        if (dto.getMaxParticipants() == null || dto.getMaxParticipants() <= 0) {
            return ResponseEntity.badRequest().header("X-Error", "max_participants_invalid").build();
        }
        if (dto.getCosts() < 0) {
            return ResponseEntity.badRequest().header("X-Error", "costs_invalid").build();
        }
        if (dto.getEventTime() == null || dto.getEventTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().header("X-Error", "event_time_invalid").build();
        }
        // validate location before persisting
        String rawLocation = dto.getLocation();
        LocationCoordinates coordinates = locationService.findCoordinates(rawLocation).orElse(null);
        if (coordinates == null) {
            return ResponseEntity.badRequest().header("X-Error", "location_not_found").build();
        }
        // only admin can set another organizer
        User organizer = resolveOrganizer(dto.getOrganizerId());
        if (organizer == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Event event = new Event();
        event.setName(dto.getName());
        event.setLocation(formatLocation(rawLocation));
        event.setLatitude(coordinates.latitude());
        event.setLongitude(coordinates.longitude());
        event.setDurationMinutes(dto.getDurationMinutes());
        event.setMaxParticipants(dto.getMaxParticipants());
        event.setDescription(dto.getDescription());
        event.setEventTime(dto.getEventTime());
        event.setCosts(dto.getCosts());
        event.setOrganizer(organizer);
        Event created = eventService.createEvent(event);
        EventParticipant organizerParticipant = new EventParticipant();
        organizerParticipant.setEvent(created);
        organizerParticipant.setUser(organizer);
        organizerParticipant.setOrganizer(true);
        organizerParticipant.setJoinedAt(LocalDateTime.now());
        participantService.createParticipant(organizerParticipant);

        LocalDateTime now = LocalDateTime.now();
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setType(ChatRoomType.EVENT);
        chatRoom.setOwner(organizer);
        chatRoom.setCreatedAt(now);
        chatRoom.setEvent(created);
        chatRoom = chatRoomService.createChatRoom(chatRoom);

        created.setEventChatRoom(chatRoom);
        eventService.updateEvent(created);

        ChatMembership chatMembership = new ChatMembership();
        chatMembership.setChatRoom(chatRoom);
        chatMembership.setUser(organizer);
        chatMembership.setRole(ChatMembershipRole.CHATADMIN);
        chatMembership.setJoinedAt(now);
        chatMembershipService.createChatMembership(chatMembership);
        return ResponseEntity.ok(toDto(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventApiDto> updateEvent(@PathVariable Long id, @RequestBody EventApiDto dto) {
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Event event = eventOpt.get();
        if (!canModifyEvent(event)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            return ResponseEntity.badRequest().header("X-Error", "name_required").build();
        }
        if (dto.getLocation() == null || dto.getLocation().isBlank()) {
            return ResponseEntity.badRequest().header("X-Error", "location_required").build();
        }
        if (dto.getDurationMinutes() == null || dto.getDurationMinutes() <= 0) {
            return ResponseEntity.badRequest().header("X-Error", "duration_invalid").build();
        }
        if (dto.getMaxParticipants() == null || dto.getMaxParticipants() <= 0) {
            return ResponseEntity.badRequest().header("X-Error", "max_participants_invalid").build();
        }
        long currentParticipants = participantService.countParticipants(event);
        if (dto.getMaxParticipants() < currentParticipants) {
            return ResponseEntity.badRequest().header("X-Error", "max_participants_too_small").build();
        }
        if (dto.getCosts() < 0) {
            return ResponseEntity.badRequest().header("X-Error", "costs_invalid").build();
        }
        if (dto.getEventTime() == null || dto.getEventTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().header("X-Error", "event_time_invalid").build();
        }
        // re-validate location on update
        String rawLocation = dto.getLocation();
        LocationCoordinates coordinates = locationService.findCoordinates(rawLocation).orElse(null);
        if (coordinates == null) {
            return ResponseEntity.badRequest().header("X-Error", "location_not_found").build();
        }
        event.setName(dto.getName());
        event.setLocation(formatLocation(rawLocation));
        event.setLatitude(coordinates.latitude());
        event.setLongitude(coordinates.longitude());
        event.setDurationMinutes(dto.getDurationMinutes());
        event.setMaxParticipants(dto.getMaxParticipants());
        event.setDescription(dto.getDescription());
        event.setEventTime(dto.getEventTime());
        event.setCosts(dto.getCosts());
        if (dto.getOrganizerId() != null && isAdmin()) {
            event.setOrganizer(userService.getUserById(dto.getOrganizerId()));
        }
        Event updated = eventService.updateEvent(event);
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Event event = eventOpt.get();
        if (!canModifyEvent(event)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (event.getPayments() != null) {
            for (Payment payment : event.getPayments()) {
                payment.setEvent(null);
            }
            event.getPayments().clear();
        }
        eventInvitationService.deleteInvitationsByEvent(event);
        ratingService.deleteRatingsByEvent(event);
        participantService.deleteParticipants(event);
        eventService.deleteEvent(event);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<EventParticipantApiDto>> getParticipants(
        @PathVariable Long id,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int size
    ) {
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User currentUser = getCurrentUser();
        if (!canViewParticipants(eventOpt.get(), currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // keep organizer on top of participant list
        int safePage = Math.max(page, 1);
        int safeSize = size < 1 ? 10 : size;
        Pageable pageable = PageRequest.of(safePage - 1, safeSize, Sort.by(Sort.Direction.DESC, "organizer").and(Sort.by(Sort.Direction.DESC, "joinedAt")));
        Page<EventParticipant> participants = participantService.getParticipants(eventOpt.get(), pageable);
        List<EventParticipantApiDto> dtos = participants.getContent().stream().map(this::toParticipantDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}/participants/all")
    public ResponseEntity<List<EventParticipantApiDto>> getAllParticipants(@PathVariable Long id) {
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User currentUser = getCurrentUser();
        if (!canViewParticipants(eventOpt.get(), currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<EventParticipant> participants = participantService.getAllParticipants(eventOpt.get());
        List<EventParticipantApiDto> dtos = participants.stream().map(this::toParticipantDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{id}/participants")
    public ResponseEntity<EventParticipantApiDto> addParticipant(
        @PathVariable Long id,
        @RequestParam Long userId
    ) {
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Event event = eventOpt.get();
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (!isAdmin() && !currentUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (event.getCosts() > 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).header("X-Error", "payment_required").build();
        }
        if (event.getMaxParticipants() != null
                && participantService.countParticipants(event) >= event.getMaxParticipants()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).header("X-Error", "event_full").build();
        }
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        // skip duplicate participants
        if (participantService.existsParticipant(event, user)) {
            return ResponseEntity.ok().build();
        }
        EventParticipant participant = new EventParticipant();
        participant.setEvent(event);
        participant.setUser(user);
        participant.setOrganizer(false);
        participant.setJoinedAt(java.time.LocalDateTime.now());
        EventParticipant created = participantService.createParticipant(participant);
        return ResponseEntity.ok(toParticipantDto(created));
    }

    @DeleteMapping("/{id}/participants/{participantId}")
    public ResponseEntity<Void> deleteParticipant(
        @PathVariable Long id,
        @PathVariable Long participantId
    ) {
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Event event = eventOpt.get();
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        EventParticipant participant = participantService.getAllParticipants(event).stream()
            .filter(p -> p.getId().equals(participantId))
            .findFirst()
            .orElse(null);
        if (participant == null) {
            return ResponseEntity.notFound().build();
        }
        if (!canModifyEvent(event) && (participant.getUser() == null || !participant.getUser().equals(currentUser))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        participantService.deleteParticipant(event, participant.getUser());
        return ResponseEntity.noContent().build();
    }

    private boolean canViewParticipants(Event event, User currentUser) {
        if (currentUser == null) {
            return false;
        }
        if (isAdmin()) {
            return true;
        }
        if (event.getOrganizer() != null && event.getOrganizer().equals(currentUser)) {
            return true;
        }
        return participantService.existsParticipant(event, currentUser);
    }

    private EventApiDto toDto(Event event) {
        return new EventApiDto(
            event.getId(),
            event.getName(),
            event.getLocation(),
            event.getDurationMinutes(),
            event.getMaxParticipants(),
            event.getDescription(),
            event.getEventTime(),
            event.getCosts(),
            event.getOrganizer() != null ? event.getOrganizer().getId() : null
        );
    }

    private EventParticipantApiDto toParticipantDto(EventParticipant participant) {
        return new EventParticipantApiDto(
            participant.getId(),
            participant.getEvent() != null ? participant.getEvent().getId() : null,
            participant.getUser() != null ? participant.getUser().getId() : null,
            participant.getUser() != null ? participant.getUser().getUsername() : null,
            participant.isOrganizer(),
            participant.getJoinedAt()
        );
    }

    private User resolveOrganizer(Long organizerId) {
        // admin can assign organizer explicitly
        if (isAdmin() && organizerId != null) {
            return userService.getUserById(organizerId);
        }
        return getCurrentUser();
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        return userService.getUserByUsername(auth.getName());
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        return auth.getAuthorities().stream().anyMatch(a -> "ADMIN".equals(a.getAuthority()));
    }

    private boolean canCreateEvents() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        // only admin or organizer can create events
        return auth.getAuthorities().stream()
            .anyMatch(a -> "ADMIN".equals(a.getAuthority()) || "ORGANISATOR".equals(a.getAuthority()));
    }

    private boolean canModifyEvent(Event event) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        boolean isAdmin = auth.getAuthorities().stream()
            .anyMatch(a -> "ADMIN".equals(a.getAuthority()));
        if (isAdmin) {
            return true;
        }
        // organizer can only modify own events
        boolean isOrganizerRole = auth.getAuthorities().stream()
            .anyMatch(a -> "ORGANISATOR".equals(a.getAuthority()));
        if (!isOrganizerRole) {
            return false;
        }
        if (event.getOrganizer() == null || event.getOrganizer().getUsername() == null) {
            return false;
        }
        User currentUser = userService.getUserByUsername(auth.getName());
        return currentUser != null && event.getOrganizer().equals(currentUser);
    }

    private String formatLocation(String location) {
        if (location == null) {
            return null;
        }
        String trimmed = location.trim().replaceAll("\\s+", " ");
        if (trimmed.isBlank()) {
            return trimmed;
        }
        String[] parts = trimmed.split(" ");
        List<String> formatted = new ArrayList<>(parts.length);
        for (String part : parts) {
            formatted.add(formatLocationToken(part));
        }
        return String.join(" ", formatted).trim();
    }

    private String formatLocationToken(String token) {
        if (token.isBlank()) {
            return token;
        }
        if (token.chars().anyMatch(Character::isDigit)) {
            return token;
        }
        String[] parts = token.split("-");
        if (parts.length > 1) {
            List<String> formatted = new ArrayList<>(parts.length);
            for (String part : parts) {
                formatted.add(capitalizeWord(part));
            }
            return String.join("-", formatted);
        }
        return capitalizeWord(token);
    }

    private String capitalizeWord(String word) {
        if (word.isBlank()) {
            return word;
        }
        int start = 0;
        int end = word.length();
        while (start < end && !Character.isLetter(word.charAt(start))) {
            start++;
        }
        while (end > start && !Character.isLetter(word.charAt(end - 1))) {
            end--;
        }
        if (start >= end) {
            return word;
        }
        String prefix = word.substring(0, start);
        String suffix = word.substring(end);
        String core = word.substring(start, end).toLowerCase(Locale.GERMAN);
        String capitalized = Character.toUpperCase(core.charAt(0)) + core.substring(1);
        return prefix + capitalized + suffix;
    }
}
