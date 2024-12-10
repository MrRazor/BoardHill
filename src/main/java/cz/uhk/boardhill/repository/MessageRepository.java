package cz.uhk.boardhill.repository;

import cz.uhk.boardhill.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message,Long> {
    @Query("SELECT m FROM Message m WHERE m.chat.name = :chatName AND m.chat.isDeleted = false AND m.isDeleted = false")
    Page<Message> findAllByChatName(String chatName, Pageable pageable);
}
