package cz.uhk.boardhill.repository;

import cz.uhk.boardhill.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
    @Query("SELECT u FROM User u JOIN u.chatUsers cu WHERE cu.chat.name = :name")
    List<User> findAllByChat(String name, Sort sort);
}
