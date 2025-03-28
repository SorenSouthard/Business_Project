package com.companyZ.ems.services;

import com.companyZ.ems.utils.DatabaseConnection;
import com.companyZ.ems.utils.SecurityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Service class for handling user authentication.
 * Provides methods for user verification and role management.
 */
public class AuthenticationService {
    private static final Logger logger = LogManager.getLogger(AuthenticationService.class);

    /**
     * Authenticates a user with the given credentials.
     *
     * @param username The username to authenticate
     * @param password The password to verify
     * @return true if authentication is successful, false otherwise
     * @throws Exception if database operation fails
     */
    public boolean authenticate(String username, String password) throws Exception {
        String sql = "SELECT password_hash, role FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    return SecurityUtils.verifyPassword(password, storedHash);
                }
                return false;
            }
        }
    }

    /**
     * Retrieves the role of a user.
     *
     * @param username The username to look up
     * @return The user's role (ADMIN or EMPLOYEE)
     * @throws Exception if database operation fails
     */
    public String getUserRole(String username) throws Exception {
        String sql = "SELECT role FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
                throw new Exception("User not found");
            }
        }
    }

    /**
     * Creates a new user account.
     *
     * @param username The username for the new account
     * @param password The password for the new account
     * @param role     The role for the new account (ADMIN or EMPLOYEE)
     * @throws Exception if database operation fails or user already exists
     */
    public void createUser(String username, String password, String role) throws Exception {
        String sql = "INSERT INTO users (user_id, username, password_hash, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            String userId = generateUserId();
            String passwordHash = SecurityUtils.hashPassword(password);

            stmt.setString(1, userId);
            stmt.setString(2, username);
            stmt.setString(3, passwordHash);
            stmt.setString(4, role);

            stmt.executeUpdate();
            logger.info("Created new user account: {}", username);
        }
    }

    /**
     * Generates a unique user ID.
     *
     * @return A unique user ID
     */
    private String generateUserId() {
        return "USER" + System.currentTimeMillis() + (int) (Math.random() * 1000);
    }

    public String getEmployeeId(String username) {
        String sql = "SELECT empid FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("empid");
                }
            }
        } catch (Exception e) {
            logger.error("Error retrieving employee ID", e);
            throw new RuntimeException("Failed to get employee ID", e);
        }

        return null;
    }
}