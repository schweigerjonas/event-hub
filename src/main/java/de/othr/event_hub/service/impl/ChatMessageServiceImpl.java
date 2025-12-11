package de.othr.event_hub.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.othr.event_hub.model.ChatMessage;
import de.othr.event_hub.model.ChatRoom;
import de.othr.event_hub.repository.ChatMessageRepository;
import de.othr.event_hub.service.ChatMessageService;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Override
    public ChatMessage createChatMessage(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }

    @Override
    public List<ChatMessage> getAllChatMessages() {
        return chatMessageRepository.findAll();
    }

    @Override
    public Optional<ChatMessage> getChatMessageById(Long id) {
        return chatMessageRepository.findById(id);
    }

    @Override
    public ChatMessage updateChatMessage(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }

    @Override
    public void deleteChatMessage(ChatMessage chatMessage) {
        chatMessageRepository.delete(chatMessage);
    }

    @Override
    public void deleteAllChatMessages() {
        chatMessageRepository.deleteAll();
    }

    @Override
    public List<ChatMessage> getChatMessagesByChatRoom(ChatRoom chatRoom) {
        return chatMessageRepository.findByChatRoomOrderBySentAtAsc(chatRoom);
    }
}
