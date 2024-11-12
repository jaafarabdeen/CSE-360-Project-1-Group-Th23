package app.page;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.Base64;
import Encryption.EncryptionUtils;
import app.User;
import app.dialog.LevelSelectionDialog;
import app.util.DatabaseHelper;
import app.util.Invitation;
import app.util.UIHelper;

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
    	stage.setOnCloseRequest(event -> databaseHelper.closeConnection());
        // Title label
        Label titleLabel = new Label("Create Your Account");
        titleLabel.getStyleClass().add("label-title");

        // Username field
        TextField usernameField = UIHelper.createTextField("USERNAME");

        // Password fields
        PasswordField passwordField = UIHelper.createPasswordField("PASSWORD");
        PasswordField confirmPasswordField = UIHelper.createPasswordField("CONFIRM PASSWORD");

        // Level selection field
        LevelSelectionDialog levelDialog = new LevelSelectionDialog();
        String selectedLevel = levelDialog.showAndWait();

        // Message label for error messages
        Label messageLabel = UIHelper.createMessageLabel();

        // Register button
        Button registerButton = UIHelper.createButton("Register", e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            try {
                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    UIHelper.setMessage(messageLabel, "Please fill in all fields.", true);
                } else if (!password.equals(confirmPassword)) {
                    UIHelper.setMessage(messageLabel, "Passwords do not match.", true);
                } else if (databaseHelper.doesUserExist(username)) {
                    UIHelper.setMessage(messageLabel, "Username already exists.", true);
                } else {
                    // Create new user account only if all checks pass
                    String hashedPassword = Base64.getEncoder().encodeToString(EncryptionUtils.hashPassword(password));
                    User newUser = new User(username, hashedPassword, "");
                    for (String role : invitation.getRoles()) {
                        newUser.addRole(role);
                    }
                    newUser.setLevel(selectedLevel);
                    databaseHelper.registerUser(newUser);
                    UIHelper.setMessage(messageLabel, "Account created. Please log in.", false);
                    // Return to login page
                    LoginPage loginPage = new LoginPage(stage);
                    loginPage.show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                UIHelper.setMessage(messageLabel, "An error occurred. Please try again.", true);
            }
        });

        // Layout using VBox
        VBox vBox = new VBox(20, titleLabel, usernameField, passwordField, confirmPasswordField, registerButton, messageLabel);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(60));

        // Create the scene with CSS styling
        Scene scene = UIHelper.createStyledScene(vBox, 1920, 1080);
        stage.setTitle("Account Creation");
        stage.setScene(scene);
        stage.show();
    }
}
