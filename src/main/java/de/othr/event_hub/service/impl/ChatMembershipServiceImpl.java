package de.othr.event_hub.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.othr.event_hub.model.ChatMembership;
import de.othr.event_hub.model.User;
import de.othr.event_hub.repository.ChatMembershipRepository;
import de.othr.event_hub.service.ChatMembershipService;

@Service
public class ChatMembershipServiceImpl implements ChatMembershipService {
    
    @Autowired
    private ChatMembershipRepository chatMembershipRepository;

    @Override
    public ChatMembership createChatMembership(ChatMembership chatMembership) {
        return chatMembershipRepository.save(chatMembership);
    }

    @Override
    public List<ChatMembership> getAllChatMemberships() {
        return chatMembershipRepository.findAll();
    }

    @Override
    public Optional<ChatMembership> getChatMembershipById(Long id) {
        return chatMembershipRepository.findById(id);
    }

    @Override
    public ChatMembership updateChatMembership(ChatMembership chatMembership) {
        return chatMembershipRepository.save(chatMembership);
    }

    @Override
    public void deleteChatMembership(ChatMembership chatMembership) {
        chatMembershipRepository.delete(chatMembership);
    }

    @Override
    public void deleteAllChatMemberships() {
        chatMembershipRepository.deleteAll();
    }

    @Override
    public List<ChatMembership> getChatMembershipsByUser(User user) {
        return chatMembershipRepository.findByUser(user);
    }
}
