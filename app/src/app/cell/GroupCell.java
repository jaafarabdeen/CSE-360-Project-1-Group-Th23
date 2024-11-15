package app.cell;

import javafx.scene.control.ListCell;
import app.util.Group;

/**
 * Custom ListCell for displaying group information in the ListView.
 * 
 * Author:
 *     - Jaafar Abdeen
 */
public class GroupCell extends ListCell<Group> {
    @Override
    protected void updateItem(Group group, boolean empty) {
        super.updateItem(group, empty);
        if (empty || group == null) {
            setText(null);
            setGraphic(null);
            setTooltip(null);
        } else {
            setText(group.getName());
            getStyleClass().add("group-cell");
        }
    }
}
