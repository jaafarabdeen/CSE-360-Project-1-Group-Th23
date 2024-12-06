package app.page;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import app.User;
import app.util.UIHelper;

/**
 * The FAQPage class represents a page that displays FAQs in dropdown format.
 * - Jaafar Abdeen
 */
public class FAQPage {
    private final Stage stage;
    private final User user;

    public FAQPage(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
    }

    /**
     * Displays the FAQ page UI.
     */
    public void show() {
        // FAQ categories and questions
        TitledPane generalFAQ = new TitledPane("Navigating the Application (General)",
                UIHelper.createFAQContent(new String[][] {
                        {"I forgot my password. How can I reset it?",
                         "Contact an Admin to receive a one-time password. At your next login, use that temporary password and follow the prompts to set a new one."},
                        {"I have multiple roles. How do I choose which role to use?",
                         "After logging in, if you have more than one role, you’ll be prompted to select the one to use this session."},
                        {"How do I finish setting up my account?",
                         "After creating your login, you’ll be directed to provide your email and name details before accessing any role’s home page."},
                        {"How can I log out?",
                         "From any role’s home page, select the 'Log Out' option."},
                        {"I was invited with a code. How do I use it?",
                         "On the login screen, enter the invitation code in the provided field to create your account and then finish setting it up."}
                }));

        TitledPane helpSystemFAQ = new TitledPane("Navigating the Help Article System",
                UIHelper.createFAQContent(new String[][] {
                        {"How do I search for help articles?",
                         "Specify a search keyword in title, author, or abstract fields. You may also search by article ID."},
                        {"How do I filter articles by level (e.g., beginner, advanced)?",
                         "Set the desired content level or 'all.' The default is 'all,' showing all levels."},
                        {"How do I view articles in a specific group or special access group?",
                         "Select the target group before searching. If you have permission, articles in that group will be displayed."},
                        {"How do I view a full article?",
                         "After a search, choose the article’s sequence number to see its full details."},
                        {"How do I handle special access encrypted articles?",
                         "Ensure you have been granted access rights. Without proper rights, you’ll see only the article’s metadata, not the encrypted content."}
                }));

        Accordion faqAccordion = new Accordion(generalFAQ, helpSystemFAQ);

        // Back button setup
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> handleBackAction());

        VBox vBox = new VBox(20, faqAccordion, backButton);
        Scene scene = UIHelper.createStyledScene(vBox, 1920, 1080);
        stage.setScene(scene);
        stage.setTitle("FAQ");
        stage.show();
    }

    /**
     * Handles the back action, navigating to the previous page.
     */
    private void handleBackAction() {
    	new DashboardPage(stage, user).show();
    }
}
