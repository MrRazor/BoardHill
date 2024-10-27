package cz.uhk.boardhill.repository;

import cz.uhk.boardhill.entity.Chat;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat,String> {
    @Query("SELECT c FROM Chat c JOIN c.chatUsers cu WHERE cu.user.username = :username")
    List<Chat> findAllByUser(String username, Sort sort);
    List<Chat> findAllByOwnerUsername(String username, Sort sort);
}
