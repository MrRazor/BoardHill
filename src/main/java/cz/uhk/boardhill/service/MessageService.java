package cz.uhk.boardhill.service;

import cz.uhk.boardhill.entity.Authority;
import cz.uhk.boardhill.entity.Message;
import cz.uhk.boardhill.repository.AuthorityRepository;
import cz.uhk.boardhill.repository.ChatRepository;
import cz.uhk.boardhill.repository.MessageRepository;
import cz.uhk.boardhill.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class MessageService implements ServiceInterface<Message, Long> {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    public MessageService(MessageRepository messageRepository, ChatRepository chatRepository, UserRepository userRepository, AuthorityRepository authorityRepository) {
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    public Optional<Message> findById(Long id) {
        return messageRepository.findById(id);
    }

    public Message save(Message message) {
        return messageRepository.save(message);
    }

    public void deleteById(Long id) {
        messageRepository.deleteById(id);
    }

    public Page<Message> getMessages(String chatId, int page, int size) {
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return messageRepository.findAllByChatNameAndIsDeleted(chatId, false, pageable);
    }

    public void createMessage(String chatId, String userId, String content) {
        Message message = new Message();
        message.setCreatedAt(ZonedDateTime.now(ZoneId.of("UTC")));
        if(chatRepository.existsById(chatId)) {
            message.setChat(chatRepository.getReferenceById(chatId));
        }
        else {
            throw new IllegalArgumentException("Chat does not exist");
        }
        if(userRepository.existsById(userId)) {
            message.setUser(userRepository.getReferenceById(userId));
        }
        else {
            throw new IllegalArgumentException("User does not exist");
        }
        message.setContent(content);
        message.setDeleted(false);
        messageRepository.save(message);
    }

    public void deleteMessage(Long messageId, String loggedInUserId) {
        List<Authority> loggedInAuthorities = authorityRepository.findByUserUsername(loggedInUserId);
        boolean isAdminLoggedIn = loggedInAuthorities.stream().anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));

        if(!message.isDeleted() && (message.getUser().getUsername().equals(loggedInUserId) || message.getChat().getOwner().getUsername().equals((loggedInUserId)) || isAdminLoggedIn)) {
            message.setDeleted(true);
            messageRepository.save(message);
        }
        else {
            throw new IllegalStateException("Message is already deleted or you do not have permissions to do this");
        }
    }
}