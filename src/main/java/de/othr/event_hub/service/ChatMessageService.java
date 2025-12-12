package de.othr.event_hub.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.othr.event_hub.model.ChatMessage;
import de.othr.event_hub.model.ChatRoom;

public interface ChatMessageService {
    
    ChatMessage createChatMessage(ChatMessage chatMessage);

    List<ChatMessage> getAllChatMessages();

    Optional<ChatMessage> getChatMessageById(Long id);

    ChatMessage updateChatMessage(ChatMessage chatMessage);

    void deleteChatMessage(ChatMessage chatMessage);

    void deleteAllChatMessages();

    Page<ChatMessage> getChatMessagesByChatRoom(ChatRoom chatRoom, Pageable pageable);
}
