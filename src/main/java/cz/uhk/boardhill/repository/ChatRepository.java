package cz.uhk.boardhill.repository;

import cz.uhk.boardhill.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat,String> {

}
