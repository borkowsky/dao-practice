package net.rewerk.dbrest.model.dao;

import net.rewerk.dbrest.model.entity.Route;

import java.util.List;

public interface RouteDao {
    Route save(Route route);

    boolean delete(Long id);

    boolean existsById(Long id);

    Route getById(Long id);

    List<Route> findByIds(List<Long> ids);

    List<Route> findAll();
}
