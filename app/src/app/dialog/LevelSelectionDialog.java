package app.dialog;

import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

/**
 * The LevelSelectionDialog class provides a dialog for selecting a level for a user.
 * Users can select their level during account creation, or admins can assign levels.
 * 
 * Author:
 *     - Ayush Kaushik
 */
public class LevelSelectionDialog {
    /**
     * Displays the level selection dialog and returns the selected level.
     * 
     * @return The selected level.
     */
    public String showAndWait() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Select Level");

        // Level choice box with default selection
        ChoiceBox<String> levelChoiceBox = new ChoiceBox<>();
        levelChoiceBox.getItems().addAll("Beginner", "Intermediate", "Advanced", "Expert");
        levelChoiceBox.setValue("Intermediate");
        levelChoiceBox.getStyleClass().add("level-choice-box");

        VBox vBox = new VBox(10, levelChoiceBox);
        vBox.setPadding(new Insets(20));

        dialog.getDialogPane().setContent(vBox);

        // OK and Cancel buttons
        ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> 
            dialogButton == okButtonType ? levelChoiceBox.getValue() : null
        );

        return dialog.showAndWait().orElse(null);
    }
}
