package cz.uhk.boardhill.dto;

import cz.uhk.boardhill.entity.Chat;
import cz.uhk.boardhill.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatUserDTO {

    private Long id;

    private Chat chat;

    private User user;

    private LocalDateTime joinedAt;

    private LocalDateTime leftAt;

    private boolean isMember;

}
