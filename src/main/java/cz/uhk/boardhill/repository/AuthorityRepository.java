package cz.uhk.boardhill.repository;

import cz.uhk.boardhill.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority,Long> {

    List<Authority> findByUserUsername(String username);

}
