package app.dialog;

import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import java.util.Set;
import java.util.HashSet;

/**
 * The RoleSelectionDialog class provides a dialog for selecting roles when generating an invitation token.
 * Admins can select multiple roles to assign to a new user.
 * 
 * Author:
 *     - Pragya Kumari
 *     - Aaryan Gaur
 */
public class RoleSelectionDialog {
    /**
     * Displays the role selection dialog and returns the selected roles.
     * 
     * @return A set of selected roles.
     */
    public Set<String> showAndWait() {
        Dialog<Set<String>> dialog = new Dialog<>();
        dialog.setTitle("Select Roles");

        // Role checkboxes with style classes for consistent styling
        CheckBox studentCheckBox = new CheckBox("Student");
        studentCheckBox.getStyleClass().add("role-checkbox");

        CheckBox instructorCheckBox = new CheckBox("Instructor");
        instructorCheckBox.getStyleClass().add("role-checkbox");

        VBox vBox = new VBox(10, studentCheckBox, instructorCheckBox);
        vBox.setPadding(new Insets(20));

        dialog.getDialogPane().setContent(vBox);

        // OK and Cancel buttons
        ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        Set<String> emptySet = new HashSet<>(); // Define an empty set to return if no roles are selected
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                Set<String> roles = new HashSet<>();
                if (studentCheckBox.isSelected()) {
                    roles.add("Student");
                }
                if (instructorCheckBox.isSelected()) {
                    roles.add("Instructor");
                }
                return roles;
            }
            return emptySet;
        });

        return dialog.showAndWait().orElse(emptySet);
    }
}
