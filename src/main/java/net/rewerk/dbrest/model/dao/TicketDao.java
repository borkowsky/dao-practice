package net.rewerk.dbrest.model.dao;

import net.rewerk.dbrest.model.entity.Ticket;

import java.util.List;

public interface TicketDao {
    Ticket save(Ticket ticket);

    boolean delete(Long id);

    boolean existsById(Long id);

    Ticket getById(Long id);

    List<Ticket> findAll();

    List<Ticket> findByUserId(Long userId);
}
