package de.othr.event_hub.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatUpdateDTO {
    private String groupname;
    private List<String> memberIds = new ArrayList<>();
}
