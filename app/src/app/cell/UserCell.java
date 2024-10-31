package app.cell;

import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import app.User;
import javafx.geometry.Pos;

/**
 * Custom ListCell to display user information in the member list.
 * Shows the username, roles, and level of each user.
 * 
 * Author:
 *     - Lewam Atnafie
 */
public class UserCell extends ListCell<User> {
    private final Label usernameLabel = new Label();
    private final Label rolesLabel = new Label();
    private final Label levelLabel = new Label();
    private final HBox hBox = new HBox(20, usernameLabel, rolesLabel, levelLabel);

    public UserCell() {
        // Apply CSS class to rely on stylesheet for hover and selected states
        usernameLabel.setStyle("-fx-text-fill: #ffffff;");
        rolesLabel.setStyle("-fx-text-fill: #a0a0a0;");
        levelLabel.setStyle("-fx-text-fill: #a0a0a0;");

        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setStyle("-fx-padding: 10;");
    }

    @Override
    protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);

        if (user != null && !empty) {
            usernameLabel.setText(user.getPreferredName());
            rolesLabel.setText(String.join(", ", user.getRoles()));
            levelLabel.setText("Level: " + user.getLevel());
            setGraphic(hBox);
        } else {
            setGraphic(null);
        }
    }
}
