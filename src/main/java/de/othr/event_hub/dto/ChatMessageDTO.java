package de.othr.event_hub.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageDTO {
    private Long id;
    private String message;
    private String senderName;
    private Long senderId;
    private LocalDateTime sentAt;
    private boolean isDeleted = false;
}
