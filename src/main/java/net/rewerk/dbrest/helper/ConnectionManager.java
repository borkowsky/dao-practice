package net.rewerk.dbrest.helper;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {
    private static ConnectionManager instance;
    private final ComboPooledDataSource dataSource;

    public static ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    private ConnectionManager() {
        dataSource = initConnectionPool();
    }

    private ComboPooledDataSource initConnectionPool() {
        Properties config = ConfigLoader.getInstance().getProperties();
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setInitialPoolSize(5);
        dataSource.setMinPoolSize(5);
        dataSource.setMaxPoolSize(15);
        dataSource.setMaxIdleTime(30000);
        dataSource.setAcquireIncrement(5);
        dataSource.setIdleConnectionTestPeriod(30000);
        dataSource.setAcquireRetryAttempts(5);
        dataSource.setAcquireRetryDelay(3000);
        dataSource.setMaxStatements(200);
        dataSource.setJdbcUrl(config.getProperty("db.url"));
        dataSource.setUser(config.getProperty("db.user"));
        dataSource.setPassword(config.getProperty("db.password"));
        return dataSource;
    }

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Could not get connection", e);
        }
    }

    public void closeConnections() {
        if (dataSource != null) {
            dataSource.resetPoolManager();
            dataSource.close();
        }
    }
}
