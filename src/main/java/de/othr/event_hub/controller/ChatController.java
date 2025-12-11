package de.othr.event_hub.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import de.othr.event_hub.config.AccountUserDetails;
import de.othr.event_hub.model.ChatRoom;
import de.othr.event_hub.model.User;
import de.othr.event_hub.service.ChatMembershipService;
import de.othr.event_hub.service.ChatMessageService;
import de.othr.event_hub.service.ChatRoomService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
@RequestMapping("/chats")
public class ChatController {

    private ChatMembershipService chatMembershipService;
    private ChatMessageService chatMessageService;
    private ChatRoomService chatRoomService;

    public ChatController(ChatMembershipService chatMembershipService, ChatMessageService chatMessageService, ChatRoomService chatRoomService) {
        super();
        this.chatMembershipService = chatMembershipService;
        this.chatMessageService = chatMessageService;
        this.chatRoomService = chatRoomService;
    }

    @GetMapping("/all")
    public String getChatsOfUser(Model model, @AuthenticationPrincipal AccountUserDetails details) {
        User user = details.getUser();
        model.addAttribute("userChats", chatMembershipService.getChatMembershipsByUser(user));
        return "chats/chats-all";
    }

    @GetMapping("/{id}")
    public String getChatMessages(Model model, @PathVariable("id") Long id, @AuthenticationPrincipal AccountUserDetails details) {
        User user = details.getUser();
        ChatRoom chatRoom = chatRoomService.getChatRoomById(id).get();
        model.addAttribute("currentUser", user);
        model.addAttribute("chatRoom", chatRoom);
        model.addAttribute("messages", chatMessageService.getChatMessagesByChatRoom(chatRoom));
        return "chats/messages";
    }
}
