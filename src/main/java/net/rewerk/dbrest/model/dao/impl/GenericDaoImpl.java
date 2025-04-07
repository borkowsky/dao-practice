package net.rewerk.dbrest.model.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import net.rewerk.dbrest.helper.ConnectionManager;
import net.rewerk.dbrest.model.dao.GenericDao;

import java.util.ArrayList;
import java.util.List;

public abstract class GenericDaoImpl<T> implements GenericDao<T> {
    private final Class<T> entityClass;

    public GenericDaoImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T save(T entity) {
        if (entity == null) return null;
        T result = null;
        EntityManager entityManager = ConnectionManager.getInstance().getEntityManager();
        try {
            entityManager.getTransaction().begin();
            T merged = entityManager.merge(entity);
            entityManager.getTransaction().commit();
            result = merged;
        } catch (Exception e) {
            System.out.println("[GenericDaoImpl] save() Exception: " + e.getMessage());
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
                System.out.println("[GenericDaoImpl] save() Transaction rolled back");
            }
        } finally {
            entityManager.close();
        }
        return result;
    }

    @Override
    public boolean delete(Long id) {
        if (id == null) return false;
        boolean result = false;
        EntityManager entityManager = ConnectionManager.getInstance().getEntityManager();
        try {
            T entity = entityManager.find(entityClass, id);
            if (entity != null) {
                entityManager.getTransaction().begin();
                entityManager.remove(entity);
                entityManager.getTransaction().commit();
                result = true;
            }
            entityManager.close();
        } catch (Exception e) {
            System.out.println("[GenericDaoImpl] delete() Exception: " + e.getMessage());
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
                System.out.println("[GenericDaoImpl] delete() Transaction rolled back");
            }
        } finally {
            entityManager.close();
        }
        return result;
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) return false;
        boolean result = false;
        try {
            EntityManager entityManager = ConnectionManager.getInstance().getEntityManager();
            result = entityManager.find(entityClass, id) != null;
            entityManager.close();
        } catch (Exception e) {
            System.out.println("[GenericDaoImpl] existsById() Exception: " + e.getMessage());
        }
        return result;
    }

    @Override
    public List<T> findAll() {
        List<T> result = new ArrayList<>();
        try (EntityManager entityManager = ConnectionManager.getInstance().getEntityManager()) {
            CriteriaQuery<T> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(entityClass);
            Root<T> root = criteriaQuery.from(entityClass);
            criteriaQuery.select(root);
            TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);
            result = typedQuery.getResultList();
            return result;
        } catch (Exception e) {
            System.out.println("[GenericDaoImpl] findAll() Exception: " + e.getMessage());
        }
        return result;
    }

    @Override
    public T getById(Long id) {
        T result = null;
        try {
            EntityManager entityManager = ConnectionManager.getInstance().getEntityManager();
            result = entityManager.find(entityClass, id);
        } catch (Exception e) {
            System.out.println("[GenericDaoImpl] getById() Exception: " + e.getMessage());
        }
        return result;
    }

    @Override
    public List<T> findByIds(List<Long> ids) {
        List<T> result = new ArrayList<>();
        try (EntityManager entityManager = ConnectionManager.getInstance().getEntityManager()) {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
            Root<T> root = criteriaQuery.from(entityClass);
            criteriaQuery.select(root);
            criteriaQuery.where(criteriaBuilder.in(root.get("id")).value(ids));
            TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);
            result = typedQuery.getResultList();
            return result;
        } catch (Exception e) {
            System.out.println("[GenericDaoImpl] findByIds() Exception: " + e.getMessage());
        }
        return result;
    }
}
