package net.rewerk.dbrest.model.dao;

import net.rewerk.dbrest.model.entity.Luggage;

import java.util.List;

public interface LuggageDao {
    Luggage save(Luggage luggage);

    boolean delete(Long luggageId);

    boolean existsById(Long luggageId);

    Luggage getById(Long luggageId);

    List<Luggage> findByIds(List<Long> ids);

    List<Luggage> findAll();
}
