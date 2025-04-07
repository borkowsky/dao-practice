package net.rewerk.dbrest.model.dao;

import java.util.List;

public interface GenericDao<T> {
    T save(T location);

    boolean delete(Long id);

    boolean existsById(Long id);

    List<T> findByIds(List<Long> ids);

    List<T> findAll();

    T getById(Long id);
}
