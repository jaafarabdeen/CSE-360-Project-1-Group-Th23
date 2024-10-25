// HelpArticleCell.java
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.geometry.Pos;

/**
 * Custom ListCell to display help article information in the articles list.
 * Shows the title and description of each article.
 * 
 * Author:
 *     - Ayush Kaushik
 */
public class HelpArticleCell extends ListCell<HelpArticle> {
    @Override
    protected void updateItem(HelpArticle article, boolean empty) {
        super.updateItem(article, empty);

        if (article != null && !empty) {
            // Create labels for title and description
            Label titleLabel = new Label(article.getTitle());
            titleLabel.setFont(new Font("Arial", 24));
            titleLabel.setTextFill(Color.web("#ffffff"));

            Label descriptionLabel = new Label(article.getDescription());
            descriptionLabel.setFont(new Font("Arial", 18));
            descriptionLabel.setTextFill(Color.web("#a0a0a0"));

            // Layout using VBox
            VBox vBox = new VBox(5, titleLabel, descriptionLabel);
            vBox.setAlignment(Pos.CENTER_LEFT);

            setGraphic(vBox);
        } else {
            setGraphic(null);
        }
    }
}
