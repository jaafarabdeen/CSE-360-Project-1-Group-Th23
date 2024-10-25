// HelpArticlesPage.java
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The HelpArticlesPage class allows users to view and manage help articles.
 * Admins and instructors can create, edit, and delete articles.
 * Students can view articles based on their level.
 * 
 * Author:
 *     -Ayush Kaushik
 */
public class HelpArticlesPage {
    private Stage stage;
    private User user;

    public HelpArticlesPage(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
    }

    /**
     * Displays the help articles UI and handles user interactions.
     */
    public void show() {
        // Title label
        Label titleLabel = new Label("Help Articles");
        titleLabel.setFont(new Font("Arial", 56));
        titleLabel.setTextFill(Color.web("#ffffff"));

        // Search bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.setMaxWidth(600);
        searchField.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #000000; -fx-font-size: 24;");

        // ListView to display articles
        ListView<HelpArticle> articlesListView = new ListView<>();
        articlesListView.setPrefSize(800, 600);
        articlesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Load articles based on user level and search keywords
        List<HelpArticle> allArticles = new ArrayList<>(HelpArticleDatabase.getArticles());
        List<HelpArticle> filteredArticles = filterArticles(allArticles, searchField.getText());
        articlesListView.getItems().addAll(filteredArticles);

        // Update articles when search text changes
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            articlesListView.getItems().clear();
            articlesListView.getItems().addAll(filterArticles(allArticles, newValue));
        });

        // Define how each article is displayed
        articlesListView.setCellFactory(param -> new HelpArticleCell());

        // Context menu for admins and instructors
        if (user.hasRole("Admin") || user.hasRole("Instructor")) {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem editArticleItem = new MenuItem("Edit Article");
            MenuItem deleteArticleItem = new MenuItem("Delete Article");
            contextMenu.getItems().addAll(editArticleItem, deleteArticleItem);

            articlesListView.setContextMenu(contextMenu);

            // Handle edit article action
            editArticleItem.setOnAction(e -> {
                HelpArticle selectedArticle = articlesListView.getSelectionModel().getSelectedItem();
                if (selectedArticle != null) {
                    CreateEditArticlePage editPage = new CreateEditArticlePage(stage, user, selectedArticle);
                    editPage.show();
                }
            });

            // Handle delete article action
            deleteArticleItem.setOnAction(e -> {
                HelpArticle selectedArticle = articlesListView.getSelectionModel().getSelectedItem();
                if (selectedArticle != null) {
                    HelpArticleDatabase.removeArticle(selectedArticle.getId());
                    articlesListView.getItems().remove(selectedArticle);
                }
            });
        }

        // Double-click to view article details
        articlesListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                HelpArticle selectedArticle = articlesListView.getSelectionModel().getSelectedItem();
                if (selectedArticle != null) {
                    ViewArticlePage viewPage = new ViewArticlePage(stage, user, selectedArticle);
                    viewPage.show();
                }
            }
        });

        // Create Article button for admins and instructors
        Button createArticleButton = new Button("Create Article");
        createArticleButton.setPrefWidth(600);
        createArticleButton.setPrefHeight(50);
        createArticleButton.setStyle("-fx-background-color: #5865F2; -fx-text-fill: white; -fx-font-size: 24;");
        createArticleButton.setOnAction(e -> {
            CreateEditArticlePage createPage = new CreateEditArticlePage(stage, user, null);
            createPage.show();
        });

        // Back button
        Button backButton = new Button("Back");
        backButton.setPrefWidth(300);
        backButton.setPrefHeight(50);
        backButton.setStyle("-fx-background-color: #FF5555; -fx-text-fill: white; -fx-font-size: 24;");
        backButton.setOnAction(e -> {
            if (user.hasRole("Admin")) {
                AdminPage adminPage = new AdminPage(stage, user);
                adminPage.show();
            } else {
                DashboardPage dashboardPage = new DashboardPage(stage, user);
                dashboardPage.show();
            }
        });

        // Layout using VBox
        VBox vBox;
        if (user.hasRole("Admin") || user.hasRole("Instructor")) {
            vBox = new VBox(20, titleLabel, searchField, createArticleButton, articlesListView, backButton);
        } else {
            vBox = new VBox(20, titleLabel, searchField, articlesListView, backButton);
        }
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(30));
        vBox.setStyle("-fx-background-color: #3b5998;");

        // Create the scene with 1920x1080 resolution
        Scene scene = new Scene(vBox, 1920, 1080);
        stage.setTitle("Help Articles");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Filters articles based on user level and search keywords.
     * 
     * @param articles The list of all articles.
     * @param keyword The search keyword.
     * @return The list of filtered articles.
     */
    private List<HelpArticle> filterArticles(List<HelpArticle> articles, String keyword) {
        return articles.stream()
                .filter(article -> article.getLevel().equalsIgnoreCase(user.getLevel()) || user.hasRole("Admin") || user.hasRole("Instructor"))
                .filter(article -> keyword == null || keyword.isEmpty() || article.getTitle().toLowerCase().contains(keyword.toLowerCase())
                        || article.getDescription().toLowerCase().contains(keyword.toLowerCase())
                        || article.getKeywords().stream().anyMatch(k -> k.toLowerCase().contains(keyword.toLowerCase())))
                .collect(Collectors.toList());
    }
}
