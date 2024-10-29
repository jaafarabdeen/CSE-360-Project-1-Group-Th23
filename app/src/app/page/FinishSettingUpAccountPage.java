package app.page;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import app.User;
import javafx.geometry.Insets;

/**
 * The FinishSettingUpAccountPage class allows users to complete their account setup by providing personal information.
 * Users can enter their email, first name, middle name, last name, and preferred name.
 * 
 * Author:
 *     - Ayush Kaushik
 *     
 */
public class FinishSettingUpAccountPage {
    private final Stage stage;
    private final User user;
    private Runnable onFinishSetup;

    public FinishSettingUpAccountPage(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
    }

    public void setOnFinishSetup(Runnable onFinishSetup) {
        this.onFinishSetup = onFinishSetup;
    }

    /**
     * Displays the account setup UI and handles user interactions.
     */
    public void show() {
        String fieldStyle = "-fx-background-color: #40444b; -fx-text-fill: #ffffff; -fx-font-size: 24;";
        String buttonStyle = "-fx-background-color: #5865F2; -fx-text-fill: white; -fx-font-size: 24;";

        // Title label
        Label titleLabel = new Label("Finish Setting Up Your Account");
        titleLabel.setFont(new Font("Arial", 56));
        titleLabel.setTextFill(Color.web("#ffffff"));

        // Email field
        TextField emailField = new TextField();
        emailField.setPromptText("EMAIL ADDRESS");
        emailField.setMaxWidth(600);
        emailField.setStyle(fieldStyle);

        // First Name field
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("FIRST NAME");
        firstNameField.setMaxWidth(600);
        firstNameField.setStyle(fieldStyle);

        // Middle Name field
        TextField middleNameField = new TextField();
        middleNameField.setPromptText("MIDDLE NAME");
        middleNameField.setMaxWidth(600);
        middleNameField.setStyle(fieldStyle);

        // Last Name field
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("LAST NAME");
        lastNameField.setMaxWidth(600);
        lastNameField.setStyle(fieldStyle);

        // Preferred Name field
        TextField preferredNameField = new TextField();
        preferredNameField.setPromptText("PREFERRED FIRST NAME (Optional)");
        preferredNameField.setMaxWidth(600);
        preferredNameField.setStyle(fieldStyle);

        // Message label for error messages
        Label messageLabel = new Label();
        messageLabel.setTextFill(Color.web("#ff5555"));
        messageLabel.setFont(new Font("Arial", 28));

        // Save button
        Button saveButton = new Button("Save");
        saveButton.setPrefWidth(600);
        saveButton.setPrefHeight(50);
        saveButton.setStyle(buttonStyle);
        saveButton.setOnAction(e -> {
            String email = emailField.getText();
            String firstName = firstNameField.getText();
            String middleName = middleNameField.getText();
            String lastName = lastNameField.getText();
            String preferredName = preferredNameField.getText();

            if (email.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                messageLabel.setText("Please fill in the required fields.");
            } else {
                user.setEmail(email);
                user.setFirstName(firstName);
                user.setMiddleName(middleName);
                user.setLastName(lastName);
                user.setPreferredName(preferredName);

                if (onFinishSetup != null) {
                    onFinishSetup.run();
                }

                // Proceed to role selection or home page
                if (user.getRoles().size() > 1) {
                    new RoleSelectionPage(stage, user).show();
                } else {
                    String role = user.getRoles().iterator().next();
                    if ("Admin".equals(role)) {
                        new AdminPage(stage, user).show();
                    } else {
                        new DashboardPage(stage, user).show();
                    }
                }
            }
        });

        // Layout using VBox
        VBox vBox = new VBox(20, titleLabel, emailField, firstNameField, middleNameField, lastNameField, preferredNameField, saveButton, messageLabel);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(60));
        vBox.setStyle("-fx-background-color: #3b5998;");

        // Create the scene with 1920x1080 resolution
        Scene scene = new Scene(vBox, 1920, 1080);
        stage.setTitle("Finish Setting Up Account");
        stage.setScene(scene);
        stage.show();
    }
}
