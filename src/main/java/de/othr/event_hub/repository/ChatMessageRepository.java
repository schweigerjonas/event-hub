package de.othr.event_hub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.othr.event_hub.model.ChatMessage;
import de.othr.event_hub.model.ChatRoom;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    List<ChatMessage> findByChatRoomOrderBySentAtAsc(ChatRoom chatRoom);
}
