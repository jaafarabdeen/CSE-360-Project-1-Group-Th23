package app.page;

import javafx.stage.Stage;
import Encryption.EncryptionHelper;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.geometry.Insets;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import app.HelpArticle;
import app.User;
import app.util.UIHelper;
import app.util.DatabaseHelper;

/**
 * The ViewArticlePage class allows users to view the details of a help article.
 * Displays the title, description, body, and other relevant information.
 * Handles access control for articles in special access groups.
 * 
 * Author:
 *     - Ayush Kaushik
 *     - Pragya Kumari
 *     - Aaryan Gaur
 */
public class ViewArticlePage {
    private final Stage stage;
    private final User user;
    private final HelpArticle article;
    private final DatabaseHelper databaseHelper;

    public ViewArticlePage(Stage stage, User user, HelpArticle article) {
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

    public void show() {
        // Check if user has access to the article
        try {
            HelpArticle accessibleArticle = databaseHelper.getArticle(article.getId(), user);
            if (accessibleArticle == null) {
                showAccessDenied();
                return;
            }

            String bodyText;
            try {
            	EncryptionHelper amr = new EncryptionHelper();
                bodyText = amr.decryptBody(accessibleArticle.getBody());
            } catch (Exception e) {
                // Fallback to original body if decryption fails
                bodyText = accessibleArticle.getBody();
            }

            // Title label
            Label titleLabel = new Label(article.getTitle());
            titleLabel.getStyleClass().add("label-title");

            // Level label
            Label levelLabel = new Label(article.getLevel());
            levelLabel.getStyleClass().add("label-subheading");

            // Description label
            Label descriptionLabel = new Label(article.getDescription());
            descriptionLabel.getStyleClass().add("label-body");

            // Body area
            TextArea bodyArea = new TextArea(bodyText);
            bodyArea.setWrapText(true);
            bodyArea.setEditable(false);
            bodyArea.setMaxWidth(800);
            bodyArea.setMaxHeight(400);
            bodyArea.getStyleClass().add("text-area-body");

            // Reference links container
            HBox linksBox = new HBox(10);
            linksBox.setAlignment(Pos.CENTER);
            article.getReferenceLinks().forEach(link -> linksBox.getChildren().add(createHyperlink(link)));

            // Back button with custom red style
            Button backButton = UIHelper.createButton("Back", e -> new HelpArticlesPage(stage, user).show(), "-fx-background-color: #FF5555; -fx-text-fill: white; -fx-font-size: 24;");

            // Layout using VBox
            VBox vBox = new VBox(20, levelLabel, titleLabel, descriptionLabel, bodyArea, linksBox, backButton);
            vBox.setAlignment(Pos.CENTER);
            vBox.setPadding(new Insets(30));

            // Create the scene with CSS styling
            Scene scene = UIHelper.createStyledScene(vBox, 1920, 1080);
            stage.setTitle("View Article");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAccessDenied();
        }
    }

    /**
     * Creates a clickable hyperlink styled as a button.
     * 
     * @param link The URL of the hyperlink.
     * @return the styled Hyperlink
     */
    private Hyperlink createHyperlink(String link) {
        Hyperlink hyperlink = new Hyperlink(link);
        hyperlink.getStyleClass().add("link-button");
        hyperlink.setOnAction(e -> openLink(link));
        return hyperlink;
    }

    /**
     * Opens the given link in the default web browser.
     * @param link The URL to open.
     */
    private void openLink(String link) {
        try {
            Desktop.getDesktop().browse(new URI(link));
        } catch (URISyntaxException | IOException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Invalid link");
            alert.setHeaderText("The link you clicked on is not valid.");
            alert.showAndWait();
        }
    }

    /**
     * Displays an access denied message.
     */
    private void showAccessDenied() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Access Denied");
        alert.setHeaderText("You do not have permission to view this article.");
        alert.showAndWait();
        new HelpArticlesPage(stage, user).show();
    }
}
