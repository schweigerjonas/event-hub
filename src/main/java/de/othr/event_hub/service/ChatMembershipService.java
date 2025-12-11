package de.othr.event_hub.service;

import java.util.List;
import java.util.Optional;

import de.othr.event_hub.model.ChatMembership;
import de.othr.event_hub.model.User;

public interface ChatMembershipService {
    
    ChatMembership createChatMembership(ChatMembership chatMembership);

    List<ChatMembership> getAllChatMemberships();

    Optional<ChatMembership> getChatMembershipById(Long id);

    ChatMembership updateChatMembership(ChatMembership chatMembership);

    void deleteChatMembership(ChatMembership chatMembership);

    void deleteAllChatMemberships();

    List<ChatMembership> getChatMembershipsByUser(User user);
}
