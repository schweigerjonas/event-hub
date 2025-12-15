package de.othr.event_hub.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.othr.event_hub.model.ChatMessage;
import de.othr.event_hub.model.ChatRoom;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    Page<ChatMessage> findByChatRoomOrderBySentAtDesc(ChatRoom chatRoom, Pageable pageable);
}
