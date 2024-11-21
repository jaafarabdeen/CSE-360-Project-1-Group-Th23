package app;

import app.page.LoginPage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
            // Set up initial root with background color to prevent white flashes
            VBox root = new VBox();
            root.setStyle("-fx-background-color: #2e3440;");
            
            // Create a single scene and set the scene background color directly
            Scene scene = new Scene(root, 1920, 1080);
            scene.setFill(Color.web("#2e3440"));

            // Set the scene to the primary stage
            primaryStage.setScene(scene);
            primaryStage.show();

            // Initialize and display the login page by updating the root node
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
