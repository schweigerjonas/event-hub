package de.othr.event_hub.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.EventInvitation;
import de.othr.event_hub.model.User;
import de.othr.event_hub.repository.EventInvitationRepository;
import de.othr.event_hub.service.EventInvitationService;

@Service
public class EventInvitationServiceImpl implements EventInvitationService {

    @Autowired
    private EventInvitationRepository eventInvitationRepository;

    @Override
    public EventInvitation createInvitation(EventInvitation invitation) {
        return eventInvitationRepository.save(invitation);
    }

    @Override
    public EventInvitation updateInvitation(EventInvitation invitation) {
        return eventInvitationRepository.save(invitation);
    }

    @Override
    public Optional<EventInvitation> getInvitationById(Long id) {
        return eventInvitationRepository.findById(id);
    }

    @Override
    public Optional<EventInvitation> getInvitationByEventAndInvitee(Event event, User invitee) {
        return eventInvitationRepository.findByEventAndInvitee(event, invitee);
    }

    @Override
    public Page<EventInvitation> getIncomingInvitations(User user, String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return eventInvitationRepository.findByInvitee(user, pageable);
        }
        return eventInvitationRepository.searchIncoming(user, keyword, pageable);
    }

    @Override
    public Page<EventInvitation> getOutgoingInvitations(User user, String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return eventInvitationRepository.findByInviter(user, pageable);
        }
        return eventInvitationRepository.searchOutgoing(user, keyword, pageable);
    }
}
