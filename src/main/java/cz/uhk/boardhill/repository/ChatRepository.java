package cz.uhk.boardhill.repository;

import cz.uhk.boardhill.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat,String> {
    @Query("SELECT c FROM Chat c JOIN c.chatUsers cu WHERE cu.user.username = :username")
    Page<Chat> findAllByUser(String username, Pageable pageable);
    Page<Chat> findAllByOwnerUsername(String username, Pageable pageable);
}
