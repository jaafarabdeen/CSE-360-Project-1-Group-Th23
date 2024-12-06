package app.page;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import java.sql.SQLException;
import java.util.List;

import app.HelpArticle;
import app.User;
import app.util.DatabaseHelper;
import app.util.UIHelper;

public class BookmarksPage {
    private final Stage stage;
    private final User user;
    private DatabaseHelper databaseHelper;

    public BookmarksPage(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
        try {
            this.databaseHelper = new DatabaseHelper();
            this.databaseHelper.connectToDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show() {
        Label titleLabel = new Label("Your Bookmarks");
        titleLabel.getStyleClass().add("label-title");

        VBox articlesBox = new VBox(20);
        articlesBox.setAlignment(Pos.CENTER);

        try {
            List<Long> bookmarkedIds = databaseHelper.getBookmarkedArticleIds(user);
            if (bookmarkedIds.isEmpty()) {
                Label noBookmarksLabel = new Label("You have no bookmarked articles.");
                noBookmarksLabel.getStyleClass().add("label-body");
                articlesBox.getChildren().add(noBookmarksLabel);
            } else {
                for (Long articleId : bookmarkedIds) {
                    HelpArticle article = databaseHelper.getArticle(articleId, user);
                    if (article != null) {
                        // Each row: Article title label, View button, Remove bookmark button
                        Label articleTitle = new Label(article.getTitle());
                        articleTitle.getStyleClass().add("label-subheading");

                        Button viewButton = UIHelper.createButton("View", e -> new ViewArticlePage(stage, user, article).show());
                        Button removeButton = UIHelper.createButton("Remove Bookmark", e -> {
                            try {
                                databaseHelper.removeBookmark(user, articleId);
                                show(); // Refresh the page after removal
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        });

                        HBox articleRow = new HBox(20, articleTitle, viewButton, removeButton);
                        articleRow.setAlignment(Pos.CENTER);
                        articlesBox.getChildren().add(articleRow);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Back button
        Button backButton = UIHelper.createButton("Back", e -> new DashboardPage(stage, user).show());

        VBox vBox = new VBox(40, titleLabel, articlesBox, backButton);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(30));

        Scene scene = UIHelper.createStyledScene(vBox, 1920, 1080);
        stage.setTitle("Your Bookmarks");
        stage.setScene(scene);
        stage.show();
    }
}
