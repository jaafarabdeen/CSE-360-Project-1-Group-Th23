// UserCell.java
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.geometry.Pos;

/**
 * Custom ListCell to display user information in the member list.
 * Shows the username and roles of each user.
 * @author
  *     - Lewam Atnafie
 */
public class UserCell extends ListCell<User> {
    @Override
    protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);

        if (user != null && !empty) {
            // Create labels for username and roles
            Label usernameLabel = new Label(user.getUsername());
            usernameLabel.setFont(new Font("Arial", 24));
            usernameLabel.setTextFill(Color.web("#ffffff"));

            Label rolesLabel = new Label(user.getRoles().toString());
            rolesLabel.setFont(new Font("Arial", 18));
            rolesLabel.setTextFill(Color.web("#a0a0a0"));

            // Layout using HBox
            HBox hBox = new HBox(20, usernameLabel, rolesLabel);
            hBox.setAlignment(Pos.CENTER_LEFT);

            setGraphic(hBox);
        } else {
            setGraphic(null);
        }
    }
}
