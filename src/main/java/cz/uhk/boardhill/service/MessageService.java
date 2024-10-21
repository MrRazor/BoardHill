package cz.uhk.boardhill.service;

import cz.uhk.boardhill.entity.Message;
import cz.uhk.boardhill.repository.MessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService implements ServiceInterface<Message, Long> {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
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

    public Page<Message> getLatestMessages(Long chatId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return messageRepository.findByChatIdOrderByCreatedAtDesc(chatId, pageable);
    }
}