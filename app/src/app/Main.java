package app;

import app.page.LoginPage;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The main entry point of the application.
 * Launches the JavaFX application and shows the login page.
 * 
 * Author:
 *     - Ayush Kaushik
 */
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize and display the login page
            LoginPage loginPage = new LoginPage(primaryStage);
            loginPage.show();
        } catch (Exception e) {
            System.err.println("An error occurred while starting the application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}
