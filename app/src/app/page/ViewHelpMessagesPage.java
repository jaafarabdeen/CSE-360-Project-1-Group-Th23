package app.page;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import app.User;
import app.util.DatabaseHelper;
import app.util.UIHelper;
import java.util.List;

/**
 * The ViewHelpMessagesPage class allows admins or instructors to view help messages sent by students.
 * Messages include both generic and specific types, along with any search terms if provided.
 *
 * Author:
 *     - Jaafar Abdeen
 *     - Ayush Kaushik
 */
public class ViewHelpMessagesPage {
    private final Stage stage;
    private final User user;
    private final DatabaseHelper databaseHelper;

    /**
     * Constructor for ViewHelpMessagesPage.
     *
     * @param stage The primary stage of the application.
     * @param user  The admin or instructor viewing the messages.
     * @throws Exception 
     */
    public ViewHelpMessagesPage(Stage stage, User user) throws Exception {
        this.stage = stage;
        this.user = user;
        this.databaseHelper = new DatabaseHelper();
        try {
            this.databaseHelper.connectToDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays the ViewHelpMessagesPage UI and handles user interactions.
     */
    public void show() {
        stage.setOnCloseRequest(event -> databaseHelper.closeConnection());

        // Title label
        Label titleLabel = new Label("Help Messages");
        titleLabel.getStyleClass().add("label-title");

        // ListView to display messages
        ListView<String> messagesListView = new ListView<>();
        messagesListView.setPrefSize(800, 600);

        // Fetch messages from the database
        List<String> messages = databaseHelper.getAllHelpMessages();
        messagesListView.getItems().addAll(messages);

        // Back button
        Button backButton = UIHelper.createButton("Back", e -> {
            if (user.hasRole("Admin")) {
                new AdminPage(stage, user).show();
            } else {
                new DashboardPage(stage, user).show();
            }
        });

        // Layout using VBox
        VBox vBox = new VBox(20, titleLabel, messagesListView, backButton);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(30));

        // Create the scene with CSS styling
        Scene scene = UIHelper.createStyledScene(vBox, 900, 700);
        stage.setTitle("View Help Messages");
        stage.setScene(scene);
        stage.show();
    }
}
