// TokenDisplayDialog.java
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * The TokenDisplayDialog class displays the generated invitation token to the admin.
 * Admins can copy the token and share it with new users for registration.
 * 
 * Author:
 *     - Lewam Atnafie
 */
public class TokenDisplayDialog {
    private String token;

    public TokenDisplayDialog(String token) {
        this.token = token;
    }

    /**
     * Displays the token in an information alert dialog.
     */
    public void showAndWait() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Invitation Token");
        alert.setHeaderText("Generated Invitation Token");
        alert.setContentText("Token: " + token);

        alert.showAndWait();
    }
}
