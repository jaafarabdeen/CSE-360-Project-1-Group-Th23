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
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
import java.util.UUID;

import app.User;
import app.cell.UserCell;
import app.dialog.LevelSelectionDialog;
import app.dialog.RoleSelectionDialog;
import app.dialog.TokenDisplayDialog;
import app.util.DatabaseHelper;
import app.util.Invitation;

import java.util.Set;
import java.util.ArrayList;
import java.util.Comparator;
import java.sql.SQLException;

/**
 * The AdminPage class represents the admin dashboard where the admin can manage users.
 * Admin can view the member list, generate invitation tokens, delete accounts, and change user roles.
 * The member list is displayed with users separated by their roles.
 * 
 * This class enhances the admin functionalities as per the requirements.
 * 
 * @author
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
        // Welcome label
        Label welcomeLabel = new Label("Welcome to the Admin Page!");
        welcomeLabel.setFont(new Font("Arial", 56));
        welcomeLabel.setTextFill(Color.web("#ffffff"));

        // Generate Token Button
        Button generateTokenButton = new Button("Generate Invitation Token");
        generateTokenButton.setPrefWidth(600);
        generateTokenButton.setPrefHeight(50);
        generateTokenButton.setStyle("-fx-background-color: #5865F2; -fx-text-fill: white; -fx-font-size: 24;");
        generateTokenButton.setOnAction(e -> {
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
        memberListLabel.setFont(new Font("Arial", 48));
        memberListLabel.setTextFill(Color.web("#ffffff"));

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

        // Additional Buttons
        Button helpArticlesButton = new Button("Help Articles");
        helpArticlesButton.setPrefWidth(600);
        helpArticlesButton.setPrefHeight(50);
        helpArticlesButton.setStyle("-fx-background-color: #5865F2; -fx-text-fill: white; -fx-font-size: 24;");
        helpArticlesButton.setOnAction(e -> {
            new HelpArticlesPage(stage, user).show();
        });

        // Finish Setting Up Account Button
        Button finishSetupButton = new Button("Finish Setting Up Your Account");
        finishSetupButton.setPrefWidth(600);
        finishSetupButton.setPrefHeight(50);
        finishSetupButton.setStyle("-fx-background-color: #5865F2; -fx-text-fill: white; -fx-font-size: 24;");
        finishSetupButton.setOnAction(e -> {
            new FinishSettingUpAccountPage(stage, user).show();
        });

        // Logout Button
        Button logoutButton = new Button("Logout");
        logoutButton.setPrefWidth(300);
        logoutButton.setPrefHeight(50);
        logoutButton.setStyle("-fx-background-color: #FF5555; -fx-text-fill: white; -fx-font-size: 24;");
        logoutButton.setOnAction(e -> {
            new LoginPage(stage).show();
        });

        // Layout for buttons
        HBox buttonBox = new HBox(20, generateTokenButton, helpArticlesButton, finishSetupButton, logoutButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Main layout using VBox
        VBox vBox = new VBox(40, welcomeLabel, buttonBox, memberListLabel, memberListView);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(30));

        // Main layout using BorderPane
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(vBox);
        borderPane.setStyle("-fx-background-color: #3b5998;");

        // Create the scene with 1920x1080 resolution
        Scene scene = new Scene(borderPane, 1920, 1080);
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
