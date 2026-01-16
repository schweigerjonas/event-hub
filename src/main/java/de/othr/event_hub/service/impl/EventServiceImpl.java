package de.othr.event_hub.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.User;
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
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public Page<Event> getEvents(String keyword, Pageable pageable) {
        // return full list when no search term
        if (keyword == null || keyword.isBlank()) {
            return eventRepository.findAll(pageable);
        }
        return eventRepository.findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(keyword, keyword, pageable);
    }

    @Override
    public Page<Event> getFavouriteEvents(String keyword, User user, Pageable pageable) {
        // search within favourite events when a term is provided
        if (keyword == null || keyword.isBlank()) {
            return eventRepository.findAllFavouritesOfUser(user, pageable);
        }
        return eventRepository.findFavouritesByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(keyword, keyword, user, pageable);
    }

    @Override
    public Page<Event> getParticipatingEvents(String keyword, User user, Pageable pageable) {
        // search within participated events when a term is provided
        if (keyword == null || keyword.isBlank()) {
            return eventRepository.findByParticipant(user, pageable);
        }
        return eventRepository.searchByParticipant(user, keyword, pageable);
    }

    @Override
    public void deleteEvent(Event event) {
        eventRepository.delete(event);
    }

    @Override
    public Event updateEvent(Event event) {
        return eventRepository.save(event);
    }
}
