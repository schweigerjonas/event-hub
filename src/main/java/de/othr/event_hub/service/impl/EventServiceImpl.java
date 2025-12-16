package de.othr.event_hub.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.othr.event_hub.model.Event;
import de.othr.event_hub.repository.EventRepository;
import de.othr.event_hub.service.EventService;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;
    
    @Override
    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }
}
