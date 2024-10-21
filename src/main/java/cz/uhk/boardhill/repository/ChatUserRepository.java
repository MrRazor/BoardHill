package cz.uhk.boardhill.repository;

import cz.uhk.boardhill.entity.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatUserRepository extends JpaRepository<ChatUser,Long> {

}
