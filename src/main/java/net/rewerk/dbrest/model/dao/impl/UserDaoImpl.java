package net.rewerk.dbrest.model.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import net.rewerk.dbrest.helper.ConnectionManager;
import net.rewerk.dbrest.model.dao.UserDao;
import net.rewerk.dbrest.model.entity.User;

import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl extends GenericDaoImpl<User> implements UserDao {
    public UserDaoImpl() {
        super(User.class);
    }

    @Override
    public List<User> findByUsername(String username) {
        List<User> result = new ArrayList<>();
        try (EntityManager entityManager = ConnectionManager.getInstance().getEntityManager()) {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<User> cq = cb.createQuery(User.class);
            Root<User> root = cq.from(User.class);
            cq.select(root).where(cb.equal(root.get("username"), username));
            result = entityManager.createQuery(cq).getResultList();
        } catch (Exception e) {
            System.out.println("[UserDaoImpl] findByUsername() Exception: " + e.getMessage());
        }
        return result;
    }
}
