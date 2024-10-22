package cz.uhk.boardhill.service;

import cz.uhk.boardhill.entity.Chat;
import cz.uhk.boardhill.repository.ChatRepository;
import cz.uhk.boardhill.repository.ChatUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatService implements ServiceInterface<Chat, Long> {

    private final ChatRepository chatRepository;
    private final ChatUserRepository chatUserRepository;

    public ChatService(ChatRepository chatRepository, ChatUserRepository chatUserRepository) {
        this.chatRepository = chatRepository;
        this.chatUserRepository = chatUserRepository;
    }

    public List<Chat> findAll() {
        return chatRepository.findAll();
    }

    public Optional<Chat> findById(Long id) {
        return chatRepository.findById(id);
    }

    public Chat save(Chat chat) {
        return chatRepository.save(chat);
    }

    public void deleteById(Long id) {
        chatRepository.deleteById(id);
    }
}