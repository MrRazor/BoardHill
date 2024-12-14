package cz.uhk.boardhill.repository;

import cz.uhk.boardhill.entity.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {
    Optional<ChatUser> findByChatNameAndUserUsername(String chatName, String userName);
}
