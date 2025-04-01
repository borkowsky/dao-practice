package net.rewerk.dbrest.model.dao;

import net.rewerk.dbrest.model.entity.Location;

import java.util.List;

public interface LocationDao {
    Location save(Location location);

    boolean delete(Long id);

    boolean existsById(Long id);

    List<Location> findAll();

    Location getById(Long id);
}
