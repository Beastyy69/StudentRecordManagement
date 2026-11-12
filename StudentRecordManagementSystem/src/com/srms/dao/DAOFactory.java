package com.srms.dao;

import com.srms.db.DBConnection;
import com.srms.exception.DataAccessException;
import com.srms.util.AppConfig;

/**
 * Chooses the storage implementation based on config/app.properties:
 *   storage.type = jdbc  -> database only (error if driver/DB missing)
 *   storage.type = file  -> serialized file only
 *   storage.type = auto  -> try database, fall back to file (default)
 */
public final class DAOFactory {

    private DAOFactory() {
    }

    public static StudentDAO create() throws DataAccessException {
        String type = AppConfig.get("storage.type").toLowerCase();

        if ("file".equals(type)) {
            return new FileStudentDAO(AppConfig.get("file.path"));
        }

        try {
            DBConnection.initialize();
            return new JdbcStudentDAO();
        } catch (ClassNotFoundException e) {
            if ("jdbc".equals(type)) {
                throw new DataAccessException("JDBC driver not found: " + AppConfig.get("db.driver")
                        + ". Put the driver .jar in the lib folder.", e);
            }
            System.out.println("[DAOFactory] JDBC driver not found, falling back to file storage.");
        } catch (Exception e) {
            if ("jdbc".equals(type)) {
                throw new DataAccessException("Could not connect to database: " + e.getMessage(), e);
            }
            System.out.println("[DAOFactory] Database unavailable (" + e.getMessage()
                    + "), falling back to file storage.");
        }
        return new FileStudentDAO(AppConfig.get("file.path"));
    }
}
