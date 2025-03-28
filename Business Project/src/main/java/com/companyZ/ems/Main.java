package com.companyZ.ems;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main application class for the Employee Management System.
 * This class serves as the entry point for the JavaFX application and handles:
 * - Application initialization
 * - Scene setup and display
 * - Resource management
 * - Error handling
 */
public class Main extends Application {
    private static final Logger logger = LogManager.getLogger(Main.class);

    /**
     * The main entry point for the JavaFX application.
     * Loads the login screen and sets up the primary stage.
     *
     * @param primaryStage The primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the login screen FXML
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));

            // Create and configure the scene
            Scene scene = new Scene(root);
            primaryStage.setTitle("Company Z - Employee Management System");
            primaryStage.setScene(scene);
            primaryStage.show();

            logger.info("Application started successfully");
        } catch (Exception e) {
            logger.error("Failed to start application", e);
            System.exit(1);
        }
    }

    /**
     * Application entry point. Launches the JavaFX application.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Called by the JavaFX runtime during application initialization.
     * Initializes necessary resources and connections.
     *
     * @throws Exception if initialization fails
     */
    @Override
    public void init() throws Exception {
        super.init();
        // Initialize database connection and other resources
        logger.info("Initializing application resources");
    }

    /**
     * Called by the JavaFX runtime during application shutdown.
     * Performs cleanup of resources.
     *
     * @throws Exception if cleanup fails
     */
    @Override
    public void stop() throws Exception {
        super.stop();
        // Clean up resources (close database connections, etc.)
        logger.info("Cleaning up application resources");
    }
}