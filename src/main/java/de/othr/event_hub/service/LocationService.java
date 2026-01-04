package de.othr.event_hub.service;

public interface LocationService {

    boolean isLocationValid(String location);

    java.util.Optional<LocationCoordinates> findCoordinates(String location);
}
