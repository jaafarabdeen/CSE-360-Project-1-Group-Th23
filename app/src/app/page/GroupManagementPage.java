package app.page;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import java.util.Optional;
import java.util.Set;

import app.User;
import app.cell.GroupCell;
import app.util.DatabaseHelper;
import app.util.Group;
import app.util.UIHelper;

/**
 * The GroupManagementPage class allows admins to manage special access groups.
 * Admins can create new groups, add/remove users, assign admin rights, and delete groups.
 * 
 * Author:
 *     - Jaafar Abdeen
 *     - Ayush Kaushik
 */
public class GroupManagementPage {
    private final Stage stage;
    private final User user;
    private final DatabaseHelper databaseHelper;

    public GroupManagementPage(Stage stage, User user) {
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
    }

    public void show() {
        stage.setOnCloseRequest(event -> databaseHelper.closeConnection());
        // Title label
        Label titleLabel = new Label("Manage Special Access Groups");
        titleLabel.getStyleClass().add("label-title");

        // ListView of groups
        ListView<Group> groupListView = new ListView<>();
        groupListView.setPrefSize(600, 600);
        groupListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Populate group list
        try {
            Set<Group> groups = databaseHelper.getAllGroups();
            groupListView.getItems().addAll(groups);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Define display style for each group
        groupListView.setCellFactory(param -> new GroupCell());

        // Context menu for group actions
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteGroupItem = new MenuItem("Delete Group");
        MenuItem manageGroupItem = new MenuItem("Manage Group");
        contextMenu.getItems().addAll(manageGroupItem, deleteGroupItem);
        groupListView.setContextMenu(contextMenu);

        // Delete group action
        deleteGroupItem.setOnAction(e -> {
            Group selectedGroup = groupListView.getSelectionModel().getSelectedItem();
            if (selectedGroup != null) {
                try {
                    databaseHelper.deleteGroup(selectedGroup.getName());
                    groupListView.getItems().remove(selectedGroup);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Manage group action
        manageGroupItem.setOnAction(e -> {
            Group selectedGroup = groupListView.getSelectionModel().getSelectedItem();
            if (selectedGroup != null) {
                new GroupDetailsPage(stage, user, selectedGroup).show();
            }
        });

        // Create Group Button
        Button createGroupButton = UIHelper.createButton("Create New Group", e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Create New Group");
            dialog.setHeaderText("Enter Group Name:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(groupName -> {
                if (!groupName.trim().isEmpty()) {
                    Group newGroup = new Group(groupName.trim());
                    // The first instructor added is given viewing and admin rights for the group
                    // In this implementation, the admin creating the group is added as a group admin
                    newGroup.addAdmin(user.getUsername());
                    try {
                        databaseHelper.createGroup(newGroup);
                        groupListView.getItems().add(newGroup);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        });

        // Back Button
        Button backButton = UIHelper.createButton("Back", e -> {
            new AdminPage(stage, user).show();
        });

        // Layout
        HBox buttonBox = new HBox(20, createGroupButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(20, titleLabel, groupListView, buttonBox);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(30));

        // Create the scene with CSS styling
        Scene scene = UIHelper.createStyledScene(vBox, 1920, 1080);
        stage.setTitle("Group Management");
        stage.setScene(scene);
        stage.show();
    }
}
