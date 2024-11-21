package app.page;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import app.User;
import app.util.DatabaseHelper;
import app.util.UIHelper;

/**
 * The SendMessagePage class allows students to send generic or specific help messages to the system.
 * Students can input their message, and optionally, the search terms they used if it's a specific message.
 *
 * Author:
 *     - Jaafar Abdeen
 */
public class SendMessagePage {
    private final Stage stage;
    private final User user;
    private final String messageType; // "Generic" or "Specific"
    private final DatabaseHelper databaseHelper;
    private TextField searchTermsField;

    /**
     * Constructor for SendMessagePage.
     *
     * @param stage       The primary stage of the application.
     * @param user        The user who is sending the message.
     * @param messageType The type of message ("Generic" or "Specific").
     * @throws Exception 
     */
    public SendMessagePage(Stage stage, User user, String messageType) throws Exception {
        this.stage = stage;
        this.user = user;
        this.messageType = messageType;
        this.databaseHelper = new DatabaseHelper();
        try {
            this.databaseHelper.connectToDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays the SendMessagePage UI and handles user interactions.
     */
    public void show() {
        stage.setOnCloseRequest(event -> databaseHelper.closeConnection());

        // Title label
        Label titleLabel = new Label("Send " + messageType + " Message");
        titleLabel.getStyleClass().add("label-title");

        // Message input area
        TextArea messageArea = UIHelper.createTextArea("Type your message here...", 800, 400);

        // Search terms input (only for specific messages)
        if ("Specific".equals(messageType)) {
            searchTermsField = UIHelper.createTextField("Enter search terms you used");
        }

        // Send button
        Button sendButton = UIHelper.createButton("Send", e -> {
            String messageContent = messageArea.getText();
            if (messageContent.isEmpty()) {
                UIHelper.showErrorDialog("Error", "Please enter a message.");
            } else {
                String searchTerms = "";
                if ("Specific".equals(messageType) && searchTermsField != null) {
                    searchTerms = searchTermsField.getText();
                }
                databaseHelper.storeHelpMessage(user.getUsername(), messageType, messageContent, searchTerms);
                UIHelper.showInfoDialog("Success", "Your message has been sent.");
                new DashboardPage(stage, user).show();
            }
        });

        // Back button
        Button backButton = UIHelper.createButton("Back", e -> new DashboardPage(stage, user).show());

        // Layout using VBox
        VBox vBox = new VBox(20, titleLabel);
        if (searchTermsField != null) {
            vBox.getChildren().add(searchTermsField);
        }
        vBox.getChildren().addAll(messageArea, sendButton, backButton);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(30));

        // Create the scene with CSS styling
        Scene scene = UIHelper.createStyledScene(vBox, 800, 600);
        stage.setTitle("Send Message");
        stage.setScene(scene);
        stage.show();
    }
}
