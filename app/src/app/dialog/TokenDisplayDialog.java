package app.dialog;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/**
 * The TokenDisplayDialog class displays the generated invitation token to the admin.
 * Admins can copy the token and share it with new users for registration.
 * 
 * Author:
 *     - Lewam Atnafie
 */
public class TokenDisplayDialog {
    private final String token;

    public TokenDisplayDialog(String token) {
        this.token = token;
    }

    /**
     * Displays the token in an information alert dialog with a "Copy" option.
     */
    public void showAndWait() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Invitation Token");
        alert.setHeaderText("Generated Invitation Token");
        alert.setContentText("Token: " + token);

        // Add "Copy" button to the alert dialog
        ButtonType copyButtonType = new ButtonType("Copy to Clipboard");
        alert.getButtonTypes().add(copyButtonType);

        alert.showAndWait().ifPresent(response -> {
            if (response == copyButtonType) {
                copyTokenToClipboard();
            }
        });
    }

    /**
     * Copies the token to the system clipboard.
     */
    private void copyTokenToClipboard() {
        ClipboardContent content = new ClipboardContent();
        content.putString(token);
        Clipboard.getSystemClipboard().setContent(content);
    }
}
