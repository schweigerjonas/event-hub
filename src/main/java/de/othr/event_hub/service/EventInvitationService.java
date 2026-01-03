package de.othr.event_hub.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.EventInvitation;
import de.othr.event_hub.model.User;

public interface EventInvitationService {

    EventInvitation createInvitation(EventInvitation invitation);

    EventInvitation updateInvitation(EventInvitation invitation);

    Optional<EventInvitation> getInvitationById(Long id);

    Optional<EventInvitation> getInvitationByEventAndInvitee(Event event, User invitee);

    Page<EventInvitation> getIncomingInvitations(User user, String keyword, Pageable pageable);

    Page<EventInvitation> getOutgoingInvitations(User user, String keyword, Pageable pageable);
}
