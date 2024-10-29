package app.page;

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
import Encryption.EncryptionUtils;
import app.User;

import java.util.Base64;

/**
 * The ResetPasswordPage class allows users to reset their password using a one-time password.
 * Users can set a new password and then log in with the new credentials.
 * 
 * Author:
 *     - Ayush Kaushik
 *     - Jaafar Abdeen
 */
public class ResetPasswordPage {
    private final Stage stage;
    private final User user;

    public ResetPasswordPage(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
    }

    /**
     * Displays the reset password UI and handles user interactions.
     */
    public void show() {
        String buttonStyle = "-fx-background-color: #5865F2; -fx-text-fill: white; -fx-font-size: 24;";
        String fieldStyle = "-fx-background-color: #40444b; -fx-text-fill: #ffffff; -fx-font-size: 24;";

        // Title label
        Label titleLabel = new Label("Reset Your Password");
        titleLabel.setFont(new Font("Arial", 56));
        titleLabel.setTextFill(Color.web("#ffffff"));

        // Password fields
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("NEW PASSWORD");
        passwordField.setMaxWidth(600);
        passwordField.setStyle(fieldStyle);

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("CONFIRM PASSWORD");
        confirmPasswordField.setMaxWidth(600);
        confirmPasswordField.setStyle(fieldStyle);

        // Message label for error messages
        Label messageLabel = new Label();
        messageLabel.setTextFill(Color.web("#ff5555"));
        messageLabel.setFont(new Font("Arial", 28));

        // Save button
        Button saveButton = createButton("Save", buttonStyle, e -> {
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                messageLabel.setText("Please fill in all fields.");
            } else if (!password.equals(confirmPassword)) {
                messageLabel.setText("Passwords do not match.");
            } else {
                String hashedPassword = Base64.getEncoder().encodeToString(EncryptionUtils.hashPassword(password));
                user.setPassword(hashedPassword);
                user.setOneTimePassword(false);
                user.setOneTimePasswordExpiry(null);

                // Return to login page
                new LoginPage(stage).show();
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

    private Button createButton(String text, String style, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button button = new Button(text);
        button.setPrefWidth(600);
        button.setPrefHeight(50);
        button.setStyle(style);
        button.setOnAction(action);
        return button;
    }
}
