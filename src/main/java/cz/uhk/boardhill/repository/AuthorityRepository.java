package cz.uhk.boardhill.repository;

import cz.uhk.boardhill.entity.Authority;
import cz.uhk.boardhill.entity.AuthorityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, AuthorityId> {
    List<Authority> findByUsername(String username);
}
