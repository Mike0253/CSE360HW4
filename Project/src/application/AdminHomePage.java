/**
 * AdminHomePage.java
 * This class represents the home page interface for administrators in the application.
 * It displays a simple welcome message and provides the basic layout for admin functionality.
 */
package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * AdminPage class represents the user interface for the admin user. This page
 * displays a simple welcome message for the admin.
 */
public class AdminHomePage {
    
    private User currentUSER;
    
    /**
     * Displays the admin page in the provided primary stage.
     * 
     * @param primaryStage The primary stage where the scene will be displayed.
     * @param user The currently logged in admin user
     */
    public void show(Stage primaryStage,User user) {
        currentUSER = user;
        VBox layout = new VBox();

        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // label to display the welcome message for the admin
        Label adminLabel = new Label("Hello, Admin!");

        adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        layout.getChildren().add(adminLabel);
        Scene adminScene = new Scene(layout, 800, 400);

        // Set the scene to primary stage
        primaryStage.setScene(adminScene);
        primaryStage.setTitle("Admin Page");
    }
}