package cz.uhk.boardhill.service;

import cz.uhk.boardhill.entity.Chat;
import cz.uhk.boardhill.repository.ChatRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatService implements ServiceInterface<Chat, Long> {

    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
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