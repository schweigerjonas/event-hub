package de.othr.event_hub.api;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
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
import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.EventParticipant;
import de.othr.event_hub.model.User;
import de.othr.event_hub.service.EventParticipantService;
import de.othr.event_hub.service.EventService;
import de.othr.event_hub.service.UserService;

@RestController
@RequestMapping("/api/events")
public class EventApiController {

    private final EventService eventService;
    private final EventParticipantService participantService;
    private final UserService userService;

    public EventApiController(EventService eventService, EventParticipantService participantService, UserService userService) {
        this.eventService = eventService;
        this.participantService = participantService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<EventApiDto>> getEvents(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false, defaultValue = "time") String sort,
        @RequestParam(required = false, defaultValue = "asc") String direction,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int size
    ) {
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
        int safePage = Math.max(page, 1);
        int safeSize = size < 1 ? 10 : size;
        Pageable pageable = PageRequest.of(safePage - 1, safeSize, Sort.by(sortDirection, sortField));
        Page<Event> events = eventService.getEvents(keyword, pageable);
        List<EventApiDto> dtos = events.getContent().stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventApiDto> getEvent(@PathVariable("id") Long id) {
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(eventOpt.get()));
    }

    @PostMapping
    public ResponseEntity<EventApiDto> createEvent(@RequestBody EventApiDto dto) {
        User organizer = dto.getOrganizerId() == null ? null : userService.getUserById(dto.getOrganizerId());
        Event event = new Event();
        event.setName(dto.getName());
        event.setLocation(dto.getLocation());
        event.setDurationMinutes(dto.getDurationMinutes());
        event.setMaxParticipants(dto.getMaxParticipants());
        event.setDescription(dto.getDescription());
        event.setEventTime(dto.getEventTime());
        event.setCosts(dto.getCosts());
        event.setOrganizer(organizer);
        Event created = eventService.createEvent(event);
        return ResponseEntity.ok(toDto(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventApiDto> updateEvent(@PathVariable("id") Long id, @RequestBody EventApiDto dto) {
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Event event = eventOpt.get();
        event.setName(dto.getName());
        event.setLocation(dto.getLocation());
        event.setDurationMinutes(dto.getDurationMinutes());
        event.setMaxParticipants(dto.getMaxParticipants());
        event.setDescription(dto.getDescription());
        event.setEventTime(dto.getEventTime());
        event.setCosts(dto.getCosts());
        if (dto.getOrganizerId() != null) {
            event.setOrganizer(userService.getUserById(dto.getOrganizerId()));
        }
        Event updated = eventService.updateEvent(event);
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable("id") Long id) {
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        eventService.deleteEvent(eventOpt.get());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<EventParticipantApiDto>> getParticipants(
        @PathVariable("id") Long id,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int size
    ) {
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        int safePage = Math.max(page, 1);
        int safeSize = size < 1 ? 10 : size;
        Pageable pageable = PageRequest.of(safePage - 1, safeSize, Sort.by(Sort.Direction.DESC, "organizer").and(Sort.by(Sort.Direction.DESC, "joinedAt")));
        Page<EventParticipant> participants = participantService.getParticipants(eventOpt.get(), pageable);
        List<EventParticipantApiDto> dtos = participants.getContent().stream().map(this::toParticipantDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{id}/participants")
    public ResponseEntity<EventParticipantApiDto> addParticipant(
        @PathVariable("id") Long id,
        @RequestParam("userId") Long userId
    ) {
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Event event = eventOpt.get();
        User user = userService.getUserById(userId);
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
        @PathVariable("id") Long id,
        @PathVariable("participantId") Long participantId
    ) {
        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Event event = eventOpt.get();
        EventParticipant participant = participantService.getAllParticipants(event).stream()
            .filter(p -> p.getId().equals(participantId))
            .findFirst()
            .orElse(null);
        if (participant == null) {
            return ResponseEntity.notFound().build();
        }
        participantService.deleteParticipant(event, participant.getUser());
        return ResponseEntity.noContent().build();
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
}
