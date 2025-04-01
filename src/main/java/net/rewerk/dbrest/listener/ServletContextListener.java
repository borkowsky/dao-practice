package net.rewerk.dbrest.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.annotation.WebListener;
import net.rewerk.dbrest.helper.ConfigLoader;
import net.rewerk.dbrest.helper.ConnectionManager;
import org.flywaydb.core.Flyway;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.Properties;

@WebListener
public class ServletContextListener implements jakarta.servlet.ServletContextListener {
    public void contextInitialized(ServletContextEvent sce) {
        Properties config = ConfigLoader.getInstance().getProperties();
        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(config.getProperty("db.url"), config.getProperty("db.user"), config.getProperty("db.password"))
                    .load();
            flyway.migrate();
        } catch (Exception e) {
            System.out.println("Flyway initialization failed");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        ConnectionManager.getInstance().closeConnections();
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                System.out.printf("Deregistered jdbc driver: %s%n", driver);
            } catch (Exception e) {
                System.out.printf("Error deregister jdbc driver: %s%n", driver);
            }
        }
    }
}
