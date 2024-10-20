package cz.uhk.boardhill.dto;

import cz.uhk.boardhill.entity.Chat;
import cz.uhk.boardhill.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDTO {

    private Long id;

    private Chat chat;

    private User user;

    private String content;

    private boolean isDeleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
