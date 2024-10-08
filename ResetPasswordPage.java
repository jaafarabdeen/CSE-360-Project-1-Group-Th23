// ResetPasswordPage.java
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.geometry.Insets;

/**
 * The ResetPasswordPage class allows users to reset their password using a one-time password.
 * Users can set a new password and then log in with the new credentials.
 * 
 * Author:
 *     - Ayush Kaushik
 *     - Jaafar Abdeen
 */
public class ResetPasswordPage {
    private Stage stage;
    private User user;

    public ResetPasswordPage(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
    }

    /**
     * Displays the reset password UI and handles user interactions.
     */
    public void show() {
        // Title label
        Label titleLabel = new Label("Reset Your Password");
        titleLabel.setFont(new Font("Arial", 56));
        titleLabel.setTextFill(Color.web("#ffffff"));

        // Password fields
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("NEW PASSWORD");
        passwordField.setMaxWidth(600);
        passwordField.setStyle("-fx-background-color: #40444b; -fx-text-fill: #ffffff; -fx-font-size: 24;");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("CONFIRM PASSWORD");
        confirmPasswordField.setMaxWidth(600);
        confirmPasswordField.setStyle("-fx-background-color: #40444b; -fx-text-fill: #ffffff; -fx-font-size: 24;");

        // Message label for error messages
        Label messageLabel = new Label();
        messageLabel.setTextFill(Color.web("#ff5555"));
        messageLabel.setFont(new Font("Arial", 28));

        // Save button
        Button saveButton = new Button("Save");
        saveButton.setPrefWidth(600);
        saveButton.setPrefHeight(50);
        saveButton.setStyle("-fx-background-color: #5865F2; -fx-text-fill: white; -fx-font-size: 24;");
        saveButton.setOnAction(e -> {
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                messageLabel.setText("Please fill in all fields.");
            } else if (!password.equals(confirmPassword)) {
                messageLabel.setText("Passwords do not match.");
            } else {
                user.setPassword(password);
                user.setOneTimePassword(false);
                user.setOneTimePasswordExpiry(null);
                // Return to login page
                LoginPage loginPage = new LoginPage(stage);
                loginPage.show();
            }
        });

        // Layout using VBox
        VBox vBox = new VBox(20, titleLabel, passwordField, confirmPasswordField, saveButton, messageLabel);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(60));
        vBox.setStyle("-fx-background-color: #3b5998;");

        // Create the scene with 1920x1080 resolution
        Scene scene = new Scene(vBox, 1920, 1080);
        stage.setTitle("Reset Password");
        stage.setScene(scene);
        stage.show();
    }
}
