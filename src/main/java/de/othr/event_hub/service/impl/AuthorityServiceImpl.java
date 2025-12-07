package de.othr.event_hub.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import de.othr.event_hub.model.Authority;
import de.othr.event_hub.repository.AuthorityRepository;
import de.othr.event_hub.service.AuthorityService;

@Service
public class AuthorityServiceImpl implements AuthorityService {
    private AuthorityRepository authorityRepository;

    public AuthorityServiceImpl(AuthorityRepository authorityRepository) {
        super();
        this.authorityRepository = authorityRepository;
    }

    @Override
    public List<Authority> getAllAuthorities() {
        // TODO Auto-generated method stub
        return (List<Authority>) authorityRepository.findAll();
    }

    @Override
    public Authority getAuthorityById(Long id) {
        // TODO Auto-generated method stub
        return authorityRepository.findById(id).get();
    }

    @Override
    public Authority getAuthorityByDescription(String description) {
        return authorityRepository.findByDescription(description).get();
    }
}
