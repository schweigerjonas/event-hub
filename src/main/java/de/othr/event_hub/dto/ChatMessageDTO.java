package de.othr.event_hub.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private Long id;
    private String message;
    private String senderName;
    private Long senderId;
    private LocalDateTime sentAt;
    private boolean isDeleted = false;
}
