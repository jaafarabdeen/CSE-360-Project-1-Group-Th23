package app;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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
    private Stage stage;
    private User user;

    public FinishSettingUpAccountPage(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
    }

    /**
     * Displays the account setup UI and handles user interactions.
     */
    public void show() {
        // Title label
        Label titleLabel = new Label("Finish Setting Up Your Account");
        titleLabel.setFont(new Font("Arial", 56));
        titleLabel.setTextFill(Color.web("#ffffff"));

        // Email field
        TextField emailField = new TextField();
        emailField.setPromptText("EMAIL ADDRESS");
        emailField.setMaxWidth(600);
        emailField.setStyle("-fx-background-color: #40444b; -fx-text-fill: #ffffff; -fx-font-size: 24;");

        // First Name field
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("FIRST NAME");
        firstNameField.setMaxWidth(600);
        firstNameField.setStyle("-fx-background-color: #40444b; -fx-text-fill: #ffffff; -fx-font-size: 24;");

        // Middle Name field
        TextField middleNameField = new TextField();
        middleNameField.setPromptText("MIDDLE NAME");
        middleNameField.setMaxWidth(600);
        middleNameField.setStyle("-fx-background-color: #40444b; -fx-text-fill: #ffffff; -fx-font-size: 24;");

        // Last Name field
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("LAST NAME");
        lastNameField.setMaxWidth(600);
        lastNameField.setStyle("-fx-background-color: #40444b; -fx-text-fill: #ffffff; -fx-font-size: 24;");

        // Preferred Name field
        TextField preferredNameField = new TextField();
        preferredNameField.setPromptText("PREFERRED FIRST NAME (Optional)");
        preferredNameField.setMaxWidth(600);
        preferredNameField.setStyle("-fx-background-color: #40444b; -fx-text-fill: #ffffff; -fx-font-size: 24;");

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

                // Proceed to role selection or home page
                if (user.getRoles().size() > 1) {
                    RoleSelectionPage roleSelectionPage = new RoleSelectionPage(stage, user);
                    roleSelectionPage.show();
                } else {
                    String role = user.getRoles().iterator().next();
                    if (role.equals("Admin")) {
                        AdminPage adminPage = new AdminPage(stage, user);
                        adminPage.show();
                    } else {
                        DashboardPage dashboardPage = new DashboardPage(stage, user);
                        dashboardPage.show();
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
