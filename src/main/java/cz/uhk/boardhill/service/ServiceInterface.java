package cz.uhk.boardhill.service;

import java.util.List;
import java.util.Optional;

public interface ServiceInterface<T, U> {

    List<T> findAll();

    Optional<T> findById(U id);

    T save(T entity);

    void deleteById(U id);

}