// CreateQuestionPage.java
package application;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The CreateQuestionPage class provides a graphical interface for users to create and submit new questions.
 * It includes input validation for question titles and details, and handles the submission process.
 * The window is modal, blocking interaction with other application windows while open.
 */
public class CreateQuestionPage extends Stage {
    private final Questions questionsList; // List to store submitted questions
    private User currentUSER; // Current user creating the question

    /**
     * Constructs a new CreateQuestionPage window.
     * 
     * @param questionsList The Questions collection where new questions will be stored
     * @param user The currently logged-in user who is creating the question
     */
    public CreateQuestionPage(Questions questionsList, User user) {
        currentUSER = user;
        this.questionsList = questionsList;
        initModality(Modality.APPLICATION_MODAL); // Block interaction with other windows
        setTitle("Ask New Question");
        createUI(); // Initialize the user interface components
    }

    /**
     * Initializes and arranges all UI components for the question creation form.
     * Includes fields for name (optional), question title, and question details,
     * along with a submission button that triggers validation and processing.
     */
    private void createUI() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20)); // Add padding around the layout

        // Name input field (optional)
        Label nameLabel = new Label("Your Name (optional):");
        TextField nameField = new TextField();
        nameField.setPromptText("Anonymous"); // Gray hint text

        // Required question title field
        Label titleLabel = new Label("Question Title:");
        TextField titleField = new TextField();

        // Detailed question input area
        Label questionLabel = new Label("Question Details:");
        TextArea questionArea = new TextArea();
        questionArea.setWrapText(true); // Enable text wrapping
        questionArea.setPrefHeight(150); // Set initial height

        // Submission button with validation handler
        Button submitButton = new Button("Submit Question");
        submitButton.setStyle("-fx-font-weight: bold;");

        // Handle submission with trimmed input values
        submitButton.setOnAction(e -> handleSubmission(
                nameField.getText().trim(),
                titleField.getText().trim(),
                questionArea.getText().trim()));

        // Add all components to the layout
        root.getChildren().addAll(nameLabel, nameField, titleLabel, titleField,
                questionLabel, questionArea, submitButton);

        Scene scene = new Scene(root, 400, 400);
        setScene(scene);
    }

    /**
     * Validates and processes user input for question submission.
     * Checks for valid input length and prohibited characters, then creates
     * and stores a new Question object if validation passes.
     * 
     * @param name  Submitter's name (empty string defaults to "Anonymous")
     * @param title Question title (must be 1-2048 valid characters)
     * @param body  Question details (must be 1-2048 valid characters)
     */
    private void handleSubmission(String name, String title, String body) {
        // Validate required fields
        if (!inputValid(title) || !inputValid(body)) {
            showErrorAlert();
            return; // Abort submission if validation fails
        }

        // Default to "Anonymous" if name not provided
        if (name.isEmpty())
            name = "Anonymous";

        // Create and store new question
        Question newQuestion = new Question(name, title, body, currentUSER.getID());
        questionsList.addQuestion(newQuestion);
        close(); // Close the window after submission
    }

    /**
     * Validates input text based on length and prohibited characters.
     * 
     * @param input The text to validate (will be stripped of leading/trailing whitespace)
     * @return true if input is between 1-2048 characters and doesn't contain " or `
     */
    private boolean inputValid(String input) {
        input = input.strip();
        // Check length constraints
        if (input.length() < 1 || input.length() > 2048)
            return false;
        // Check for prohibited characters
        return !input.contains("\"") && !input.contains("`");
    }

    /**
     * Displays an error alert dialog when input validation fails.
     * The alert informs the user about the input requirements and
     * must be dismissed before continuing.
     */
    private void showErrorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText("Validation Error");
        alert.setContentText("Input must be 1-2048 characters and cannot contain \" or `");
        alert.showAndWait(); // Block interaction until dismissed
    }
}