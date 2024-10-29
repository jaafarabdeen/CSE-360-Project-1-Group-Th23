package app;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.geometry.Insets;

/**
 * The ViewArticlePage class allows users to view the details of a help article.
 * Displays the title, description, body, and other relevant information.
 * 
 * Author:
 *     - Ayush Kaushik
 */
public class ViewArticlePage {
    private final Stage stage;
    private final User user;
    private final HelpArticle article;

    public ViewArticlePage(Stage stage, User user, HelpArticle article) {
        this.stage = stage;
        this.user = user;
        this.article = article;
    }

    /**
     * Displays the article details UI.
     */
    public void show() {
        String labelStyle = "-fx-text-fill: #ffffff;";
        String buttonStyle = "-fx-background-color: #FF5555; -fx-text-fill: white; -fx-font-size: 24;";

        // Title label
        Label titleLabel = new Label(article.getTitle());
        titleLabel.setFont(new Font("Arial", 56));
        titleLabel.setStyle(labelStyle);

        // Description label
        Label descriptionLabel = new Label(article.getDescription());
        descriptionLabel.setFont(new Font("Arial", 28));
        descriptionLabel.setStyle(labelStyle);

        // Body area
        TextArea bodyArea = new TextArea(article.getBody());
        bodyArea.setWrapText(true);
        bodyArea.setEditable(false);
        bodyArea.setMaxWidth(800);
        bodyArea.setMaxHeight(400);
        bodyArea.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #000000; -fx-font-size: 24;");

        // Back button
        Button backButton = new Button("Back");
        backButton.setPrefWidth(300);
        backButton.setPrefHeight(50);
        backButton.setStyle(buttonStyle);
        backButton.setOnAction(e -> new HelpArticlesPage(stage, user).show());

        // Layout using VBox
        VBox vBox = new VBox(20, titleLabel, descriptionLabel, bodyArea, backButton);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(30));
        vBox.setStyle("-fx-background-color: #3b5998;");

        // Create the scene with 1920x1080 resolution
        Scene scene = new Scene(vBox, 1920, 1080);
        stage.setTitle("View Article");
        stage.setScene(scene);
        stage.show();
    }
}
