package de.othr.event_hub.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.EventFavourite;
import de.othr.event_hub.model.User;
import de.othr.event_hub.repository.EventFavouriteRepository;
import de.othr.event_hub.service.EventFavouriteService;

@Service
public class EventFavouriteServiceImpl implements EventFavouriteService {

    @Autowired
    private EventFavouriteRepository eventFavouriteRepository;

    @Override
    public boolean isEventFavourite(Event event, User user) {
        EventFavourite eventFavourite = eventFavouriteRepository.findByEventAndUser(event, user);
        return eventFavourite != null; 
    }

    @Override
    public EventFavourite addEventFavourite(Event event, User user) {
        EventFavourite eventFavourite = new EventFavourite();
        eventFavourite.setEvent(event);
        eventFavourite.setUser(user);
        return eventFavouriteRepository.save(eventFavourite);
    }

    @Override
    public void deleteEventFavourite(Event event, User user) {
        EventFavourite eventFavourite = eventFavouriteRepository.findByEventAndUser(event, user);
        eventFavouriteRepository.delete(eventFavourite);
    }
}
