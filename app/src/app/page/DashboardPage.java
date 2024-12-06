package app.page;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import app.User;
import app.util.UIHelper;
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
        String welcomeText = "Welcome to the Dashboard, " + user.getPreferredName() + "!";

        // Welcome label with user's preferred name
        Label welcomeLabel = new Label(welcomeText);
        welcomeLabel.getStyleClass().add("label-title");

        // Help Articles button
        Button helpArticlesButton = UIHelper.createButton("Help Articles", e -> new HelpArticlesPage(stage, user).show());
        
        // Send Generic Message Button
        Button sendGenericMessageButton = UIHelper.createButton("Send Generic Message", e -> {
            try {
                new SendMessagePage(stage, user, "Generic").show();
            } catch (Exception e1) {}
        });

        // Send Specific Message Button
        Button sendSpecificMessageButton = UIHelper.createButton("Send Specific Message", e -> {
            try {
                new SendMessagePage(stage, user, "Specific").show();
            } catch (Exception e1) {}
        });

        Button viewMessagesButton = UIHelper.createButton("View Help Messages", e -> {
            try {
                new ViewHelpMessagesPage(stage, user).show();
            } catch (Exception e1) {}
        });

        HBox messageButtonBox;
        if (!user.hasRole("Instructor")) {
            // Layout for message buttons
            messageButtonBox = new HBox(20, sendGenericMessageButton, sendSpecificMessageButton);
            messageButtonBox.setAlignment(Pos.CENTER);
        } else {
            messageButtonBox = new HBox(20, viewMessagesButton);
            messageButtonBox.setAlignment(Pos.CENTER);
        }

        // Need Help? button
        Button needHelpButton = UIHelper.createButton("Need Help?", e -> new FAQPage(stage, user).show());

        // Logout button
        Button logoutButton = UIHelper.createButton("Logout", e -> new LoginPage(stage).show());
        
        // Quit Button
        Button quitButton = UIHelper.createButton("Quit", e -> {
            stage.close();
        }, "-fx-background-color: #FF5555;");

        // Layout using VBox
        VBox vBox = new VBox(40, welcomeLabel, helpArticlesButton, messageButtonBox, needHelpButton, logoutButton, quitButton);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(30));

        // Main layout using BorderPane
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(vBox);

        // Create the scene with CSS styling
        Scene scene = UIHelper.createStyledScene(borderPane, 1920, 1080);
        stage.setTitle("Dashboard");
        stage.setScene(scene);
        stage.show();
    }
}
