package de.othr.event_hub.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.othr.event_hub.model.Event;
import de.othr.event_hub.model.User;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(String name, String location, Pageable pageable);

    // favourites for a user
    @Query("SELECT e FROM Event e JOIN e.favourites f WHERE f.user = :user")
    Page<Event> findAllFavouritesOfUser(User user, Pageable pageable);

    // favourites filtered by name and location
    @Query("""
        SELECT e FROM Event e 
        JOIN e.favourites f 
        WHERE f.user = :user 
        AND LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')) 
        AND LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%'))
    """)
    Page<Event> findFavouritesByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(String name, String location, User user, Pageable pageable);

    // events where the user participates
    @Query("SELECT DISTINCT e FROM Event e JOIN EventParticipant p ON p.event = e WHERE p.user = :user")
    Page<Event> findByParticipant(User user, Pageable pageable);

    // participant events filtered by keyword
    @Query("""
        SELECT DISTINCT e FROM Event e
        JOIN EventParticipant p ON p.event = e
        WHERE p.user = :user
        AND (
            LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(e.location) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
    """)
    Page<Event> searchByParticipant(User user, String keyword, Pageable pageable);
}
