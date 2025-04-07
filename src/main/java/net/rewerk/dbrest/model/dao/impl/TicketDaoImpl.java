package net.rewerk.dbrest.model.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import net.rewerk.dbrest.helper.ConnectionManager;
import net.rewerk.dbrest.model.dao.*;
import net.rewerk.dbrest.model.entity.*;

import java.util.ArrayList;
import java.util.List;

public class TicketDaoImpl extends GenericDaoImpl<Ticket> implements TicketDao {
    public TicketDaoImpl() {
        super(Ticket.class);
    }

    @Override
    public List<Ticket> findByUserId(Long userId) {
        List<Ticket> result = new ArrayList<>();
        EntityManager entityManager = ConnectionManager.getInstance().getEntityManager();
        try {
            TypedQuery<Ticket> query = entityManager.createNamedQuery("Ticket.findByUserId", Ticket.class);
            query.setParameter("userId", userId);
            result = query.getResultList();
        } catch (Exception e) {
            System.out.println("[TicketDaoImpl] findByUserId() Exception: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}
