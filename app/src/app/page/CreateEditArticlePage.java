package app.page;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import app.HelpArticle;
import app.User;
import app.util.UIHelper;
import app.util.DatabaseHelper;
import app.util.Group;

/**
 * The CreateEditArticlePage class allows admins and instructors to create or edit help articles.
 * Users can enter all the necessary fields and save the article to the database.
 * For articles in special access groups, the body will be encrypted.
 * 
 * Author:
 *     - Jaafar Abdeen
 *     - Ayush Kaushik
 *     - Pragya Kumari
 *     - Aaryan Gaur
 */
public class CreateEditArticlePage {
    private final Stage stage;
    private final User user;
    private final HelpArticle article; // null if creating a new article
    private final DatabaseHelper databaseHelper;

    public CreateEditArticlePage(Stage stage, User user, HelpArticle article) {
        this.stage = stage;
        this.user = user;
        this.article = article;
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
     * Displays the create/edit article UI and handles user interactions.
     */
    public void show() {
        stage.setOnCloseRequest(event -> databaseHelper.closeConnection());

        // Title label
        Label titleLabel = new Label(article == null ? "Create New Article" : "Edit Article");
        titleLabel.getStyleClass().add("label-title");

        // Title field
        TextField titleField = UIHelper.createTextField("TITLE");

        // Description field
        TextArea descriptionArea = UIHelper.createTextArea("DESCRIPTION", 800, 100);

        // Body field
        TextArea bodyArea = UIHelper.createTextArea("BODY", 800, 300);

        // Keywords field
        TextField keywordsField = UIHelper.createTextField("KEYWORDS (comma-separated)");

        // Level field
        ChoiceBox<String> levelChoiceBox = new ChoiceBox<>();
        levelChoiceBox.getItems().addAll("Beginner", "Intermediate", "Advanced", "Expert");

        // Reference Links field
        TextArea referenceLinksArea = UIHelper.createTextArea("REFERENCE LINKS (comma-separated)", 800, 100);

        // Special Access Group ChoiceBox
        ChoiceBox<String> groupChoiceBox = new ChoiceBox<>();
        groupChoiceBox.getItems().add("None");
        try {
            List<Group> groups = new ArrayList<>(databaseHelper.getAllGroups());
            for (Group g : groups) {
                groupChoiceBox.getItems().add(g.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        groupChoiceBox.setValue("None");

        // Message label for error messages
        Label messageLabel = UIHelper.createMessageLabel();

        // Pre-fill fields if editing
        if (article != null) {
            titleField.setText(article.getTitle());
            descriptionArea.setText(article.getDescription());
            bodyArea.setText(article.getBody());
            keywordsField.setText(String.join(", ", article.getKeywords()));
            levelChoiceBox.setValue(article.getLevel());
            referenceLinksArea.setText(String.join(", ", article.getReferenceLinks()));
            if (article.getGroupName() != null) {
                groupChoiceBox.setValue(article.getGroupName());
            } else {
                groupChoiceBox.setValue("None");
            }
        }

        // Save button
        Button saveButton = UIHelper.createButton("Save", e -> {
            String title = titleField.getText();
            String description = descriptionArea.getText();
            String body = bodyArea.getText();
            String level = levelChoiceBox.getValue();

            if (title.isEmpty() || description.isEmpty() || body.isEmpty() || level == null || level.isEmpty()) {
                UIHelper.setMessage(messageLabel, "Please fill in the required fields.", true);
            } else {
                Set<String> keywords = parseInputToSet(keywordsField.getText());
                Set<String> referenceLinks = parseInputToSet(referenceLinksArea.getText());
                String groupName = groupChoiceBox.getValue();
                if ("None".equals(groupName)) {
                    groupName = null;
                }

                boolean isEncrypted = groupName != null;

                if (article == null) {
                    // Create new article
                    HelpArticle newArticle = new HelpArticle(title, description, body, level, keywords, referenceLinks, user.getUsername(), groupName, isEncrypted);
                    try {
                        databaseHelper.registerArticle(newArticle);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    // Update existing article
                    article.setTitle(title);
                    article.setDescription(description);
                    article.setBody(body);
                    article.setLevel(level);
                    article.setKeywords(keywords);
                    article.setReferenceLinks(referenceLinks);
                    article.setGroupName(groupName);
                    article.setEncrypted(isEncrypted);
                    try {
                        databaseHelper.updateArticle(article);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                // Return to help articles page
                new HelpArticlesPage(stage, user).show();
            }
        });

        // Back button
        Button backButton = UIHelper.createButton("Back", e -> new HelpArticlesPage(stage, user).show());

        // Layout using VBox
        VBox vBox = new VBox(20, titleLabel, titleField, descriptionArea, bodyArea, keywordsField, levelChoiceBox, groupChoiceBox, referenceLinksArea, saveButton, backButton, messageLabel);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(30));

        // Create the scene with CSS styling
        Scene scene = UIHelper.createStyledScene(vBox, 1920, 1080);
        stage.setTitle(article == null ? "Create Article" : "Edit Article");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Helper method to parse comma-separated input strings into a Set of trimmed strings.
     * 
     * @param input the comma-separated input string
     * @return a Set of trimmed strings
     */
    private Set<String> parseInputToSet(String input) {
        Set<String> result = new HashSet<>();
        if (input != null && !input.trim().isEmpty()) {
            for (String item : input.split(",")) {
                result.add(item.trim());
            }
        }
        return result;
    }
}
