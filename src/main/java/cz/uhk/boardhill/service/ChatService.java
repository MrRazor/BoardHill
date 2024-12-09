package cz.uhk.boardhill.service;

import cz.uhk.boardhill.entity.*;
import cz.uhk.boardhill.repository.AuthorityRepository;
import cz.uhk.boardhill.repository.ChatRepository;
import cz.uhk.boardhill.repository.ChatUserRepository;
import cz.uhk.boardhill.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class ChatService implements ServiceInterface<Chat, String> {

    private final ChatRepository chatRepository;
    private final ChatUserRepository chatUserRepository;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    public ChatService(ChatRepository chatRepository, ChatUserRepository chatUserRepository, UserRepository userRepository, AuthorityRepository authorityRepository) {
        this.chatRepository = chatRepository;
        this.chatUserRepository = chatUserRepository;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    public List<Chat> findAll() {
        return chatRepository.findAll();
    }

    public Optional<Chat> findById(String id) {
        return chatRepository.findById(id);
    }

    public Chat save(Chat chat) {
        return chatRepository.save(chat);
    }

    public void deleteById(String id) {
        chatRepository.deleteById(id);
    }

    public List<Chat> findAllChats() {
        Sort sort = Sort.by("name").ascending();
        return chatRepository.findAll(sort);
    }

    public List<Chat> findAllNotDeletedChats() {
        Sort sort = Sort.by("name").ascending();
        return chatRepository.findAllByIsDeletedIsFalse(sort);
    }

    public List<Chat> findAllNotDeletedChatsByUser(String username) {
        Sort sort = Sort.by("name").ascending();
        return chatRepository.findAllByUser(username, sort);
    }

    public List<Chat> findAllNotDeletedChatsByOwner(String username) {
        Sort sort = Sort.by("name").ascending();
        return chatRepository.findAllByOwnerUsernameAndIsDeletedIsFalse(username, sort);
    }

    public Chat createChat(String name, String userId) {
        if (chatRepository.existsById(name)) {
            throw new IllegalArgumentException("Chat with the given name already exists");
        }
        if(!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User does not exist");
        }

        Chat chat = new Chat();
        chat.setName(name);
        chat.setOwner(userRepository.getReferenceById(userId));
        chat.setCreatedAt(ZonedDateTime.now(ZoneId.of("UTC")));
        chat.setDeleted(false);

        return chatRepository.save(chat);
    }

    public void deleteChat(Chat chat, String loggedInUserId) {
        List<Authority> loggedInAuthorities = authorityRepository.findByUserUsername(loggedInUserId);
        boolean isAdminLoggedIn = loggedInAuthorities.stream().anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"));
        if(!chat.isDeleted() && (chat.getOwner().getUsername().equals(loggedInUserId) || isAdminLoggedIn)) {
            chat.setDeleted(true);
            chatRepository.save(chat);
        }
        else {
            throw new IllegalStateException("Chat is already deleted or you do not have permissions to do this");
        }
    }

    public void addUserToChat(String chatName, String loggedInUserId, String userToAddId) {
        Chat chat = chatRepository.findById(chatName)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        if (!chat.getOwner().getUsername().equals(loggedInUserId)) {
            throw new IllegalStateException("Only the chat owner can add users");
        }

        boolean alreadyMember = chat.getChatUsers().stream()
                .anyMatch(chatUser -> chatUser.getUser().getUsername().equals(userToAddId));
        if (alreadyMember) {
            throw new IllegalArgumentException("User is already in the chat");
        }

        if(!userRepository.existsById(userToAddId)) {
            throw new IllegalArgumentException("User does not exist");
        }

        ChatUser chatUser = new ChatUser();
        chatUser.setChat(chat);
        chatUser.setUser(userRepository.getReferenceById(userToAddId));
        chatUser.setJoinedAt(ZonedDateTime.now());

        chatUserRepository.save(chatUser);
    }

    public void removeUserFromChat(String chatName, String loggedInUserId, String userToRemoveId) {
        if(loggedInUserId.equals(userToRemoveId)) {
            throw new IllegalArgumentException("Chat owner cannot be removed");
        }

        Chat chat = chatRepository.findById(chatName)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        if (!chat.getOwner().getUsername().equals(loggedInUserId)) {
            throw new IllegalStateException("Only the chat owner can remove users");
        }

        ChatUser chatUser = chatUserRepository.findByChatNameAndUserUsername(chatName, userToRemoveId)
                .orElseThrow(() -> new IllegalArgumentException("User is not part of this chat"));

        chatUserRepository.delete(chatUser);
    }

}