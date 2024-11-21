package app.page;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import java.util.UUID;
import java.util.Set;
import java.util.ArrayList;
import java.util.Comparator;
import java.sql.SQLException;

import app.User;
import app.cell.UserCell;
import app.dialog.LevelSelectionDialog;
import app.dialog.RoleSelectionDialog;
import app.dialog.TokenDisplayDialog;
import app.util.DatabaseHelper;
import app.util.Invitation;
import app.util.UIHelper;

/**
 * The AdminPage class represents the admin dashboard where the admin can manage users and special access groups.
 * Admin can view the member list, generate invitation tokens, delete accounts, change user roles,
 * and manage special access groups.
 * 
 * Author:
 *     - Ayush Kaushik
 *     - Jaafar Abdeen
 */
public class AdminPage {
    private final Stage stage;
    private final User user;
    private final DatabaseHelper databaseHelper;

    public AdminPage(Stage stage, User user) {
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

    /**
     * Displays the admin page UI and handles admin interactions.
     */
    public void show() {
        stage.setOnCloseRequest(event -> databaseHelper.closeConnection());
        // Welcome label
        Label welcomeLabel = new Label("Welcome to the Admin Page!");
        welcomeLabel.getStyleClass().add("label-title");

        // Generate Token Button
        Button generateTokenButton = UIHelper.createButton("Generate Invitation Token", e -> {
            // Show role selection dialog
            RoleSelectionDialog roleDialog = new RoleSelectionDialog();
            Set<String> selectedRoles = roleDialog.showAndWait();
            if (!selectedRoles.isEmpty()) {
                String token = UUID.randomUUID().toString();
                Invitation invitation = new Invitation(token, selectedRoles);
                try {
                    databaseHelper.registerInvitation(invitation);
                    new TokenDisplayDialog(token).showAndWait();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Member List Label
        Label memberListLabel = new Label("Member List");
        memberListLabel.getStyleClass().add("label-subtitle");

        // ListView to display members
        ListView<User> memberListView = new ListView<>();
        memberListView.setPrefSize(600, 600);
        memberListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Populate and sort the member list
        try {
            ArrayList<User> userList = new ArrayList<>(databaseHelper.getAllUsers());
            userList.sort(Comparator.comparingInt(this::getRolePriority));
            memberListView.getItems().addAll(userList);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Define display style for each user
        memberListView.setCellFactory(param -> new UserCell());
        memberListView.getStyleClass().add("list-view");

        // Context menu for user actions
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteUserItem = new MenuItem("Delete User");
        MenuItem changeRoleItem = new MenuItem("Change Roles");
        MenuItem setLevelItem = new MenuItem("Set Level");
        contextMenu.getItems().addAll(deleteUserItem, changeRoleItem, setLevelItem);
        // Set context menu on the ListView
        memberListView.setContextMenu(contextMenu);

        // Delete user action
        deleteUserItem.setOnAction(e -> {
            User selectedUser = memberListView.getSelectionModel().getSelectedItem();
            if (selectedUser != null && !selectedUser.getUsername().equals(user.getUsername())) {
                try {
                    databaseHelper.deleteUser(selectedUser.getUsername());
                    memberListView.getItems().remove(selectedUser);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Change role action
        changeRoleItem.setOnAction(e -> {
            User selectedUser = memberListView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                RoleSelectionDialog roleDialog = new RoleSelectionDialog();
                Set<String> selectedRoles = roleDialog.showAndWait();
                if (!selectedRoles.isEmpty()) {
                    selectedUser.setRoles(selectedRoles);
                    try {
                        databaseHelper.updateUser(selectedUser);
                        // Refresh the member list
                        memberListView.refresh();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        // Set level action
        setLevelItem.setOnAction(e -> {
            User selectedUser = memberListView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                LevelSelectionDialog levelDialog = new LevelSelectionDialog();
                String selectedLevel = levelDialog.showAndWait();
                if (selectedLevel != null) {
                    selectedUser.setLevel(selectedLevel);
                    try {
                        databaseHelper.updateUser(selectedUser);
                        // Refresh the member list
                        memberListView.refresh();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        // Manage Groups Button
        Button manageGroupsButton = UIHelper.createButton("Manage Groups", e -> {
            new GroupManagementPage(stage, user).show();
        });

        // Additional Buttons
        Button helpArticlesButton = UIHelper.createButton("Help Articles", e -> {
            new HelpArticlesPage(stage, user).show();
        });

        Button finishSetupButton = UIHelper.createButton("Finish Setting Up Your Account", e -> {
            new FinishSettingUpAccountPage(stage, user).show();
        });
        
        Button viewMessagesButton = UIHelper.createButton("View Help Messages", e -> {
            try {
				new ViewHelpMessagesPage(stage, user).show();
			} catch (Exception e1) {}
        });

        Button logoutButton = UIHelper.createButton("Logout", e -> {
            new LoginPage(stage).show();
        }, "-fx-background-color: #FF5555;");

        // Layout for buttons
        HBox buttonBox = new HBox(20, generateTokenButton, manageGroupsButton, helpArticlesButton, finishSetupButton, viewMessagesButton, logoutButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Main layout using VBox
        VBox vBox = new VBox(40, welcomeLabel, buttonBox, memberListLabel, memberListView);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(30));

        // Main layout using BorderPane
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(vBox);

        // Create the scene with CSS styling
        Scene scene = UIHelper.createStyledScene(borderPane, 1920, 1080);
        stage.setTitle("Admin Page");
        stage.setScene(scene);
        stage.show();
    }

    private int getRolePriority(User user) {
        if (user.hasRole("Admin")) return 1;
        else if (user.hasRole("Instructor")) return 2;
        return 3; // Student or other
    }
}
