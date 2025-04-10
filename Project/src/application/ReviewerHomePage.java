package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The ReviewerHomePage class represents the main interface for reviewers in the application.
 * It provides functionality to view, search, and review questions and answers, as well as
 * manage private messages.
 */
public class ReviewerHomePage {
    /** The currently logged-in user */
    private User currentUSER;
    
    /** Database connection handler */
    private static final DatabaseHelper databaseHelper = new DatabaseHelper();

    /** Container for all questions */
    private Questions questionsList = new Questions();
    
    /** Container for all answers */
    private Answers answersList = new Answers();

    /** UI component for displaying question details */
    private VBox questionDetails = new VBox();
    
    /** Scroll pane for answers display */
    private ScrollPane answersScroll = new ScrollPane();
    
    /** List view for displaying questions */
    private ListView<Question> questionsListView = new ListView<>();
    
    /** Text field for searching questions */
    private TextField searchField = new TextField();

    /**
     * Initializes and displays the main application window for the reviewer.
     *
     * @param primaryStage the primary stage for this application
     * @param user the currently logged-in reviewer user
     */
    public void show(Stage primaryStage, User user) {
        currentUSER = user;
        try {
            databaseHelper.connectToDatabase(); // Establish database connection
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }

        loadData(); // Load existing data
        createMainUI(primaryStage); // Build the user interface
    }

    /**
     * Constructs the main UI layout with navigation and content areas.
     *
     * @param primaryStage the primary stage for this application
     */
    private void createMainUI(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // === Left Panel Configuration ===
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setPrefWidth(300);

        // Search bar
        HBox searchBar = new HBox(5);
        searchField.setPromptText("Search by title...");
        searchField.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");

        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        searchButton.setOnAction(e -> searchQuestionsByTitle());

        Button clearButton = new Button("Clear");
        clearButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        clearButton.setOnAction(e -> {
            searchField.clear();
            refreshQuestionList();
        });

        searchBar.getChildren().addAll(searchField, searchButton, clearButton);
        HBox.setHgrow(searchField, Priority.ALWAYS); // Make search field expandable

        Label questionsLabel = new Label("Questions");
        questionsLabel.setStyle("-fx-font-size: 14pt; -fx-font-weight: bold;");

        refreshQuestionList(); // Populate the questions list

        // Handle question selection changes
        questionsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null)
                showQuestionDetails(newVal); // Update details when selection changes
        });

        // New Question button
        Button newQuestionButton = new Button("New Question");
        newQuestionButton.setMaxWidth(Double.MAX_VALUE);
        newQuestionButton.setStyle("-fx-font-weight: bold; -fx-background-color: #2196F3; -fx-text-fill: white;");
        newQuestionButton.setOnAction(e -> showCreateQuestionPage());

        leftPanel.getChildren().addAll(searchBar, questionsLabel, questionsListView, newQuestionButton);
        VBox.setVgrow(questionsListView, Priority.ALWAYS); // Make list expandable

        // === Center Panel Configuration ===
        VBox centerPanel = new VBox(10);
        centerPanel.setPadding(new Insets(10));
        questionDetails.setSpacing(10);
        answersScroll.setContent(questionDetails);
        answersScroll.setFitToWidth(true); // Enable horizontal scrolling
        centerPanel.getChildren().addAll(new Label("Selected Question"), answersScroll);

        root.setLeft(leftPanel);
        root.setCenter(centerPanel);
        
        Button viewMessagesButton = new Button("View Messages");
        viewMessagesButton.setOnAction(e -> showReviewerMessages());
        leftPanel.getChildren().add(viewMessagesButton);

        // Finalize window setup
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Discussion Board");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Searches for questions by title and updates the list view with matching results.
     * The search is case-insensitive and looks for partial matches in question titles.
     */
    private void searchQuestionsByTitle() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            refreshQuestionList(); // Show all questions if search is empty
            return;
        }

        ObservableList<Question> filteredQuestions = FXCollections.observableArrayList();
        for (Question question : questionsList.getQuestionArray()) {
            if (question.getTitle().toLowerCase().contains(searchText)) {
                filteredQuestions.add(question);
            }
        }

        questionsListView.setItems(filteredQuestions);
        questionsListView.refresh();
    }

    /**
     * Updates the questions list view with current data from the questionsList.
     * The list is displayed with numbered items showing question titles.
     */
    private void refreshQuestionList() {
        questionsListView.setItems(FXCollections.observableArrayList(questionsList.getQuestionArray()));
        questionsListView.setCellFactory(lv -> new ListCell<Question>() {
            @Override
            protected void updateItem(Question item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : (getIndex() + 1 + ". " + item.getTitle()));
            }
        });
    } 

    /**
     * Opens the question creation dialog and refreshes the question list after creation.
     */
    private void showCreateQuestionPage() {
        CreateQuestionPage createPage = new CreateQuestionPage(questionsList, currentUSER);
        createPage.showAndWait(); // Modal dialog
        refreshQuestionList();
        saveData(); // Persist changes
    }

    /**
     * Persists questions and answers to the database.
     * 
     * @throws SQLException if there is an error during database operations
     */
    private void saveData() {
        try {
            databaseHelper.saveQuestions(questionsList);
            databaseHelper.saveAnswers(answersList);
        } catch (SQLException e) {
            System.err.println("Failed to save data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads questions and answers from the database and refreshes the question list.
     * 
     * @throws SQLException if there is an error during database operations
     */
    private void loadData() {
        try {
            questionsList = databaseHelper.loadQuestions();
            answersList = databaseHelper.loadAnswer();
        } catch (SQLException e) {
            System.err.println("Failed to load data: " + e.getMessage());
            e.printStackTrace();
        }
        refreshQuestionList();
    }

    /**
     * Displays detailed view of a selected question including its answers and review controls.
     *
     * @param q the Question object to display details for
     */
    private void showQuestionDetails(Question q) {
        questionDetails.getChildren().clear();

        // Question Display Section
        Label titleLabel = new Label(q.getTitle());
        titleLabel.setStyle("-fx-font-size: 16pt; -fx-font-weight: bold;");

        Label authorLabel = new Label("Author: " + q.getName());
        authorLabel.setStyle("-fx-font-style: italic;");

        Label bodyLabel = new Label(q.getTextBody());
        bodyLabel.setWrapText(true);

        Separator separator = new Separator();
        
        Button questionReviewButton = new Button("Add Review for Question");
        questionReviewButton.setOnAction(e -> 
            new CreateReviewPage(
                currentUSER.getID(),
                q.getID(),
                null
            ).show()
        );
        questionDetails.getChildren().add(questionReviewButton);

        // Answers Display Section
        VBox answersBox = new VBox(10);
        answersBox.setPadding(new Insets(10));

        Label answersLabel = new Label("Answers:");
        answersLabel.setStyle("-fx-font-weight: bold;");
        answersBox.getChildren().add(answersLabel);

        // Display existing answers
        ArrayList<Answer> answers = answersList.getAnswersByUUID(q.getID());
        for (Answer a : answers) {
            VBox answerBox = new VBox(5);
            answerBox.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10;");

            Label answerAuthor = new Label("Answered by: " + a.getName());
            answerAuthor.setStyle("-fx-font-style: italic;");

            Label answerBody = new Label(a.getTextBody());
            answerBody.setWrapText(true);
            
            Button answerReviewButton = new Button("Add Review for Answer");
            answerReviewButton.setOnAction(e -> 
                new CreateReviewPage(
                    currentUSER.getID(),
                    q.getID(),
                    a.getID()
                ).show()
            );
            
            HBox reviewControls = new HBox(5);
            Button approveButton = new Button("Approve");
            Button messageButton = new Button("Message Student");

            approveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            approveButton.setOnAction(e -> {
                a.setUnderReview(false);
                try {
                    databaseHelper.saveAnswers(answersList);
                    showQuestionDetails(q); // Refresh view
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });

            messageButton.setOnAction(e -> {
                List<Review> reviews = null;
                try {
                    reviews = databaseHelper.getReviewsByAnswerId(a.getID());
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                if (!reviews.isEmpty()) {
                    new MessageDialog(
                        reviews.get(0).getId(), // Use review UUID
                        currentUSER.getID(),
                        a.getUserUUID()
                    ).show();
                }
            });

            reviewControls.getChildren().addAll(approveButton, messageButton);
            answerBox.getChildren().add(reviewControls);
         
            answerBox.getChildren().add(answerReviewButton);

            answerBox.getChildren().addAll(answerAuthor, answerBody);
            answersBox.getChildren().add(answerBox);
        }

        // Reply Input Section
        VBox replySection = new VBox(10);
        replySection.setPadding(new Insets(10));
        replySection.setStyle("-fx-background-color: #e8e8e8; -fx-padding: 10;");

        Label replyLabel = new Label("Add a Reply:");
        TextField nameField = new TextField();
        nameField.setPromptText("Name (optional)");

        TextArea replyArea = new TextArea();
        replyArea.setPromptText("Enter your reply here...");
        replyArea.setWrapText(true);
        replyArea.setPrefHeight(100);

        Button postButton = new Button("Post Reply");
        postButton.setStyle("-fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white;");

        postButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String replyText = replyArea.getText().trim();

            if (!inputValid(replyText)) {
                showAlert("Invalid Reply", "Reply must be 1-2048 characters and cannot contain \" or `");
                return;
            }

            if (name.isEmpty())
                name = "Anonymous";

            Answer newAnswer = new Answer(name, replyText, q.getID(), currentUSER.getID());
            newAnswer.setRepuation(currentUSER.getReputation());
            answersList.addAnswer(newAnswer);

            nameField.clear();
            replyArea.clear();
            showQuestionDetails(q); // Refresh to show new answer
            saveData(); // Persist changes
        });

        replySection.getChildren().addAll(replyLabel, nameField, replyArea, postButton);

        // Combine all sections
        questionDetails.getChildren().addAll(titleLabel, authorLabel, bodyLabel,
                separator, answersBox, new Separator(), replySection);
    }

    /**
     * Validates user input for answers and replies.
     *
     * @param input the text input to validate
     * @return true if the input is valid, false otherwise
     */
    private boolean inputValid(String input) {
        input = input.strip();
        return !input.isEmpty() &&
                input.length() <= 2048 &&
                !input.contains("\"") &&
                !input.contains("`");
    }

    /**
     * Displays an error alert dialog with the specified title and message.
     *
     * @param title the title of the alert dialog
     * @param message the message content of the alert
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Displays a window showing all private messages received by the current reviewer.
     */
    private void showReviewerMessages() {
        privateMessageList allMessages = databaseHelper.getMessagesToUser(currentUSER.getID());
        ListView<privateMessage> messageList = new ListView<>();
        messageList.setCellFactory(lv -> new ListCell<privateMessage>() {
            @Override
            protected void updateItem(privateMessage msg, boolean empty) {
                super.updateItem(msg, empty);
                if (empty || msg == null) {
                    setText(null);
                } else {
                    setText("From: " + msg.getFromUUID() + "\n" + msg.getTextBody());
                }
            }
        });
        messageList.getItems().addAll(allMessages.getPrivateMessageArray());
        
        Stage msgStage = new Stage();
        msgStage.setScene(new Scene(new ScrollPane(messageList), 400, 300));
        msgStage.setTitle("Received Messages");
        msgStage.show();
    }
}