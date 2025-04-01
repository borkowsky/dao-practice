package net.rewerk.dbrest.helper;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Getter
public class ConfigLoader {
    private static final String CONFIG_FILE = "app.properties";
    private final Properties properties = new Properties();
    private static ConfigLoader instance;

    public static ConfigLoader getInstance() {
        if (instance == null) {
            instance = new ConfigLoader();
        }
        return instance;
    }

    private ConfigLoader() {
        parseConfig();
    }

    private void parseConfig() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to load config file: " + CONFIG_FILE);
        }
    }
}
