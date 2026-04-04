package co.edu.uptc.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {

    void save(T entity);
    List<T> findAll();
    Optional<T> findById(String id);
    void update(T entity);
    void deleteById(String id);
    void replaceAll(List<T> data);
}