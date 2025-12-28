package de.othr.event_hub.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public Page<Event> getEvents(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return eventRepository.findAll(pageable);
        }
        return eventRepository.findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(keyword, keyword, pageable);
    }
}
