package de.othr.event_hub.service;

import java.util.List;

import de.othr.event_hub.model.Authority;

public interface AuthorityService {
    List<Authority> getAllAuthorities();

    Authority getAuthorityById(Long id);

    Authority getAuthorityByDescription(String description);
}
