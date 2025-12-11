package de.othr.event_hub.service;

import java.util.List;
import java.util.Optional;

import de.othr.event_hub.model.ChatRoom;

public interface ChatRoomService {
    
    ChatRoom createChatRoom(ChatRoom chatRoom);

    List<ChatRoom> getAllChatRooms();

    Optional<ChatRoom> getChatRoomById(Long id);

    ChatRoom updateChatRoom(ChatRoom chatRoom);

    void deleteChatRoom(ChatRoom chatRoom);

    void deleteAllChatRooms();
}
