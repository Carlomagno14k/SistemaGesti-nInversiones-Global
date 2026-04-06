package co.edu.uptc.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface Repository<T> {
    void save(T entity);
    List<T> findAll();
    // Usamos Predicate para que la lógica le diga cómo buscar el ID
    Optional<T> findBy(Predicate<T> condition); 
    void deleteBy(Predicate<T> condition);
    void replaceAll(List<T> data);
}