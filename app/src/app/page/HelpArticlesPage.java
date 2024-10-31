package app.page;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import app.HelpArticle;
import app.User;
import app.cell.HelpArticleCell;
import app.util.DatabaseHelper;
import app.util.HelpArticleDatabase;
import app.util.UIHelper;

/**
 * The HelpArticlesPage class allows users to view and manage help articles.
 * Admins and instructors can create, edit, and delete articles.
 * Students can view articles based on their level.
 * 
 * Author:
 *     - Ayush Kaushik
 *     - Jaafar Abdeen
 */
public class HelpArticlesPage {
    private final Stage stage;
    private final User user;
    private final DatabaseHelper databaseHelper;
    private final List<HelpArticle> allArticles;

    public HelpArticlesPage(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
        DatabaseHelper tempDatabaseHelper = null;
        try {
            tempDatabaseHelper = new DatabaseHelper();
            tempDatabaseHelper.connectToDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.databaseHelper = tempDatabaseHelper;
        this.allArticles = new ArrayList<>(HelpArticleDatabase.getArticles());
    }

    /**
     * Displays the help articles UI and handles user interactions.
     */
    public void show() {
        // Title label
        Label titleLabel = new Label("Help Articles");
        titleLabel.getStyleClass().add("label-title");

        // Search bar
        TextField searchField = UIHelper.createTextField("Search...");

        // ListView to display articles
        ListView<HelpArticle> articlesListView = new ListView<>();
        articlesListView.setPrefSize(800, 600);
        articlesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        articlesListView.setCellFactory(param -> new HelpArticleCell());

        // Load initial filtered articles
        articlesListView.getItems().setAll(filterArticles(searchField.getText()));

        // Update articles on search field change
        searchField.textProperty().addListener((observable, oldValue, newValue) -> 
            articlesListView.getItems().setAll(filterArticles(newValue))
        );

        // Backup button
        Button backupButton = UIHelper.createButton("Backup Articles", e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Backup File Location");
            fileChooser.setInitialFileName("Backup-" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd-HH")));
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                try {
                    databaseHelper.backupArticles(file.getAbsolutePath());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Restore button
        Button restoreButton = UIHelper.createButton("Restore Articles", e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Backup File");
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                // Display a dialog to choose the restore method (merge or overwrite)
                Alert methodAlert = new Alert(Alert.AlertType.CONFIRMATION);
                methodAlert.setTitle("Restore Options");
                methodAlert.setHeaderText("Choose Restore Method");
                methodAlert.setContentText("Do you want to merge with current entries or overwrite them?");
                
                ButtonType mergeOption = new ButtonType("Merge");
                ButtonType overwriteOption = new ButtonType("Overwrite");
                ButtonType cancelOption = new ButtonType("Cancel", ButtonType.CANCEL.getButtonData());

                methodAlert.getButtonTypes().setAll(mergeOption, overwriteOption, cancelOption);

                methodAlert.showAndWait().ifPresent(methodResponse -> {
                    if (methodResponse == cancelOption) return;
                    boolean merge = methodResponse == mergeOption;

                    // Display another dialog to specify group filter or restore all
                    TextInputDialog groupDialog = new TextInputDialog();
                    groupDialog.setTitle("Group Filter");
                    groupDialog.setHeaderText("Restore Specific Group");
                    groupDialog.setContentText("Enter group name to filter by (leave blank for all):");

                    groupDialog.showAndWait().ifPresent(group -> {
                        try {
                            // Call restoreArticles with the chosen options
                            databaseHelper.restoreArticles(file.getAbsolutePath(), merge, group.isEmpty() ? null : group);
                            // Reload articles from the database after restore
                            allArticles.clear();
                            allArticles.addAll(HelpArticleDatabase.getArticles());
                            // Refresh ListView with the updated articles
                            articlesListView.getItems().setAll(filterArticles(searchField.getText()));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                });
            }
        });

        // Organize backup and restore buttons in an HBox
        HBox buttonBox = new HBox(20, backupButton, restoreButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Context menu for admins and instructors
        if (user.hasRole("Admin") || user.hasRole("Instructor")) {
            ContextMenu contextMenu = createContextMenu(articlesListView);
            articlesListView.setContextMenu(contextMenu);
        }

        // Double-click to view article details
        articlesListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                HelpArticle selectedArticle = articlesListView.getSelectionModel().getSelectedItem();
                if (selectedArticle != null) {
                    new ViewArticlePage(stage, user, selectedArticle).show();
                }
            }
        });

        // Create Article button for admins and instructors
        Button createArticleButton = UIHelper.createButton("Create Article", e -> 
            new CreateEditArticlePage(stage, user, null).show()
        );

        // Back button
        Button backButton = UIHelper.createButton("Back", e -> {
            if (user.hasRole("Admin")) {
                new AdminPage(stage, user).show();
            } else {
                new DashboardPage(stage, user).show();
            }
        });

        // Layout using VBox
        VBox vBox;
        if (user.hasRole("Admin") || user.hasRole("Instructor")) {
            vBox = new VBox(20, titleLabel, searchField, createArticleButton, buttonBox, articlesListView, backButton);
        } else {
            vBox = new VBox(20, titleLabel, searchField, articlesListView, backButton);
        }
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(30));

        // Create the scene with CSS styling
        Scene scene = UIHelper.createStyledScene(vBox, 1920, 1080);
        stage.setTitle("Help Articles");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Filters articles based on user level and search keywords.
     * 
     * @param keyword The search keyword.
     * @return The list of filtered articles.
     */
    private List<HelpArticle> filterArticles(String keyword) {
        return allArticles.stream()
                .filter(article -> article.getLevel().equalsIgnoreCase(user.getLevel()) || user.hasRole("Admin") || user.hasRole("Instructor"))
                .filter(article -> keyword == null || keyword.isEmpty() ||
                        article.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        article.getDescription().toLowerCase().contains(keyword.toLowerCase()) ||
                        article.getKeywords().stream().anyMatch(k -> k.toLowerCase().contains(keyword.toLowerCase())))
                .collect(Collectors.toList());
    }

    private ContextMenu createContextMenu(ListView<HelpArticle> articlesListView) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editArticleItem = new MenuItem("Edit Article");
        MenuItem deleteArticleItem = new MenuItem("Delete Article");
        contextMenu.getItems().addAll(editArticleItem, deleteArticleItem);

        editArticleItem.setOnAction(e -> {
            HelpArticle selectedArticle = articlesListView.getSelectionModel().getSelectedItem();
            if (selectedArticle != null) {
                new CreateEditArticlePage(stage, user, selectedArticle).show();
            }
        });

        deleteArticleItem.setOnAction(e -> {
            HelpArticle selectedArticle = articlesListView.getSelectionModel().getSelectedItem();
            if (selectedArticle != null) {
                HelpArticleDatabase.removeArticle(selectedArticle.getId());
                articlesListView.getItems().remove(selectedArticle);
            }
        });

        return contextMenu;
    }
}
