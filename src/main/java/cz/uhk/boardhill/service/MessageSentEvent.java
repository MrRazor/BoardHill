package cz.uhk.boardhill.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MessageSentEvent {
    private final String chatId;
}
