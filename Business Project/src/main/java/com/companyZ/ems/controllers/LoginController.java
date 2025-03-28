package com.companyZ.ems.controllers;

import com.companyZ.ems.services.AuthenticationService;
import com.companyZ.ems.utils.SecurityUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Controller class for handling user login functionality.
 * Manages user authentication and navigation to appropriate dashboards.
 */
public class LoginController {
    private static final Logger logger = LogManager.getLogger(LoginController.class);
    private final AuthenticationService authService = new AuthenticationService();

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button clearButton;

    /**
     * Initializes the login controller.
     * Sets up event handlers and initial state.
     */
    @FXML
    public void initialize() {
        // Add input validation listeners
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                usernameField.setStyle("");
            }
        });

        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                passwordField.setStyle("");
            }
        });

        // Add enter key support for login
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                handleLogin();
            }
        });
    }

    /**
     * Handles the login button click event.
     * Validates input and attempts user authentication.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Input validation
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            highlightEmptyFields(username, password);
            return;
        }

        try {
            // Attempt login
            boolean isAuthenticated = authService.authenticate(username, password);

            if (isAuthenticated) {
                String role = authService.getUserRole(username);
                logger.info("User {} logged in successfully with role {}", username, role);
                navigateToDashboard(role);
            } else {
                logger.warn("Failed login attempt for user: {}", username);
                showError("Invalid username or password");
                highlightInvalidCredentials();
            }
        } catch (Exception e) {
            logger.error("Error during login for user: " + username, e);
            showError("An error occurred during login. Please try again.");
        }
    }

    /**
     * Handles the clear button click event.
     * Resets all input fields.
     */
    @FXML
    private void handleClear() {
        usernameField.clear();
        passwordField.clear();
        usernameField.setStyle("");
        passwordField.setStyle("");
    }

    /**
     * Navigates to the appropriate dashboard based on user role.
     *
     * @param role The user's role (ADMIN or EMPLOYEE)
     */
    private void navigateToDashboard(String role) {
        try {
            String fxmlPath = role.equals("ADMIN") ? "/fxml/AdminDashboard.fxml" : "/fxml/EmployeeDashboard.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Company Z - " + role + " Dashboard");
            stage.show();

            logger.info("Navigated to {} dashboard", role);
        } catch (IOException e) {
            logger.error("Error loading dashboard for role: " + role, e);
            showError("Error loading dashboard. Please try again.");
        }
    }

    /**
     * Shows an error message in an alert dialog.
     *
     * @param message The error message to display
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Highlights empty input fields in red.
     */
    private void highlightEmptyFields(String username, String password) {
        if (username.isEmpty()) {
            usernameField.setStyle("-fx-border-color: red;");
        }
        if (password.isEmpty()) {
            passwordField.setStyle("-fx-border-color: red;");
        }
    }

    /**
     * Highlights fields with invalid credentials in red.
     */
    private void highlightInvalidCredentials() {
        usernameField.setStyle("-fx-border-color: red;");
        passwordField.setStyle("-fx-border-color: red;");
    }
}
