package de.othr.event_hub.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import de.othr.event_hub.config.AccountUserDetails;
import de.othr.event_hub.service.ChatMembershipService;
import de.othr.event_hub.service.ChatMessageService;
import de.othr.event_hub.service.ChatRoomService;
import org.springframework.web.bind.annotation.GetMapping;


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
    public String getChatsOfUser(@AuthenticationPrincipal AccountUserDetails details) {
        return "chats/chats-all";
    }
}
