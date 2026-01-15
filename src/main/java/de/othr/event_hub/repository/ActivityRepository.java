package de.othr.event_hub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.othr.event_hub.model.Activity;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

}
