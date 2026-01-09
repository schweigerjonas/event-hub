package de.othr.event_hub.service;

import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.EventFavourite;
import de.othr.event_hub.model.User;

public interface EventFavouriteService {
    boolean isEventFavourite(Event event, User user);

    EventFavourite addEventFavourite(Event event, User user);

    void deleteEventFavourite(Event event, User user);
}
