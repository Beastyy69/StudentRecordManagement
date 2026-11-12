package com.srms.db;

import com.srms.util.AppConfig;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Creates JDBC connections and makes sure the students table exists. */
public final class DBConnection {

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        String url = AppConfig.get("db.url");
        String user = AppConfig.get("db.user");
        String pass = AppConfig.get("db.password");
        if (user.isEmpty()) {
            return DriverManager.getConnection(url);
        }
        return DriverManager.getConnection(url, user, pass);
    }

    /**
     * Loads the driver, opens one connection and creates the table.
     * Throws if the driver jar is missing or the DB cannot be reached.
     */
    public static void initialize() throws ClassNotFoundException, SQLException {
        Class.forName(AppConfig.get("db.driver"));

        // SQLite creates the file itself, but the folder must exist.
        String url = AppConfig.get("db.url");
        if (url.startsWith("jdbc:sqlite:")) {
            File dbFile = new File(url.substring("jdbc:sqlite:".length()));
            File parent = dbFile.getAbsoluteFile().getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
        }

        String ddl = "CREATE TABLE IF NOT EXISTS students ("
                + " roll_no    VARCHAR(15)  PRIMARY KEY,"
                + " name       VARCHAR(50)  NOT NULL,"
                + " course     VARCHAR(50)  NOT NULL,"
                + " study_year INT          NOT NULL,"
                + " email      VARCHAR(100) NOT NULL UNIQUE,"
                + " phone      VARCHAR(10)  NOT NULL,"
                + " gender     VARCHAR(10)  NOT NULL"
                + ")";
        try (Connection con = getConnection(); Statement st = con.createStatement()) {
            st.executeUpdate(ddl);
        }
    }
}
