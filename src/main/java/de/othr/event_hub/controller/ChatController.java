package de.othr.event_hub.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.othr.event_hub.config.AccountUserDetails;
import de.othr.event_hub.dto.ChatMessageDTO;
import de.othr.event_hub.dto.SendMessageDTO;
import de.othr.event_hub.model.ChatMembership;
import de.othr.event_hub.model.ChatMessage;
import de.othr.event_hub.model.ChatRoom;
import de.othr.event_hub.model.User;
import de.othr.event_hub.model.enums.ChatMembershipRole;
import de.othr.event_hub.model.enums.ChatRoomType;
import de.othr.event_hub.service.ChatMembershipService;
import de.othr.event_hub.service.ChatMessageService;
import de.othr.event_hub.service.ChatRoomService;
import de.othr.event_hub.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping("/chats")
public class ChatController {

    private ChatMembershipService chatMembershipService;
    private ChatMessageService chatMessageService;
    private ChatRoomService chatRoomService;
    private UserService userService;
    private SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatMembershipService chatMembershipService, ChatMessageService chatMessageService, ChatRoomService chatRoomService, SimpMessagingTemplate messagingTemplate, UserService userService) {
        super();
        this.chatMembershipService = chatMembershipService;
        this.chatMessageService = chatMessageService;
        this.chatRoomService = chatRoomService;
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
    }

    @GetMapping("/all")
    public String getChatsOfUser(Model model, @AuthenticationPrincipal AccountUserDetails details) {
        User user = details.getUser();
        model.addAttribute("userChats", chatMembershipService.getChatMembershipsByUser(user));
        return "chats/chats-all";
    }

    @PostMapping("/all")
    public String createChat(@RequestParam("groupname") String groupname, @AuthenticationPrincipal AccountUserDetails details) {
        User user = details.getUser();
        LocalDateTime now = LocalDateTime.now();

        // create chat room
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(groupname);
        chatRoom.setType(ChatRoomType.GROUP);
        chatRoom.setOwner(user);
        chatRoom.setCreatedAt(now);
        chatRoom =chatRoomService.createChatRoom(chatRoom);

        // configure chat membership
        ChatMembership chatMembership = new ChatMembership();
        chatMembership.setChatRoom(chatRoom);
        chatMembership.setUser(user);
        chatMembership.setRole(ChatMembershipRole.CHATADMIN);
        chatMembership.setJoinedAt(now);
        chatMembershipService.createChatMembership(chatMembership);

        return "redirect:/chats/all";
    }

    @PostMapping("/{id}/leave")
    public String leaveChat(@PathVariable("id") Long id, @AuthenticationPrincipal AccountUserDetails details) {
        User user = details.getUser();
        ChatRoom chatRoom = chatRoomService.getChatRoomById(id).get();
        if (chatRoom.getOwner().equals(user)) {
            chatRoomService.deleteChatRoom(chatRoom);
        }
        else {
            chatMembershipService.deleteChatMembershipByChatRoomAndUser(chatRoom, user);
        }
        return "redirect:/chats/all";
    }

    @GetMapping("/{id}")
    public String getChatMessages(
        Model model, 
        @PathVariable("id") Long id, 
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int size,
        @AuthenticationPrincipal AccountUserDetails details
    ) {
        User user = details.getUser();
        ChatRoom chatRoom = chatRoomService.getChatRoomById(id).get();
        // the first page is 1 for the user, 0 for the database
        // size * page => load more button works as expected, implementation like in the exercises is commented out
        // Pageable paging = PageRequest.of(page - 1, size);
        Pageable paging = PageRequest.of(0, size * page);
        Page<ChatMessage> pageMessages = chatMessageService.getChatMessagesByChatRoom(chatRoom, paging);
        // need to create seperate list bc pageMessages.getContent() is immutable
        List<ChatMessage> messages = new ArrayList<>(pageMessages.getContent());
        // reverse order so that latest messages are always on site 1
        Collections.reverse(messages);
        model.addAttribute("messages", messages);
        model.addAttribute("currentUser", user);
        model.addAttribute("chatRoom", chatRoom);
        // variables fo paginator
        model.addAttribute("entitytype", "chatMessage");
        // model.addAttribute("currentPage", pageMessages.getNumber() + 1);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalItems", pageMessages.getTotalElements());
        // model.addAttribute("totalPages", pageMessages.getTotalPages());
        model.addAttribute("pageSize", size);
        int totalPages = (int) Math.ceil((double) pageMessages.getTotalElements() / size);
        model.addAttribute("totalPages", totalPages);
        return "chats/messages";
    }

    @MessageMapping("/chat/{id}/send")
    public void sendChatMessage(@DestinationVariable long id, SendMessageDTO message) {
        User user = userService.getUserById(message.getUserId());
        ChatRoom chatRoom = chatRoomService.getChatRoomById(id).get();

        // save message in DB
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatRoom(chatRoom);
        chatMessage.setSender(user);
        chatMessage.setMessage(message.getMessage());
        chatMessage.setSentAt(java.time.LocalDateTime.now());
        chatMessageService.createChatMessage(chatMessage);

        // notify everyone in the chatroom
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setMessage(message.getMessage());
        dto.setSenderName(user.getUsername());
        dto.setSenderId(user.getId());
        dto.setSentAt(chatMessage.getSentAt());
        messagingTemplate.convertAndSend("/topic/chats/" + id, dto);
    }
}
