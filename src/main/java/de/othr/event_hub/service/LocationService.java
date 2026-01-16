package de.othr.event_hub.service;

public interface LocationService {

    java.util.Optional<LocationCoordinates> findCoordinates(String location);
}
