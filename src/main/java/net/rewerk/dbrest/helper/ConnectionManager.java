package net.rewerk.dbrest.helper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class ConnectionManager {
    private static ConnectionManager instance;
    private final EntityManagerFactory entityManagerFactory;

    public static ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    private ConnectionManager() {
        entityManagerFactory = Persistence.createEntityManagerFactory("default");
    }

    public EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public void closeConnections() {
        entityManagerFactory.close();
    }
}
