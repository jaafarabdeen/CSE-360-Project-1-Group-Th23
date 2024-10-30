package app.cell;

import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import app.HelpArticle;
import javafx.geometry.Pos;

/**
 * Custom ListCell to display help article information in the articles list.
 * Shows the title and description of each article.
 * 
 * Author:
 *     - Ayush Kaushik
 */
public class HelpArticleCell extends ListCell<HelpArticle> {
    private final Label titleLabel;
    private final Label descriptionLabel;
    private final VBox vBox;

    public HelpArticleCell() {
        // Initialize labels and layout only once
        titleLabel = new Label();
        titleLabel.getStyleClass().add("article-title");

        descriptionLabel = new Label();
        descriptionLabel.getStyleClass().add("article-description");

        vBox = new VBox(5, titleLabel, descriptionLabel);
        vBox.setAlignment(Pos.CENTER_LEFT);
    }

    @Override
    protected void updateItem(HelpArticle article, boolean empty) {
        super.updateItem(article, empty);

        if (article != null && !empty) {
            // Update labels with article information
            titleLabel.setText(article.getTitle());
            descriptionLabel.setText(article.getDescription());
            setGraphic(vBox);
        } else {
            setGraphic(null);
        }
    }
}