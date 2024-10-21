package cz.uhk.boardhill.service;

import cz.uhk.boardhill.entity.Authority;
import cz.uhk.boardhill.entity.ChatUser;
import cz.uhk.boardhill.repository.ChatUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatUserService implements ServiceInterface<ChatUser, Long> {

    private final ChatUserRepository chatUserRepository;

    public ChatUserService(ChatUserRepository chatUserRepository) {
        this.chatUserRepository = chatUserRepository;
    }

    public List<ChatUser> findAll() {
        return chatUserRepository.findAll();
    }

    public Optional<ChatUser> findById(Long id) {
        return chatUserRepository.findById(id);
    }

    public ChatUser save(ChatUser chatUser) {
        return chatUserRepository.save(chatUser);
    }

    public void deleteById(Long id) {
        chatUserRepository.deleteById(id);
    }
}