package app;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
import java.sql.SQLException;
import Encryption.EncryptionUtils;
import Encryption.EncryptionHelper;
import java.util.Base64;

/**
 * The LoginPage class represents the login screen of the application.
 * Users can log in with their username and password or use an invitation code to register.
 * It also provides functionalities for the admin to manage users.
 * 
 * User data is stored in the database.
 * Invitation codes are also stored in a database table for validation purposes.
 * 
 * Author:
 *     - Pragya Kumari
 *     - Aaryan Gaur
 */
public class LoginPage {
    private final Stage stage;
    private final DatabaseHelper databaseHelper;
    private final EncryptionHelper encryptionHelper;

    public LoginPage(Stage stage) {
        this.stage = stage;
        DatabaseHelper tempDatabaseHelper = null;
        EncryptionHelper tempEncryptionHelper = null;
        try {
            tempDatabaseHelper = new DatabaseHelper();
            tempEncryptionHelper = new EncryptionHelper();
            tempDatabaseHelper.connectToDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.databaseHelper = tempDatabaseHelper;
        this.encryptionHelper = tempEncryptionHelper;
    }

    /**
     * Displays the login page UI and handles user interactions.
     */
    public void show() {
        String buttonStyle = "-fx-background-color: #5865F2; -fx-text-fill: white; -fx-font-size: 24;";
        String fieldStyle = "-fx-background-color: #40444b; -fx-text-fill: #ffffff; -fx-font-size: 24;";

        // Title label with welcome message
        Label titleLabel = new Label("Welcome!");
        titleLabel.setFont(new Font("Arial", 56));
        titleLabel.setTextFill(Color.web("#ffffff"));

        // Username input field
        TextField usernameField = new TextField();
        usernameField.setPromptText("USERNAME");
        usernameField.setMaxWidth(600);
        usernameField.setStyle(fieldStyle);

        // Password input field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("PASSWORD");
        passwordField.setMaxWidth(600);
        passwordField.setStyle(fieldStyle);

        // Invitation code input field
        TextField invitationCodeField = new TextField();
        invitationCodeField.setPromptText("INVITATION CODE");
        invitationCodeField.setMaxWidth(600);
        invitationCodeField.setStyle(fieldStyle);
        
        // Message label for error or information messages
        Label messageLabel = new Label();
        messageLabel.setTextFill(Color.web("#ff5555"));
        messageLabel.setFont(new Font("Arial", 28));

        // Login button
        Button loginButton = createButton("Login", buttonStyle, e -> handleLogin(usernameField, passwordField, messageLabel));

        // Register button
        Button registerButton = createButton("Register", buttonStyle, e -> handleRegistration(invitationCodeField, messageLabel));

        // Layout configuration using VBox
        VBox vBox = new VBox(20, titleLabel, usernameField, passwordField, invitationCodeField, loginButton, registerButton, messageLabel);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(60));

        // Main layout using BorderPane
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(vBox);
        borderPane.setStyle("-fx-background-color: #3b5998;");

        // Create the scene with 1920x1080 resolution
        Scene scene = new Scene(borderPane, 1920, 1080);
        stage.setTitle("Login Page");
        stage.setScene(scene);
        stage.show();
    }

    private void handleRegistration(TextField invitationCodeField, Label messageLabel) {
        String invitationCode = invitationCodeField.getText();
        if (invitationCode.isEmpty()) {
            messageLabel.setText("Please enter an invitation code.");
            return;
        }

        try {
            if (databaseHelper.doesInvitationExist(invitationCode)) {
                Invitation invitation = databaseHelper.getInvitation(invitationCode);
                databaseHelper.deleteInvitation(invitationCode);
                new AccountCreationPage(stage, invitation).show();
            } else {
                messageLabel.setText("Invalid invitation code.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void handleLogin(TextField usernameField, PasswordField passwordField, Label messageLabel) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String hashedPassword = Base64.getEncoder().encodeToString(EncryptionUtils.hashPassword(password));

        try {
            if (databaseHelper.isDatabaseEmpty()) {
                handleAdminAccountCreation(username, password, messageLabel);
            } else if (!username.isEmpty() && !password.isEmpty()) {
                if (databaseHelper.doesUserExist(username)) {
                    User user = databaseHelper.getUser(username);
                    if (hashedPassword.equals(user.getPassword())) {
                        proceedToNextPage(user);
                    } else {
                        messageLabel.setText("Incorrect password.");
                    }
                } else {
                    messageLabel.setText("User not found.");
                }
            } else {
                messageLabel.setText("Please enter your username and password or invitation code.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleAdminAccountCreation(String username, String password, Label messageLabel) throws SQLException {
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter a username and password.");
        } else {
            User adminUser = new User(username, Base64.getEncoder().encodeToString(EncryptionUtils.hashPassword(password)), "Admin");
            try {
				databaseHelper.registerUser(adminUser);
			} catch (Exception e) {
				e.printStackTrace();
			}
            messageLabel.setText("Admin account created. Please log in again.");
        }
    }

    private void proceedToNextPage(User user) {
        if (user.getEmail() == null || user.getFirstName() == null) {
            FinishSettingUpAccountPage finishPage = new FinishSettingUpAccountPage(stage, user);
            finishPage.show();
            finishPage.setOnFinishSetup(() -> {
                try {
                    databaseHelper.updateUser(user);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });
        } else if (user.getRoles().size() > 1) {
            new RoleSelectionPage(stage, user).show();
        } else {
            navigateToRolePage(user);
        }
    }

    private void navigateToRolePage(User user) {
        String role = user.getRoles().iterator().next();
        if ("Admin".equals(role)) {
            new AdminPage(stage, user).show();
        } else {
            new DashboardPage(stage, user).show();
        }
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
