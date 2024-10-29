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
import javafx.scene.control.Hyperlink;
import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * The ViewArticlePage class allows users to view the details of a help article.
 * Displays the title, description, body, and other relevant information.
 * 
 * Author:
 *     - Pragya Kumari
 *     - Aaryan Gaur
 */
public class ViewArticlePage {
    private Stage stage;
    private User user;
    private HelpArticle article;

    public ViewArticlePage(Stage stage, User user, HelpArticle article) {
        this.stage = stage;
        this.user = user;
        this.article = article;
    }

    /**
     * Displays the article details UI.
     */
    public void show() {
        // Title label
        Label titleLabel = new Label(article.getTitle());
        titleLabel.setFont(new Font("Arial", 56));
        titleLabel.setTextFill(Color.web("#ffffff"));

        // Description label
        Label descriptionLabel = new Label(article.getDescription());
        descriptionLabel.setFont(new Font("Arial", 28));
        descriptionLabel.setTextFill(Color.web("#ffffff"));

        // Body area
        TextArea bodyArea = new TextArea(article.getBody());
        bodyArea.setWrapText(true);
        bodyArea.setEditable(false);
        bodyArea.setMaxWidth(800);
        bodyArea.setMaxHeight(400);
        bodyArea.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #000000; -fx-font-size: 24;");

        // Reference links
        VBox linksBox = new VBox();
        linksBox.setAlignment(Pos.CENTER);
        linksBox.setSpacing(10);

        // Iterate through the reference links if they exist
        for (String link : article.getReferenceLinks()) {
            Hyperlink hyperlink = new Hyperlink(link);
            hyperlink.setOnAction(e -> openLink(link));
            linksBox.getChildren().add(hyperlink);
        }

        // Back button
        Button backButton = new Button("Back");
        backButton.setPrefWidth(300);
        backButton.setPrefHeight(50);
        backButton.setStyle("-fx-background-color: #FF5555; -fx-text-fill: white; -fx-font-size: 24;");
        backButton.setOnAction(e -> {
            HelpArticlesPage helpArticlesPage = new HelpArticlesPage(stage, user);
            helpArticlesPage.show();
        });

        // Layout using VBox
        VBox vBox = new VBox(20, titleLabel, descriptionLabel, bodyArea, linksBox, backButton);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(30));
        vBox.setStyle("-fx-background-color: #3b5998;");

        // Create the scene with 1920x1080 resolution
        Scene scene = new Scene(vBox, 1920, 1080);
        stage.setTitle("View Article");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Opens the given link in the default web browser.
     * @param link The URL to open.
     */
    private void openLink(String link) {
        try {
            Desktop.getDesktop().browse(new URI(link));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
