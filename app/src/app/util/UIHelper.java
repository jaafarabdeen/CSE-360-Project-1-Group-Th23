package app.util;

import javafx.animation.ScaleTransition;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;

/**
 * The UIHelper class handles animations and styles for the UI.
 * 
 * Author:
 *     - Jaafar Abdeen
 */
public class UIHelper {
	
	private static final String STYLESHEET_PATH = UIHelper.class.getResource("styles.css").toExternalForm();
	
	/**
     * Creates a Scene with the specified root and applies the main stylesheet.
     * 
     * @param root the root node for the Scene
     * @param width the width of the Scene
     * @param height the height of the Scene
     * @return a Scene with the main stylesheet applied
     */
    public static Scene createStyledScene(Parent root, double width, double height) {
        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add(STYLESHEET_PATH);
        return scene;
    }
    
    /**
     * Creates a ScaleTransition animation for scaling a Button to the specified X and Y values.
     * This method is used internally for creating consistent animations with different scales and durations.
     *
     * @param button         the Button to animate
     * @param toX            the X-axis scaling factor to scale the button to (1.0 is default size)
     * @param toY            the Y-axis scaling factor to scale the button to (1.0 is default size)
     * @param durationMillis the duration of the scaling animation in milliseconds
     * @return               a ScaleTransition object configured for the specified parameters
     */
    private static ScaleTransition createScaleTransition(Button button, double toX, double toY, int durationMillis) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(durationMillis), button);
        scaleTransition.setToX(toX);
        scaleTransition.setToY(toY);
        return scaleTransition;
    }

    /**
     * Creates a styled Button with the specified text, action, and optional custom style.
     *
     * @param text     the text displayed on the button
     * @param action   the action to be performed when the button is clicked
     * @param customStyle (optional) custom CSS style string to apply to the button
     * @return a styled Button with animations applied
     */
    public static Button createButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> action, String... customStyle) {
        Button button = new Button(text);
        button.getStyleClass().add("button");
        button.setOnAction(action);
        applyButtonAnimations(button);

        // Apply custom style if provided, otherwise use default blue style
        if (customStyle.length > 0) {
            button.setStyle(customStyle[0]);
        } else {
            button.setStyle("-fx-background-color: #5865F2; -fx-text-fill: white; -fx-font-size: 18;");
        }

        return button;
    }

    /**
     * Applies hover, press, and release animations to the provided button.
     *
     * @param button the Button to apply animations to
     */
    public static void applyButtonAnimations(Button button) {
        ScaleTransition scaleUp = createScaleTransition(button, 1.05, 1.05, 200);
        ScaleTransition scaleDown = createScaleTransition(button, 1, 1, 200);
        ScaleTransition scalePressed = createScaleTransition(button, 0.98, 0.98, 100);
        ScaleTransition scaleReleased = createScaleTransition(button, 1, 1, 100);

        button.setOnMouseEntered(e -> scaleUp.play());
        button.setOnMouseExited(e -> scaleDown.play());
        button.setOnMousePressed(e -> scalePressed.play());
        button.setOnMouseReleased(e -> scaleReleased.play());
    }
    
    public static Label createMessageLabel() {
        Label messageLabel = new Label();
        messageLabel.getStyleClass().add("message-label");
        return messageLabel;
    }

    /**
     * Updates the text of the provided message Label and changes its color based on the message type.
     * 
     * @param messageLabel the Label to update
     * @param message      the message text to display
     * @param isError      if true, displays the message in an error style; otherwise, as info
     */
    public static void setMessage(Label messageLabel, String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setStyle(isError ? "-fx-text-fill: #ff5555;" : "-fx-text-fill: #32cd32;");
    }
    
    /**
     * Creates a styled TextField with the specified prompt text.
     * 
     * @param promptText the prompt text to display within the TextField
     * @return a styled TextField with the specified prompt
     */
    public static TextField createTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.getStyleClass().add("text-field");
        textField.setMaxWidth(600);
        return textField;
    }
    
    /**
     * Creates a styled PasswordField with the specified prompt text.
     * 
     * @param promptText the prompt text to display within the PasswordField
     * @return a styled PasswordField with the specified prompt
     */
    public static PasswordField createPasswordField(String promptText) {
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(promptText);
        passwordField.getStyleClass().add("password-field");
        passwordField.setMaxWidth(600);
        return passwordField;
    }
    
    /**
     * Creates a styled TextArea with the specified prompt text, width, and height.
     * 
     * @param promptText the prompt text to display in the TextArea
     * @param maxWidth   the maximum width of the TextArea
     * @param maxHeight  the maximum height of the TextArea
     * @return a styled TextArea with the specified prompt and dimensions
     */
    public static TextArea createTextArea(String promptText, int maxWidth, int maxHeight) {
        TextArea textArea = new TextArea();
        textArea.setPromptText(promptText);
        textArea.setMaxWidth(maxWidth);
        textArea.setMaxHeight(maxHeight);
        textArea.getStyleClass().add("text-area");
        return textArea;
    }
    
    /**
     * Shows an error dialog with the specified title and message.
     * 
     * @param title The title of the dialog.
     * @param message The message content of the dialog.
     */
    public static void showErrorDialog(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
    
    /**
     * Displays an information dialog with the specified title and message.
     *
     * @param title   The title of the dialog.
     * @param message The message content of the dialog.
     */
    public static void showInfoDialog(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}

