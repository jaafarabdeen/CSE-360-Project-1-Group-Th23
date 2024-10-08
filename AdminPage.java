// AdminPage.java
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
import java.util.Set;
import java.util.ArrayList;
import java.util.Comparator;

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
    private Stage stage;
    private User user;

    public AdminPage(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
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
                LoginPage.invitationTokens.put(token, invitation);
                // Display the token
                TokenDisplayDialog tokenDialog = new TokenDisplayDialog(token);
                tokenDialog.showAndWait();
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

        // Populate the member list
        ArrayList<User> userList = new ArrayList<>(LoginPage.userDatabase.values());
        // Sort users by role: Admin > Instructor > Student
        userList.sort(new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                return getRolePriority(u1) - getRolePriority(u2);
            }

            private int getRolePriority(User user) {
                if (user.hasRole("Admin")) {
                    return 1;
                } else if (user.hasRole("Instructor")) {
                    return 2;
                } else {
                    return 3; // Student or others
                }
            }
        });

        // Add users to the ListView
        for (User user : userList) {
            memberListView.getItems().add(user);
        }

        // Define how each user is displayed
        memberListView.setCellFactory(param -> new UserCell());

        // Context menu for user actions
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteUserItem = new MenuItem("Delete User");
        MenuItem changeRoleItem = new MenuItem("Change Roles");
        contextMenu.getItems().addAll(deleteUserItem, changeRoleItem);

        // Set context menu on the ListView
        memberListView.setContextMenu(contextMenu);

        // Handle delete user action
        deleteUserItem.setOnAction(e -> {
            User selectedUser = memberListView.getSelectionModel().getSelectedItem();
            if (selectedUser != null && !selectedUser.getUsername().equals(user.getUsername())) {
                LoginPage.userDatabase.remove(selectedUser.getUsername());
                memberListView.getItems().remove(selectedUser);
            }
        });

        // Handle change role action
        changeRoleItem.setOnAction(e -> {
            User selectedUser = memberListView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                RoleSelectionDialog roleDialog = new RoleSelectionDialog();
                Set<String> selectedRoles = roleDialog.showAndWait();
                if (!selectedRoles.isEmpty()) {
                    selectedUser.setRoles(selectedRoles);
                    // Refresh the member list
                    memberListView.refresh();
                }
            }
        });

        // Finish Setting Up Account Button
        Button finishSetupButton = new Button("Finish Setting Up Your Account");
        finishSetupButton.setPrefWidth(600);
        finishSetupButton.setPrefHeight(50);
        finishSetupButton.setStyle("-fx-background-color: #5865F2; -fx-text-fill: white; -fx-font-size: 24;");
        finishSetupButton.setOnAction(e -> {
            FinishSettingUpAccountPage finishPage = new FinishSettingUpAccountPage(stage, user);
            finishPage.show();
        });

        // Logout Button
        Button logoutButton = new Button("Logout");
        logoutButton.setPrefWidth(300);
        logoutButton.setPrefHeight(50);
        logoutButton.setStyle("-fx-background-color: #FF5555; -fx-text-fill: white; -fx-font-size: 24;");
        logoutButton.setOnAction(e -> {
            LoginPage loginPage = new LoginPage(stage);
            loginPage.show();
        });

        // Layout for buttons
        HBox buttonBox = new HBox(20, generateTokenButton, finishSetupButton, logoutButton);
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
}
