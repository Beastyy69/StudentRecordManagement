package com.srms.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads settings from config/app.properties (next to the program).
 * Falls back to safe defaults if the file is missing.
 */
public final class AppConfig {

    private static final String CONFIG_FILE = "config/app.properties";
    private static final Properties PROPS = new Properties();

    static {
        // Defaults
        PROPS.setProperty("storage.type", "auto");
        PROPS.setProperty("db.driver", "org.sqlite.JDBC");
        PROPS.setProperty("db.url", "jdbc:sqlite:data/students.db");
        PROPS.setProperty("db.user", "");
        PROPS.setProperty("db.password", "");
        PROPS.setProperty("file.path", "data/students.dat");
        PROPS.setProperty("admin.username", "admin");
        // SHA-256 of "admin123"
        PROPS.setProperty("admin.passwordHash",
                "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9");

        try (InputStream in = new FileInputStream(CONFIG_FILE)) {
            PROPS.load(in);
        } catch (IOException e) {
            System.out.println("[AppConfig] " + CONFIG_FILE + " not found, using defaults.");
        }
    }

    private AppConfig() {
    }

    public static String get(String key) {
        return PROPS.getProperty(key, "").trim();
    }
}
