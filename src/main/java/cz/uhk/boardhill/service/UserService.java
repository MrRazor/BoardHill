package cz.uhk.boardhill.service;

import cz.uhk.boardhill.entity.Authority;
import cz.uhk.boardhill.entity.Chat;
import cz.uhk.boardhill.entity.User;
import cz.uhk.boardhill.repository.AuthorityRepository;
import cz.uhk.boardhill.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements ServiceInterface<User, String> {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    public UserService(UserRepository userRepository, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteById(String id) {
        userRepository.deleteById(id);
    }

    public void register(String username, String password) {
        if(!userRepository.existsById(username)) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setEnabled(true);
            user = userRepository.save(user);

            Authority authority = new Authority();
            authority.setUser(user);
            authority.setAuthority("ROLE_USER");
            authorityRepository.save(authority);
        }
        else {
            throw new IllegalArgumentException("Username already exists");
        }
    }

    public void changeUserEnabledStatus(String userId, boolean enabled, String loggedInUserId) {
        List<Authority> loggedInAuthorities = authorityRepository.findByUserUsername(loggedInUserId);
        boolean isAdminLoggedIn = loggedInAuthorities.stream().anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"));
        List<Authority> authorities = authorityRepository.findByUserUsername(userId);
        boolean isAdmin = authorities.stream().anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"));
        if(isAdminLoggedIn && !isAdmin) {
            if(userRepository.existsById(userId)) {
                User user = userRepository.getReferenceById(userId);
                user.setEnabled(enabled);
                userRepository.save(user);
            }
        }
        else {
            throw new IllegalStateException("You are not admin or other user is admin");
        }
    }

    public List<User> findAllUsers() {
        Sort sort = Sort.by("username").ascending();
        return userRepository.findAll(sort);
    }

    public List<Chat> findAllUsersByChat(String chatName) {
        Sort sort = Sort.by("username").ascending();
        return userRepository.findAllByChat(chatName, sort);
    }
}