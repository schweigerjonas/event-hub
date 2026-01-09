package de.othr.event_hub.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.User;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(String name, String location, Pageable pageable);

    @Query("SELECT e FROM Event e JOIN e.favourites f WHERE f.user = :user")
    Page<Event> findAllFavouritesOfUser(User user, Pageable pageable);

    @Query("""
        SELECT e FROM Event e 
        JOIN e.favourites f 
        WHERE f.user = :user 
        AND LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')) 
        AND LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%'))
    """)
    Page<Event> findFavouritesByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(String name, String location, User user, Pageable pageable);
}
