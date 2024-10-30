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
        // Apply CSS classes for consistent styling
        usernameLabel.getStyleClass().add("user-username");
        rolesLabel.getStyleClass().add("user-roles");
        levelLabel.getStyleClass().add("user-level");

        hBox.setAlignment(Pos.CENTER_LEFT);
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
