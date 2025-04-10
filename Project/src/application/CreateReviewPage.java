package application;

import java.sql.SQLException;
import java.util.UUID;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The CreateReviewPage class provides a graphical interface for users to create and submit reviews.
 * It allows users to enter review content and handles the submission process to the database.
 * The window is modal, blocking interaction with other application windows while open.
 */
public class CreateReviewPage extends Stage {
    private final Review review; // The review object being created
    private final DatabaseHelper dbHelper = new DatabaseHelper(); // Database helper for saving reviews

    /**
     * Constructs a new CreateReviewPage window.
     * 
     * @param reviewerId The UUID of the user creating the review
     * @param questionId The UUID of the question being reviewed
     * @param answerId The UUID of the answer being reviewed
     */
    public CreateReviewPage(UUID reviewerId, UUID questionId, UUID answerId) {
        this.review = new Review(reviewerId, questionId, answerId, "");
        initModality(Modality.APPLICATION_MODAL); // Block interaction with other windows
        setTitle("Create Review");
        createUI(); // Initialize the user interface components
    }

    /**
     * Initializes and arranges all UI components for the review creation form.
     * Includes a text area for review content and a submission button that
     * triggers validation and database saving.
     */
    private void createUI() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20)); // Add padding around the layout

        // Review content input area
        Label contentLabel = new Label("Review Content:");
        TextArea contentArea = new TextArea();
        contentArea.setWrapText(true); // Enable text wrapping
        contentArea.setPrefHeight(150); // Set initial height

        // Submission button with validation handler
        Button submitButton = new Button("Submit Review");
        submitButton.setStyle("-fx-font-weight: bold;");

        // Handle submission with trimmed input value
        submitButton.setOnAction(e -> {
            String content = contentArea.getText().trim();
            if (content.isEmpty()) {
                showErrorAlert("Content cannot be empty!");
                return;
            }
            
            // Update and save the review
            review.setContent(content);
            try {
                dbHelper.saveReview(review);
                close(); // Close the window after successful submission
            } catch (SQLException ex) {
                showErrorAlert("Failed to save review: " + ex.getMessage());
            }
        });

        // Add all components to the layout
        root.getChildren().addAll(contentLabel, contentArea, submitButton);
        setScene(new Scene(root, 400, 300));
    }

    /**
     * Displays an error alert dialog with a custom message.
     * The alert must be dismissed before continuing.
     * 
     * @param message The error message to display
     */
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null); // No header text
        alert.setContentText(message);
        alert.showAndWait(); // Block interaction until dismissed
    }
}