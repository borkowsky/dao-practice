package net.rewerk.dbrest.model.dao;

import net.rewerk.dbrest.model.entity.Staff;

import java.util.List;

public interface StaffDao {
    Staff save(Staff staff);

    boolean delete(Long id);

    boolean existsById(Long id);

    List<Staff> findAll();

    Staff getById(Long id);

    List<Staff> findByIds(List<Long> ids);
}
