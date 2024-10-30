package app.page;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Insets;
import java.sql.SQLException;
import Encryption.EncryptionUtils;
import app.User;
import app.util.DatabaseHelper;
import app.util.Invitation;
import app.util.UIHelper;
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

    public LoginPage(Stage stage) {
        this.stage = stage;
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
     * Displays the login page UI and handles user interactions.
     */
    public void show() {
        // Title label with welcome message
        Label titleLabel = new Label("Welcome!");
        titleLabel.getStyleClass().add("label-title");

        // Create fields and buttons using UIHelper methods
        TextField usernameField = UIHelper.createTextField("USERNAME");
        PasswordField passwordField = UIHelper.createPasswordField("PASSWORD");
        TextField invitationCodeField = UIHelper.createTextField("INVITATION CODE");

        Label messageLabel = UIHelper.createMessageLabel();

        Button loginButton = UIHelper.createButton("Login", e -> handleLogin(usernameField, passwordField, messageLabel));
        Button registerButton = UIHelper.createButton("Register", e -> handleRegistration(invitationCodeField, messageLabel));

        // Layout configuration using VBox
        VBox vBox = new VBox(20, titleLabel, usernameField, passwordField, invitationCodeField, loginButton, registerButton, messageLabel);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(60));

        // Main layout using BorderPane
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(vBox);

        // Create the scene using UIHelper and apply the CSS stylesheet
        Scene scene = UIHelper.createStyledScene(borderPane, 1920, 1080);

        // Display the stage
        stage.setTitle("Login Page");
        stage.setScene(scene);
        stage.show();
    }

    private void handleRegistration(TextField invitationCodeField, Label messageLabel) {
        String invitationCode = invitationCodeField.getText();
        if (invitationCode.isEmpty()) {
            UIHelper.setMessage(messageLabel, "Please enter an invitation code.", true);
            return;
        }

        try {
            if (databaseHelper.doesInvitationExist(invitationCode)) {
                Invitation invitation = databaseHelper.getInvitation(invitationCode);
                databaseHelper.deleteInvitation(invitationCode);
                new AccountCreationPage(stage, invitation).show();
            } else {
                UIHelper.setMessage(messageLabel, "Invalid invitation code.", true);
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
                        UIHelper.setMessage(messageLabel, "Incorrect password.", true);
                    }
                } else {
                    UIHelper.setMessage(messageLabel, "User not found.", true);
                }
            } else {
                UIHelper.setMessage(messageLabel, "Please enter your username and password or invitation code.", true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleAdminAccountCreation(String username, String password, Label messageLabel) throws SQLException {
        if (username.isEmpty() || password.isEmpty()) {
            UIHelper.setMessage(messageLabel, "Please enter a username and password.", true);
        } else {
            User adminUser = new User(username, Base64.getEncoder().encodeToString(EncryptionUtils.hashPassword(password)), "Admin");
            try {
                databaseHelper.registerUser(adminUser);
            } catch (Exception e) {
                e.printStackTrace();
            }
            UIHelper.setMessage(messageLabel, "Admin account created. Please log in again.", false);
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
}
