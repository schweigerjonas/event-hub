package de.othr.event_hub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import de.othr.event_hub.model.ChatMembership;
import de.othr.event_hub.model.ChatRoom;
import de.othr.event_hub.model.User;

public interface ChatMembershipRepository extends JpaRepository<ChatMembership, Long> {

    List<ChatMembership> findByUser(User user);

    @Modifying
    void deleteByChatRoomAndUser(ChatRoom chatRoom, User user);

    List<ChatMembership> findByChatRoom(ChatRoom chatRoom);

    @Query(
        value = """
            SELECT cm.*
            FROM chat_memberships cm
            INNER JOIN (
                SELECT chatroom_id, MIN(id) as min_id
                FROM chat_memberships
                GROUP BY chatroom_id
            ) sub ON cm.id = sub.min_id
        """,
        nativeQuery = true
    )
    List<ChatMembership> getOneChatMembershipPerChatRoom();
}
