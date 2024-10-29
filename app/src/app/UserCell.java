package app;

import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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
        // Initialize styles
        String whiteTextColor = "#000000";
        String grayTextColor = "#a0a0a0";

        usernameLabel.setFont(new Font("Arial", 24));
        usernameLabel.setTextFill(Color.web(whiteTextColor));

        rolesLabel.setFont(new Font("Arial", 18));
        rolesLabel.setTextFill(Color.web(grayTextColor));

        levelLabel.setFont(new Font("Arial", 18));
        levelLabel.setTextFill(Color.web(grayTextColor));

        hBox.setAlignment(Pos.CENTER_LEFT);
    }

    @Override
    protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);

        if (user != null && !empty) {
            usernameLabel.setText(user.getPreferredName());
            rolesLabel.setText(user.getRoles().toString());
            levelLabel.setText("Level: " + user.getLevel());
            setGraphic(hBox);
        } else {
            setGraphic(null);
        }
    }
}
