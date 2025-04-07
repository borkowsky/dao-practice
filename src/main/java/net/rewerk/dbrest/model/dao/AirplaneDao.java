package net.rewerk.dbrest.model.dao;

import net.rewerk.dbrest.model.entity.Airplane;

import java.util.List;

public interface AirplaneDao {
    Airplane save(Airplane airplane);

    boolean delete(Long id);

    boolean existsById(Long id);

    Airplane getById(Long id);

    List<Airplane> findByIds(List<Long> ids);

    List<Airplane> findAll();
}
