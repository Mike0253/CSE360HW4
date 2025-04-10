package application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The InvitationPage class provides an interface for administrators to generate
 * and display invitation codes for new users.
 * 
 * <p>This page allows administrators to:
 * <ul>
 *   <li>Generate unique invitation codes</li>
 *   <li>View the generated codes</li>
 *   <li>Share these codes with potential new users</li>
 * </ul>
 * </p>
 * 
 * <p>Invitation codes are stored in the database and can only be used once
 * for registration.</p>
 */
public class InvitationPage {

    /**
     * Displays the invitation code generation page in the provided stage.
     * 
     * <p>The page contains:
     * <ul>
     *   <li>A title label</li>
     *   <li>A button to generate new codes</li>
     *   <li>A label to display the generated code</li>
     * </ul>
     * </p>
     * 
     * @param databaseHelper The DatabaseHelper instance used to generate and store
     *                       invitation codes in the database
     * @param primaryStage   The primary stage where the scene will be displayed.
     *                       This stage's scene will be replaced with the invitation
     *                       page content.
     */
    public void show(DatabaseHelper databaseHelper, Stage primaryStage) {
        // Create the main layout container
        VBox layout = new VBox();
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Create and style the page title label
        Label userLabel = new Label("Invite");
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Create the code generation button
        Button showCodeButton = new Button("Generate Invitation Code");

        // Create the label to display generated codes
        Label inviteCodeLabel = new Label("");
        inviteCodeLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");

        // Set action for the generate button
        showCodeButton.setOnAction(a -> {
            // Generate and display a new invitation code
            String invitationCode = databaseHelper.generateInvitationCode();
            inviteCodeLabel.setText(invitationCode);
        });

        // Add components to the layout
        layout.getChildren().addAll(userLabel, showCodeButton, inviteCodeLabel);
        
        // Create the scene with the layout
        Scene inviteScene = new Scene(layout, 800, 400);

        // Configure the primary stage
        primaryStage.setScene(inviteScene);
        primaryStage.setTitle("Invite Page");
    }
}