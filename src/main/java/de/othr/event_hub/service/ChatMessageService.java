package de.othr.event_hub.service;

import java.util.List;
import java.util.Optional;

import de.othr.event_hub.model.ChatMessage;
import de.othr.event_hub.model.ChatRoom;

public interface ChatMessageService {
    
    ChatMessage createChatMessage(ChatMessage chatMessage);

    List<ChatMessage> getAllChatMessages();

    Optional<ChatMessage> getChatMessageById(Long id);

    ChatMessage updateChatMessage(ChatMessage chatMessage);

    void deleteChatMessage(ChatMessage chatMessage);

    void deleteAllChatMessages();

    List<ChatMessage> getChatMessagesByChatRoom(ChatRoom chatRoom);
}
