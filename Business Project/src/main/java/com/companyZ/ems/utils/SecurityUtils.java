package com.companyZ.ems.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for security-related operations.
 * Provides methods for password hashing and verification.
 */
public class SecurityUtils {
    private static final Logger logger = LogManager.getLogger(SecurityUtils.class);
    private static final int SALT_ROUNDS = 12;

    /**
     * Hashes a password using BCrypt.
     *
     * @param password The plain text password to hash
     * @return The hashed password
     */
    public static String hashPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(password, BCrypt.gensalt(SALT_ROUNDS));
    }

    /**
     * Verifies a password against its hash.
     *
     * @param password       The plain text password to verify
     * @param hashedPassword The hashed password to check against
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        if (password == null || hashedPassword == null) {
            logger.warn("Password verification failed: null password or hash");
            return false;
        }
        try {
            return BCrypt.checkpw(password, hashedPassword);
        } catch (Exception e) {
            logger.error("Error verifying password", e);
            return false;
        }
    }

    /**
     * Validates password strength.
     *
     * @param password The password to validate
     * @return true if the password meets strength requirements
     */
    public static boolean validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c))
                hasUpperCase = true;
            else if (Character.isLowerCase(c))
                hasLowerCase = true;
            else if (Character.isDigit(c))
                hasDigit = true;
            else
                hasSpecialChar = true;
        }

        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }

    /**
     * Sanitizes input to prevent SQL injection.
     *
     * @param input The input to sanitize
     * @return The sanitized input
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("[';\"\\\\]", "");
    }
}