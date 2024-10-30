package app.page;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.HashSet;
import java.util.Set;

import app.HelpArticle;
import app.User;
import app.util.HelpArticleDatabase;
import app.util.UIHelper;

/**
 * The CreateEditArticlePage class allows admins and instructors to create or edit help articles.
 * Users can enter all the necessary fields and save the article to the database.
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
        TextField levelField = UIHelper.createTextField("LEVEL (Beginner, Intermediate, Advanced, Expert)");

        // Groups field
        TextField groupsField = UIHelper.createTextField("GROUPS (comma-separated)");

        // Reference Links field
        TextArea referenceLinksArea = UIHelper.createTextArea("REFERENCE LINKS (comma-separated)", 800, 100);

        // Message label for error messages
        Label messageLabel = UIHelper.createMessageLabel();

        // Pre-fill fields if editing
        if (article != null) {
            titleField.setText(article.getTitle());
            descriptionArea.setText(article.getDescription());
            bodyArea.setText(article.getBody());
            keywordsField.setText(String.join(", ", article.getKeywords()));
            levelField.setText(article.getLevel());
            groupsField.setText(String.join(", ", article.getGroups()));
            referenceLinksArea.setText(String.join(", ", article.getReferenceLinks()));
        }

        // Save button
        Button saveButton = UIHelper.createButton("Save", e -> {
            String title = titleField.getText();
            String description = descriptionArea.getText();
            String body = bodyArea.getText();
            String level = levelField.getText();

            if (title.isEmpty() || description.isEmpty() || body.isEmpty() || level.isEmpty()) {
                UIHelper.setMessage(messageLabel, "Please fill in the required fields.", true);
            } else {
                Set<String> keywords = parseInputToSet(keywordsField.getText());
                Set<String> groups = parseInputToSet(groupsField.getText());
                Set<String> referenceLinks = parseInputToSet(referenceLinksArea.getText());

                if (article == null) {
                    // Create new article
                    HelpArticle newArticle = new HelpArticle(title, description, body, level, keywords, groups, referenceLinks, user.getUsername());
                    HelpArticleDatabase.addArticle(newArticle);
                } else {
                    // Update existing article
                    article.setTitle(title);
                    article.setDescription(description);
                    article.setBody(body);
                    article.setLevel(level);
                    article.setKeywords(keywords);
                    article.setGroups(groups);
                    article.setReferenceLinks(referenceLinks);
                    HelpArticleDatabase.updateArticle(article);
                }

                // Return to help articles page
                new HelpArticlesPage(stage, user).show();
            }
        });

        // Back button
        Button backButton = UIHelper.createButton("Back", e -> new HelpArticlesPage(stage, user).show());

        // Layout using VBox
        VBox vBox = new VBox(20, titleLabel, titleField, descriptionArea, bodyArea, keywordsField, levelField, groupsField, referenceLinksArea, saveButton, backButton, messageLabel);
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
        for (String item : input.split(",")) {
            result.add(item.trim());
        }
        return result;
    }
}
