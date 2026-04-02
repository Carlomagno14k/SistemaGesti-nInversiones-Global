package co.edu.uptc.repository;

import java.util.List;

public interface Repository<T> {
    void save(T entity);
    List<T> findAll();
    void replaceAll(List<T> data);
}
