package net.rewerk.dbrest.model.dao;

import net.rewerk.dbrest.model.entity.User;

import java.util.List;

public interface UserDao {
    User save(User user);

    boolean delete(User user);

    boolean existsById(Long id);

    User getById(Long id);

    List<User> findAll();

    List<User> findByUsername(String username);
}
