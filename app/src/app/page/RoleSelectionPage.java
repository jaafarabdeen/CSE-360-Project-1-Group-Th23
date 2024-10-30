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
 * The RoleSelectionPage class allows users with multiple roles to select a role for the session.
 * Depending on the selected role, the user will be directed to the appropriate dashboard.
 * 
 * Author:
 *     - Jaafar Abdeen
 */
public class RoleSelectionPage {
    private final Stage stage;
    private final User user;

    public RoleSelectionPage(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
    }

    /**
     * Displays the role selection UI and handles user interactions.
     */
    public void show() {
        // Role selection label
        Label roleLabel = new Label("Select your role for this session:");
        roleLabel.setFont(new Font("Arial", 48));
        roleLabel.setTextFill(Color.web("#ffffff"));

        VBox roleButtons = new VBox(20);
        roleButtons.setAlignment(Pos.CENTER);

        // Create a button for each role the user has
        user.getRoles().forEach(role -> roleButtons.getChildren().add(createRoleButton(role)));

        // Layout using VBox
        VBox vBox = new VBox(40, roleLabel, roleButtons);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(30));

        // Main layout using BorderPane
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(vBox);
        borderPane.setStyle("-fx-background-color: #3b5998;");

        // Create the scene with 1920x1080 resolution
        Scene scene = new Scene(borderPane, 1920, 1080);
        stage.setTitle("Role Selection");
        stage.setScene(scene);
        stage.show();
    }

    private Button createRoleButton(String role) {
        Button roleButton = new Button(role);
        roleButton.setPrefWidth(600);
        roleButton.setPrefHeight(50);
        roleButton.setStyle("-fx-background-color: #5865F2; -fx-text-fill: white; -fx-font-size: 24;");
        roleButton.setOnAction(e -> navigateToRolePage(role));
        return roleButton;
    }

    private void navigateToRolePage(String role) {
        if ("Admin".equals(role)) {
            new AdminPage(stage, user).show();
        } else {
            new DashboardPage(stage, user).show();
        }
    }
}