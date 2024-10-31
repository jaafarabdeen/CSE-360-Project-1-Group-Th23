package app.page;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import app.User;
import app.util.UIHelper;
import javafx.geometry.Insets;

/**
 * The FinishSettingUpAccountPage class allows users to complete their account setup by providing personal information.
 * Users can enter their email, first name, middle name, last name, and preferred name.
 * 
 * Author:
 *     - Ayush Kaushik
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
        // Title label
        Label titleLabel = new Label("Finish Setting Up Your Account");
        titleLabel.getStyleClass().add("label-title");

        // Email field
        TextField emailField = UIHelper.createTextField("EMAIL ADDRESS");

        // First Name field
        TextField firstNameField = UIHelper.createTextField("FIRST NAME");

        // Middle Name field
        TextField middleNameField = UIHelper.createTextField("MIDDLE NAME");

        // Last Name field
        TextField lastNameField = UIHelper.createTextField("LAST NAME");

        // Preferred Name field
        TextField preferredNameField = UIHelper.createTextField("PREFERRED FIRST NAME (Optional)");

        // Message label for error messages
        Label messageLabel = UIHelper.createMessageLabel();

        // Save button
        Button saveButton = UIHelper.createButton("Save", e -> {
            String email = emailField.getText();
            String firstName = firstNameField.getText();
            String middleName = middleNameField.getText();
            String lastName = lastNameField.getText();
            String preferredName = preferredNameField.getText();

            if (email.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                UIHelper.setMessage(messageLabel, "Please fill in the required fields.", true);
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

        // Create the scene with CSS styling
        Scene scene = UIHelper.createStyledScene(vBox, 1920, 1080);
        stage.setTitle("Finish Setting Up Account");
        stage.setScene(scene);
        stage.show();
    }
}
