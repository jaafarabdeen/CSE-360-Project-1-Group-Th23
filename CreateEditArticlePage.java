// CreateEditArticlePage.java
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
import java.util.HashSet;
import java.util.Set;

/**
 * The CreateEditArticlePage class allows admins and instructors to create or edit help articles.
 * Users can enter all the necessary fields and save the article to the database.
 * 
 * Author:
 *     - Ayush Kaushik
 */
public class CreateEditArticlePage {
    private Stage stage;
    private User user;
    private HelpArticle article; // null if creating a new article

    public CreateEditArticlePage(Stage stage, User user, HelpArticle article) {
        this.stage = stage;
        this.user = user;
        this.article = article;
    }

    /**
     * Displays the create/edit article UI and handles user interactions.
     */
    public void show() {
        // Title label
        Label titleLabel = new Label(article == null ? "Create New Article" : "Edit Article");
        titleLabel.setFont(new Font("Arial", 56));
        titleLabel.setTextFill(Color.web("#ffffff"));

        // Title field
        TextField titleField = new TextField();
        titleField.setPromptText("TITLE");
        titleField.setMaxWidth(800);
        titleField.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #000000; -fx-font-size: 24;");

        // Description field
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("DESCRIPTION");
        descriptionArea.setMaxWidth(800);
        descriptionArea.setMaxHeight(100);
        descriptionArea.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #000000; -fx-font-size: 24;");

        // Body field
        TextArea bodyArea = new TextArea();
        bodyArea.setPromptText("BODY");
        bodyArea.setMaxWidth(800);
        bodyArea.setMaxHeight(300);
        bodyArea.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #000000; -fx-font-size: 24;");

        // Keywords field
        TextField keywordsField = new TextField();
        keywordsField.setPromptText("KEYWORDS (comma-separated)");
        keywordsField.setMaxWidth(800);
        keywordsField.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #000000; -fx-font-size: 24;");

        // Level field
        TextField levelField = new TextField();
        levelField.setPromptText("LEVEL (Beginner, Intermediate, Advanced, Expert)");
        levelField.setMaxWidth(800);
        levelField.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #000000; -fx-font-size: 24;");

        // Groups field
        TextField groupsField = new TextField();
        groupsField.setPromptText("GROUPS (comma-separated)");
        groupsField.setMaxWidth(800);
        groupsField.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #000000; -fx-font-size: 24;");

        // Message label for error messages
        Label messageLabel = new Label();
        messageLabel.setTextFill(Color.web("#ff5555"));
        messageLabel.setFont(new Font("Arial", 28));

        // Pre-fill fields if editing
        if (article != null) {
            titleField.setText(article.getTitle());
            descriptionArea.setText(article.getDescription());
            bodyArea.setText(article.getBody());
            keywordsField.setText(String.join(", ", article.getKeywords()));
            levelField.setText(article.getLevel());
            groupsField.setText(String.join(", ", article.getGroups()));
        }

        // Save button
        Button saveButton = new Button("Save");
        saveButton.setPrefWidth(600);
        saveButton.setPrefHeight(50);
        saveButton.setStyle("-fx-background-color: #5865F2; -fx-text-fill: white; -fx-font-size: 24;");
        saveButton.setOnAction(e -> {
            String title = titleField.getText();
            String description = descriptionArea.getText();
            String body = bodyArea.getText();
            String keywordsText = keywordsField.getText();
            String level = levelField.getText();
            String groupsText = groupsField.getText();

            if (title.isEmpty() || description.isEmpty() || body.isEmpty() || level.isEmpty()) {
                messageLabel.setText("Please fill in the required fields.");
            } else {
                Set<String> keywords = new HashSet<>();
                for (String keyword : keywordsText.split(",")) {
                    keywords.add(keyword.trim());
                }

                Set<String> groups = new HashSet<>();
                for (String group : groupsText.split(",")) {
                    groups.add(group.trim());
                }

                if (article == null) {
                    // Create new article
                    HelpArticle newArticle = new HelpArticle(title, description, body, level, keywords, groups, user.getUsername());
                    HelpArticleDatabase.addArticle(newArticle);
                } else {
                    // Update existing article
                    article.setTitle(title);
                    article.setDescription(description);
                    article.setBody(body);
                    article.setLevel(level);
                    article.setKeywords(keywords);
                    article.setGroups(groups);
                    HelpArticleDatabase.updateArticle(article);
                }

                // Return to help articles page
                HelpArticlesPage helpArticlesPage = new HelpArticlesPage(stage, user);
                helpArticlesPage.show();
            }
        });

        // Layout using VBox
        VBox vBox = new VBox(20, titleLabel, titleField, descriptionArea, bodyArea, keywordsField, levelField, groupsField, saveButton, messageLabel);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(30));
        vBox.setStyle("-fx-background-color: #3b5998;");

        // Create the scene with 1920x1080 resolution
        Scene scene = new Scene(vBox, 1920, 1080);
        stage.setTitle(article == null ? "Create Article" : "Edit Article");
        stage.setScene(scene);
        stage.show();
    }
}
