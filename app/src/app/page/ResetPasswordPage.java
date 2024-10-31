package app.page;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import app.User;
import app.util.UIHelper;
import javafx.geometry.Insets;
import Encryption.EncryptionUtils;

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
        // Title label
        Label titleLabel = new Label("Reset Your Password");
        titleLabel.getStyleClass().add("label-title");

        // Password fields
        PasswordField passwordField = UIHelper.createPasswordField("NEW PASSWORD");
        PasswordField confirmPasswordField = UIHelper.createPasswordField("CONFIRM PASSWORD");

        // Message label for error messages
        Label messageLabel = UIHelper.createMessageLabel();

        // Save button
        Button saveButton = UIHelper.createButton("Save", e -> {
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                UIHelper.setMessage(messageLabel, "Please fill in all fields.", true);
            } else if (!password.equals(confirmPassword)) {
                UIHelper.setMessage(messageLabel, "Passwords do not match.", true);
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

        // Create the scene with CSS styling
        Scene scene = UIHelper.createStyledScene(vBox, 1920, 1080);
        stage.setTitle("Reset Password");
        stage.setScene(scene);
        stage.show();
    }
}
