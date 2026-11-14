package com.neelima.qa.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads environment-specific configuration properties.
 * Usage: pass -Denv=qa or -Denv=staging to Maven or rely on default 'qa'.
 * Any key can be overridden on the command line, e.g. -Dapi.username=alice.
 */
public class ConfigLoader {
    private static final Properties props = new Properties();

    static {
        load();
    }

    private static void load() {
        String env = System.getProperty("env", "qa").trim();
        String resource = "config-" + env + ".properties";
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
            if (is != null) {
                props.load(is);
            } else {
                // fallback to default config.properties
                try (InputStream is2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties")) {
                    if (is2 != null) props.load(is2);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config: " + resource, e);
        }
    }

    public static String get(String key) {
        return System.getProperty(key, props.getProperty(key));
    }

    public static String get(String key, String defaultValue) {
        String value = get(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
