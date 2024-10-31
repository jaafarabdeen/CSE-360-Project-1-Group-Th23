package app.util;

import javafx.animation.ScaleTransition;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        scene.getStylesheets().add(UIHelper.class.getResource("styles.css").toExternalForm());
        return scene;
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
        button.setOnMouseEntered(e -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), button);
            scaleUp.setToX(1.05);
            scaleUp.setToY(1.05);
            scaleUp.play();
        });

        button.setOnMouseExited(e -> {
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), button);
            scaleDown.setToX(1);
            scaleDown.setToY(1);
            scaleDown.play();
        });

        button.setOnMousePressed(e -> {
            ScaleTransition scalePressed = new ScaleTransition(Duration.millis(100), button);
            scalePressed.setToX(0.98);
            scalePressed.setToY(0.98);
            scalePressed.play();
        });

        button.setOnMouseReleased(e -> {
            ScaleTransition scaleReleased = new ScaleTransition(Duration.millis(100), button);
            scaleReleased.setToX(1);
            scaleReleased.setToY(1);
            scaleReleased.play();
        });
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
}

