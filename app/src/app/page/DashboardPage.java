package app.page;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import app.User;
import javafx.geometry.Insets;

/**
 * The DashboardPage class represents the user dashboard.
 * Users can see a welcome message and have the option to log out.
 * 
 * This class provides a simple dashboard interface for non-admin users.
 * Admins have a separate dashboard with additional functionalities.
 * 
 * Note: Additional features can be added to the dashboard as needed.
 * 
 * Author:
 *     - Ayush Kaushik
 *     - Jaafar Abdeen
 */
public class DashboardPage {
    private final Stage stage;
    private final User user;

    public DashboardPage(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
    }

    /**
     * Displays the dashboard UI and handles user interactions.
     */
    public void show() {
        String buttonStyle = "-fx-text-fill: white; -fx-font-size: 24;";
        String welcomeText = "Welcome to the Dashboard, " + user.getPreferredName() + "!";

        // Welcome label with user's preferred name
        Label welcomeLabel = new Label(welcomeText);
        welcomeLabel.setFont(new Font("Arial", 56));
        welcomeLabel.setTextFill(Color.web("#ffffff"));

        // Help Articles button
        Button helpArticlesButton = new Button("Help Articles");
        helpArticlesButton.setPrefWidth(600);
        helpArticlesButton.setPrefHeight(50);
        helpArticlesButton.setStyle("-fx-background-color: #5865F2; " + buttonStyle);
        helpArticlesButton.setOnAction(e -> new HelpArticlesPage(stage, user).show());

        // Logout button
        Button logoutButton = new Button("Logout");
        logoutButton.setPrefWidth(600);
        logoutButton.setPrefHeight(50);
        logoutButton.setStyle("-fx-background-color: #FF5555; " + buttonStyle);
        logoutButton.setOnAction(e -> new LoginPage(stage).show());

        // Layout using VBox
        VBox vBox = new VBox(40, welcomeLabel, helpArticlesButton, logoutButton);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(30));

        // Main layout using BorderPane
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(vBox);
        borderPane.setStyle("-fx-background-color: #3b5998;");

        // Create the scene with 1920x1080 resolution
        Scene scene = new Scene(borderPane, 1920, 1080);
        stage.setTitle("Dashboard");
        stage.setScene(scene);
        stage.show();
    }
}
