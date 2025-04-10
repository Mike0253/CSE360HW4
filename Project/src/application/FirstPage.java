package application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The FirstPage class represents the initial screen displayed to the first user of the application.
 * It provides a welcome message and a button to navigate to the administrator setup page.
 * 
 * <p>This page is typically shown when the application detects that no users exist in the database,
 * indicating this is the first launch of the application.</p>
 */
public class FirstPage {

    /**
     * The DatabaseHelper instance used for database interactions.
     * This is passed to the AdminSetupPage when navigating to it.
     */
    private final DatabaseHelper databaseHelper;

    /**
     * Constructs a FirstPage with the specified DatabaseHelper.
     * 
     * @param databaseHelper The DatabaseHelper instance to use for database operations
     */
    public FirstPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the first page in the provided primary stage.
     * 
     * <p>The page includes:
     * <ul>
     *   <li>A welcome message for the first user</li>
     *   <li>A continue button that navigates to the AdminSetupPage</li>
     * </ul>
     * </p>
     * 
     * @param primaryStage The primary stage where the scene will be displayed.
     *                     This stage's scene will be replaced with the first page content.
     */
    public void show(Stage primaryStage) {
        // Create the main layout container
        VBox layout = new VBox(5);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Create and style the welcome label
        Label userLabel = new Label(
                "Hello..You are the first person here. \nPlease select continue to setup administrator access");
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Create the continue button
        Button continueButton = new Button("Continue");
        
        // Set action for the continue button
        continueButton.setOnAction(a -> {
            // Navigate to the administrator setup page
            new AdminSetupPage(databaseHelper).show(primaryStage);
        });

        // Add components to the layout
        layout.getChildren().addAll(userLabel, continueButton);
        
        // Create the scene with the layout
        Scene firstPageScene = new Scene(layout, 800, 400);

        // Configure and show the primary stage
        primaryStage.setScene(firstPageScene);
        primaryStage.setTitle("First Page");
        primaryStage.show();
    }
}