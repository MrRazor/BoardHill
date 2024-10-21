package cz.uhk.boardhill.service;

import cz.uhk.boardhill.entity.Authority;
import cz.uhk.boardhill.repository.AuthorityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorityService {

    private final AuthorityRepository authorityRepository;

    public AuthorityService(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    public List<Authority> findAll() {
        return authorityRepository.findAll();
    }

    public Optional<Authority> findById(Long id) {
        return authorityRepository.findById(id);
    }

    public Authority save(Authority authority) {
        return authorityRepository.save(authority);
    }

    public void deleteById(Long id) {
        authorityRepository.deleteById(id);
    }
}