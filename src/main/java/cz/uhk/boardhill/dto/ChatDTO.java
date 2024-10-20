package cz.uhk.boardhill.dto;

import cz.uhk.boardhill.entity.ChatUser;
import cz.uhk.boardhill.entity.Message;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class ChatDTO {

    private Long id;

    private boolean isGlobal;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Set<Message> messages;

    private Set<ChatUser> chatUsers;

}
