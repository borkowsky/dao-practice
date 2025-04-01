package net.rewerk.dbrest.model.dao;

import net.rewerk.dbrest.model.entity.StaffRole;

import java.util.List;

public interface StaffRoleDao {
    StaffRole save(StaffRole staffRole);

    boolean delete(Long id);

    boolean existsById(Long id);

    List<StaffRole> findAll();

    StaffRole getById(Long id);
}
