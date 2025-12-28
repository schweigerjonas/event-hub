package de.othr.event_hub.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.EventParticipant;
import de.othr.event_hub.model.User;
import de.othr.event_hub.repository.EventParticipantRepository;
import de.othr.event_hub.service.EventParticipantService;

@Service
public class EventParticipantServiceImpl implements EventParticipantService {

    @Autowired
    private EventParticipantRepository eventParticipantRepository;

    @Override
    public EventParticipant createParticipant(EventParticipant participant) {
        return eventParticipantRepository.save(participant);
    }

    @Override
    public Page<EventParticipant> getParticipants(Event event, Pageable pageable) {
        return eventParticipantRepository.findByEvent(event, pageable);
    }

    @Override
    public boolean existsParticipant(Event event, User user) {
        return eventParticipantRepository.existsByEventAndUser(event, user);
    }

    @Override
    public long countParticipants(Event event) {
        return eventParticipantRepository.countByEvent(event);
    }

    @Override
    @Transactional
    public void deleteParticipant(Event event, User user) {
        eventParticipantRepository.deleteByEventAndUser(event, user);
    }
}
