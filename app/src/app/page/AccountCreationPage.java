package app.page;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
import java.util.Base64;
import Encryption.EncryptionUtils;
import app.User;
import app.dialog.LevelSelectionDialog;
import app.util.DatabaseHelper;
import app.util.Invitation;

/**
 * The AccountCreationPage class allows new users to create an account using an invitation code.
 * Users can set their username and password, and the roles are assigned based on the invitation.
 * 
 * Author:
 *     - Jaafar Abdeen
 *     - Ayush Kaushik
 *     - Lewam Atnafie
 */
public class AccountCreationPage {
    private final Stage stage;
    private final Invitation invitation;
    private final DatabaseHelper databaseHelper;

    public AccountCreationPage(Stage stage, Invitation invitation) {
        this.stage = stage;
        this.invitation = invitation;
        DatabaseHelper tempDatabaseHelper = null;
        try {
            tempDatabaseHelper = new DatabaseHelper();
            tempDatabaseHelper.connectToDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.databaseHelper = tempDatabaseHelper;
    }

    /**
     * Displays the account creation UI and handles user interactions.
     */
    public void show() {
        // Title label
        Label titleLabel = new Label("Create Your Account");
        titleLabel.setFont(new Font("Arial", 56));
        titleLabel.setTextFill(Color.web("#ffffff"));

        // Username field
        TextField usernameField = new TextField();
        usernameField.setPromptText("USERNAME");
        usernameField.setMaxWidth(600);
        usernameField.setStyle("-fx-background-color: #40444b; -fx-text-fill: #ffffff; -fx-font-size: 24;");

        // Password fields
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("PASSWORD");
        passwordField.setMaxWidth(600);
        passwordField.setStyle("-fx-background-color: #40444b; -fx-text-fill: #ffffff; -fx-font-size: 24;");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("CONFIRM PASSWORD");
        confirmPasswordField.setMaxWidth(600);
        confirmPasswordField.setStyle("-fx-background-color: #40444b; -fx-text-fill: #ffffff; -fx-font-size: 24;");

        // Level selection field
        LevelSelectionDialog levelDialog = new LevelSelectionDialog();
        String selectedLevel = levelDialog.showAndWait();

        // Message label for error messages
        Label messageLabel = new Label();
        messageLabel.setTextFill(Color.web("#ff5555"));
        messageLabel.setFont(new Font("Arial", 28));

        // Register button
        Button registerButton = new Button("Register");
        registerButton.setPrefWidth(600);
        registerButton.setPrefHeight(50);
        registerButton.setStyle("-fx-background-color: #5865F2; -fx-text-fill: white; -fx-font-size: 24;");
        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            try {
                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    messageLabel.setText("Please fill in all fields.");
                } else if (!password.equals(confirmPassword)) {
                    messageLabel.setText("Passwords do not match.");
                } else if (databaseHelper.doesUserExist(username)) {
                    messageLabel.setText("Username already exists.");
                } else {
                    // Create new user account only if all checks pass
                    String hashedPassword = Base64.getEncoder().encodeToString(EncryptionUtils.hashPassword(password));
                    User newUser = new User(username, hashedPassword, "");
                    for (String role : invitation.getRoles()) {
                        newUser.addRole(role);
                    }
                    newUser.setLevel(selectedLevel);
                    databaseHelper.registerUser(newUser);
                    messageLabel.setText("Account created. Please log in.");
                    // Return to login page
                    LoginPage loginPage = new LoginPage(stage);
                    loginPage.show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                messageLabel.setText("An error occurred. Please try again.");
            }
        });

        // Layout using VBox
        VBox vBox = new VBox(20, titleLabel, usernameField, passwordField, confirmPasswordField, registerButton, messageLabel);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(60));
        vBox.setStyle("-fx-background-color: #3b5998;");

        // Create the scene with 1920x1080 resolution
        Scene scene = new Scene(vBox, 1920, 1080);
        stage.setTitle("Account Creation");
        stage.setScene(scene);
        stage.show();
    }
}