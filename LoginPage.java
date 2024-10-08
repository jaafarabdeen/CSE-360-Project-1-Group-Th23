// LoginPage.java
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
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;

/**
 * The LoginPage class represents the login screen of the application.
 * Users can log in with their username and password or use an invitation code to register.
 * It also provides functionalities for the admin to manage users.
 * 
 * User data is stored in a HashMap, mapping usernames to User objects.
 * Invitation codes are also stored in a HashMap for validation purposes.
 * 
 * @author
 *     - Pragya Kumari
 *     - Aaryan Gaur
 */
public class LoginPage {
    private Stage stage;
    /**
     * The user database storing username and User object pairs.
     */
    public static Map<String, User> userDatabase = new HashMap<>();

    /**
     * The invitation code database storing codes and Invitation objects.
     */
    public static Map<String, Invitation> invitationTokens = new HashMap<>();

    public LoginPage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Displays the login page UI and handles user interactions.
     */
    public void show() {
        // Title label with welcome message
        Label titleLabel = new Label("Welcome!");
        titleLabel.setFont(new Font("Arial", 56));  // Larger font for better visibility
        titleLabel.setTextFill(Color.web("#ffffff"));

        // Username input field
        TextField usernameField = new TextField();
        usernameField.setPromptText("USERNAME");
        usernameField.setMaxWidth(600);
        usernameField.setStyle("-fx-background-color: #40444b; -fx-text-fill: #ffffff; -fx-font-size: 24;");

        // Password input field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("PASSWORD");
        passwordField.setMaxWidth(600);
        passwordField.setStyle("-fx-background-color: #40444b; -fx-text-fill: #ffffff; -fx-font-size: 24;");

        // Invitation code input field
        TextField invitationCodeField = new TextField();
        invitationCodeField.setPromptText("INVITATION CODE");
        invitationCodeField.setMaxWidth(600);
        invitationCodeField.setStyle("-fx-background-color: #40444b; -fx-text-fill: #ffffff; -fx-font-size: 24;");

        // Login button
        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(600);
        loginButton.setPrefHeight(50);
        loginButton.setStyle("-fx-background-color: #5865F2; -fx-text-fill: white; -fx-font-size: 24;");

        // Register button
        Button registerButton = new Button("Register");
        registerButton.setPrefWidth(600);
        registerButton.setPrefHeight(50);
        registerButton.setStyle("-fx-background-color: #5865F2; -fx-text-fill: white; -fx-font-size: 24;");

        // Message label for error or information messages
        Label messageLabel = new Label();
        messageLabel.setTextFill(Color.web("#ff5555"));
        messageLabel.setFont(new Font("Arial", 28));

        // Event handler for the login button
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String invitationCode = invitationCodeField.getText();

            if (userDatabase.isEmpty()) {
                // If no users exist, create an admin account
                if (username.isEmpty() || password.isEmpty()) {
                    messageLabel.setText("Please enter a username and password.");
                } else {
                    User adminUser = new User(username, password, "Admin");
                    userDatabase.put(username, adminUser);
                    messageLabel.setText("Admin account created. Please log in again.");
                    usernameField.clear();
                    passwordField.clear();
                }
            } else if (!invitationCode.isEmpty()) {
                // Handle registration via invitation code
                if (invitationTokens.containsKey(invitationCode)) {
                    Invitation invitation = invitationTokens.get(invitationCode);
                    invitationTokens.remove(invitationCode);
                    // Proceed to account creation page
                    AccountCreationPage accountCreationPage = new AccountCreationPage(stage, invitation);
                    accountCreationPage.show();
                } else {
                    messageLabel.setText("Invalid invitation code.");
                }
            } else if (!username.isEmpty() && !password.isEmpty()) {
                // Handle normal login
                if (userDatabase.containsKey(username)) {
                    User user = userDatabase.get(username);
                    if (user.isOneTimePassword()) {
                        // Check if one-time password is still valid
                        if (LocalDateTime.now().isAfter(user.getOneTimePasswordExpiry())) {
                            messageLabel.setText("One-time password has expired.");
                        } else if (user.checkPassword(password)) {
                            // Proceed to reset password
                            ResetPasswordPage resetPasswordPage = new ResetPasswordPage(stage, user);
                            resetPasswordPage.show();
                        } else {
                            messageLabel.setText("Invalid one-time password.");
                        }
                    } else if (user.checkPassword(password)) {
                        // Check if user has completed account setup
                        if (user.getEmail() == null || user.getFirstName() == null) {
                            // Proceed to finish setting up account
                            FinishSettingUpAccountPage finishPage = new FinishSettingUpAccountPage(stage, user);
                            finishPage.show();
                        } else if (user.getRoles().size() > 1) {
                            // Proceed to role selection page
                            RoleSelectionPage roleSelectionPage = new RoleSelectionPage(stage, user);
                            roleSelectionPage.show();
                        } else {
                            // Proceed to home page based on role
                            String role = user.getRoles().iterator().next();
                            if (role.equals("Admin")) {
                                AdminPage adminPage = new AdminPage(stage, user);
                                adminPage.show();
                            } else {
                                DashboardPage dashboardPage = new DashboardPage(stage, user);
                                dashboardPage.show();
                            }
                        }
                    } else {
                        messageLabel.setText("Incorrect password.");
                    }
                } else {
                    messageLabel.setText("User not found.");
                }
            } else {
                messageLabel.setText("Please enter your username and password or invitation code.");
            }
        });

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
        stage.show();  // Display the login page
    }
}
