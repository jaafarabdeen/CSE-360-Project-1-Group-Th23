package app;

import javafx.application.Application;
import javafx.stage.Stage;

/**
* The main entry point of the application.
* Launches the JavaFX application and shows the login page.
* 
* @author
*     - Ayush Kaushik
*/
public class Main extends Application {
 @Override
 public void start(Stage primaryStage) {
     // Initialize and display the login page
     LoginPage loginPage = new LoginPage(primaryStage);
     loginPage.show();
 }

 public static void main(String[] args) {
     // Launch the JavaFX application
     launch(args);
 }
}