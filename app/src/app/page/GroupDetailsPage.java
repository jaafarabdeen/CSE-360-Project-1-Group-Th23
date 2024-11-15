// GroupDetailsPage.java
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
import app.util.Group;
import app.User;
import app.util.DatabaseHelper;
import app.util.UIHelper;
import java.util.Optional;

/**
 * The GroupDetailsPage class allows admins to manage the details of a specific group.
 * Admins can add/remove users, assign admin rights, and manage group members.
 * 
 * Author:
 *     - Jaafar Abdeen
 *     - Ayush Kaushik
 */
public class GroupDetailsPage {
    private final Stage stage;
    private final User user;
    private final Group group;
    private final DatabaseHelper databaseHelper;

    public GroupDetailsPage(Stage stage, User user, Group group) {
        this.stage = stage;
        this.user = user;
        this.group = group;
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
        Label titleLabel = new Label("Managing Group: " + group.getName());
        titleLabel.getStyleClass().add("label-title");

        // ListView of group members
        ListView<String> memberListView = new ListView<>();
        memberListView.setPrefSize(600, 600);
        memberListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Populate member list
        memberListView.getItems().addAll(group.getAdmins());
        memberListView.getItems().addAll(group.getInstructorAdmins());
        memberListView.getItems().addAll(group.getInstructors());
        memberListView.getItems().addAll(group.getStudents());

        // Context menu for member actions
        ContextMenu contextMenu = new ContextMenu();
        MenuItem removeMemberItem = new MenuItem("Remove Member");
        MenuItem changeRightsItem = new MenuItem("Change Rights");
        contextMenu.getItems().addAll(changeRightsItem, removeMemberItem);
        memberListView.setContextMenu(contextMenu);

        // Remove member action
        removeMemberItem.setOnAction(e -> {
            String selectedUser = memberListView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                group.getAdmins().remove(selectedUser);
                group.getInstructorAdmins().remove(selectedUser);
                group.getInstructors().remove(selectedUser);
                group.getStudents().remove(selectedUser);
                memberListView.getItems().remove(selectedUser);
                try {
                    databaseHelper.updateGroup(group);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Change rights action
        changeRightsItem.setOnAction(e -> {
            String selectedUser = memberListView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                // Implement a dialog to select new rights
                // For brevity, we'll assume changing between student and instructor
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Change Rights");
                dialog.setHeaderText("Enter new rights for " + selectedUser + " (Admin, InstructorAdmin, Instructor, Student):");
                Optional<String> result = dialog.showAndWait();
                result.ifPresent(newRights -> {
                    group.getAdmins().remove(selectedUser);
                    group.getInstructorAdmins().remove(selectedUser);
                    group.getInstructors().remove(selectedUser);
                    group.getStudents().remove(selectedUser);

                    switch (newRights.trim()) {
                        case "Admin":
                            group.addAdmin(selectedUser);
                            break;
                        case "InstructorAdmin":
                            group.addInstructorAdmin(selectedUser);
                            break;
                        case "Instructor":
                            group.addInstructor(selectedUser);
                            break;
                        case "Student":
                            group.addStudent(selectedUser);
                            break;
                        default:
                            // Invalid input, do nothing
                            break;
                    }

                    memberListView.getItems().clear();
                    memberListView.getItems().addAll(group.getAdmins());
                    memberListView.getItems().addAll(group.getInstructorAdmins());
                    memberListView.getItems().addAll(group.getInstructors());
                    memberListView.getItems().addAll(group.getStudents());
                    try {
                        databaseHelper.updateGroup(group);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }
        });

        // Add Member Button
        Button addMemberButton = UIHelper.createButton("Add Member", e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Member");
            dialog.setHeaderText("Enter Username of Member to Add:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(username -> {
                // Check if user exists
                try {
                    if (databaseHelper.doesUserExist(username.trim())) {
                        // Add user as a student by default
                        group.addStudent(username.trim());
                        memberListView.getItems().add(username.trim());
                        databaseHelper.updateGroup(group);
                    } else {
                        // Show error message
                        UIHelper.showErrorDialog("User not found", "The username you entered does not exist.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        });

        // Back Button
        Button backButton = UIHelper.createButton("Back", e -> {
            new GroupManagementPage(stage, user).show();
        });

        // Layout
        HBox buttonBox = new HBox(20, addMemberButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(20, titleLabel, memberListView, buttonBox);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(30));

        // Create the scene with CSS styling
        Scene scene = UIHelper.createStyledScene(vBox, 1920, 1080);
        stage.setTitle("Group Details");
        stage.setScene(scene);
        stage.show();
    }
}
