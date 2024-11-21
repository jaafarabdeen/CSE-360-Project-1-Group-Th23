package app.page;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import app.HelpArticle;
import app.User;
import app.util.DatabaseHelper;
import app.util.Group;
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
    private ChoiceBox<String> contentLevelChoiceBox;
    private ChoiceBox<String> groupChoiceBox;
    private String currentGroup = "All";
    private Map<Integer, HelpArticle> articleSequenceMap = new HashMap<>();
    private TextField searchField;
    private Label searchResultLabel;
    private ListView<HelpArticle> articlesListView;


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
        this.allArticles = new ArrayList<>(HelpArticleDatabase.getArticles(user));
    }

    /**
     * Displays the help articles UI and handles user interactions.
     */
    public void show() {
        stage.setOnCloseRequest(event -> databaseHelper.closeConnection());
        // Title label
        Label titleLabel = new Label("Help Articles");
        titleLabel.getStyleClass().add("label-subtitle");

        // Content Level ChoiceBox
        contentLevelChoiceBox = new ChoiceBox<>();
        contentLevelChoiceBox.getItems().addAll("All", "Beginner", "Intermediate", "Advanced", "Expert");
        contentLevelChoiceBox.setValue(user.getContentLevelPreference() != null ? user.getContentLevelPreference() : "All");

        // Update user content level preference when selection changes
        contentLevelChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            user.setContentLevelPreference(newValue);
            updateArticleList(searchField.getText());
        });

        // Group ChoiceBox
        groupChoiceBox = new ChoiceBox<>();
        groupChoiceBox.getItems().add("All");

        // Get list of groups the user has access to
        try {
            List<Group> userGroups = databaseHelper.getGroupsForUser(user);
            for (Group group : userGroups) {
                groupChoiceBox.getItems().add(group.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        groupChoiceBox.setValue("All");

        // Update current group when selection changes
        groupChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            currentGroup = newValue;
            updateArticleList(searchField.getText());
        });

        // Search bar
        searchField = UIHelper.createTextField("Search...");

        // Search result label
        searchResultLabel = new Label();
        searchResultLabel.getStyleClass().add("label-subheading");

        // ListView to display articles
        articlesListView = new ListView<>();
        articlesListView.setPrefSize(800, 360);
        articlesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Define how each article is displayed in the list
        articlesListView.setCellFactory((ListView<HelpArticle> param) -> new ListCell<HelpArticle>() {
            @Override
            protected void updateItem(HelpArticle item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    int seqNum = getIndex() + 1;
                    String shortForm = seqNum + ". " + item.getTitle() + " by " + item.getAuthorUsername() + "\n" + item.getDescription();
                    setText(shortForm);
                }
            }
        });

        // Load initial filtered articles
        updateArticleList("");

        // Update articles on search field change
        searchField.textProperty().addListener((observable, oldValue, newValue) -> updateArticleList(newValue));

        // Sequence number input and view button
        TextField sequenceNumberField = new TextField();
        sequenceNumberField.setPromptText("Enter sequence number");
        
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
                            databaseHelper.restoreArticles(file.getAbsolutePath(), merge, group.isBlank() ? null : group);
                            // Reload articles from the database after restore
                            allArticles.clear();
                            allArticles.addAll(HelpArticleDatabase.getArticles(user));
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

        Button viewArticleButton = UIHelper.createButton("View Article", e -> {
            String seqNumStr = sequenceNumberField.getText();
            try {
                int seqNum = Integer.parseInt(seqNumStr);
                HelpArticle selectedArticle = articlesListView.getItems().get(seqNum - 1);
                if (selectedArticle != null) {
                    new ViewArticlePage(stage, user, selectedArticle).show();
                } else {
                    UIHelper.showErrorDialog("Invalid Sequence Number", "Please enter a valid sequence number.");
                }
            } catch (NumberFormatException ex) {
                UIHelper.showErrorDialog("Invalid Input", "Please enter a valid sequence number.");
            }
        });

        HBox viewArticleBox = new HBox(10, sequenceNumberField, viewArticleButton);
        viewArticleBox.setAlignment(Pos.CENTER);

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
            vBox = new VBox(15, titleLabel, contentLevelChoiceBox, groupChoiceBox, searchField, searchResultLabel, articlesListView, buttonBox, viewArticleBox, createArticleButton, backButton);
        } else {
            vBox = new VBox(20, titleLabel, contentLevelChoiceBox, groupChoiceBox, searchField, searchResultLabel, articlesListView, viewArticleBox, backButton);
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
                .filter(article -> {
                    if (!"All".equalsIgnoreCase(user.getContentLevelPreference())) {
                        return article.getLevel().equalsIgnoreCase(user.getContentLevelPreference());
                    }
                    return true;
                })
                .filter(article -> {
                    if (!"All".equalsIgnoreCase(currentGroup)) {
                        return currentGroup.equals(article.getGroupName());
                    }
                    return true;
                })
                .filter(article -> keyword == null || keyword.isEmpty() ||
                        article.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        article.getDescription().toLowerCase().contains(keyword.toLowerCase()) ||
                        article.getAuthorUsername().toLowerCase().contains(keyword.toLowerCase()) ||
                        article.getId() == parseLongSafely(keyword)
                )
                .collect(Collectors.toList());
    }

    private long parseLongSafely(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return -1;
        }
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
    
    private void updateArticleList(String keyword) {
        List<HelpArticle> filteredArticles = filterArticles(keyword);
        articlesListView.getItems().setAll(filteredArticles);

        // Update sequence mapping
        articleSequenceMap.clear();
        int sequenceNumber = 1;
        for (HelpArticle article : filteredArticles) {
            articleSequenceMap.put(sequenceNumber++, article);
        }

        // Compute counts
        Map<String, Long> levelCounts = filteredArticles.stream()
                .collect(Collectors.groupingBy(HelpArticle::getLevel, Collectors.counting()));

        long totalArticles = 0;
        StringBuilder countsBuilder = new StringBuilder();
        for (String level : Arrays.asList("Beginner", "Intermediate", "Advanced", "Expert")) {
            long count = levelCounts.getOrDefault(level, 0L);
            if (count > 0) {
                countsBuilder.append(level).append(": ").append(count).append("\n");
                totalArticles += count;
            }
        }
        countsBuilder.insert(0, "Total articles: " + totalArticles + "\n");

        searchResultLabel.setText("Current Group: " + currentGroup + "\n" + countsBuilder.toString());
    }

}
