package com.companyZ.ems.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class for managing database connections in the Employee Management
 * System.
 * Provides centralized database connection management with proper error
 * handling
 * and logging.
 * 
 * Note: In a production environment, connection pooling should be implemented,
 * and credentials should be stored in a secure configuration file.
 */
public class DatabaseConnection {
    private static final Logger logger = LogManager.getLogger(DatabaseConnection.class);

    // Database connection parameters
    // TODO: Move these to a secure configuration file
    private static final String URL = "jdbc:mysql://localhost:3306/company_z_ems";
    private static final String USER = "root";
    private static final String PASSWORD = "pass";

    /**
     * Establishes and returns a connection to the MySQL database.
     * Each call creates a new connection - in production, use connection pooling.
     *
     * @return A Connection object representing the database connection
     * @throws Exception if the connection cannot be established
     */
    public static Connection getConnection() throws Exception {
        try {
            // Attempt to establish database connection
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            logger.debug("Database connection established successfully");
            return conn;
        } catch (Exception e) {
            logger.error("Failed to establish database connection", e);
            throw new Exception("Database connection failed: " + e.getMessage(), e);
        }
    }

    /**
     * Safely closes a database connection.
     * Should be called in a finally block after database operations.
     *
     * @param conn The Connection object to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                logger.debug("Database connection closed successfully");
            } catch (Exception e) {
                logger.error("Error closing database connection", e);
            }
        }
    }
}