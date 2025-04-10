package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

public class StudentHomePage {

	// Database connection handler
	private static final DatabaseHelper databaseHelper = new DatabaseHelper();

	// Data containers
	private Questions questionsList = new Questions();
	private Answers answersList = new Answers();

	// UI components
	private VBox questionDetails = new VBox();
	private ScrollPane answersScroll = new ScrollPane();
	private ListView<Question> questionsListView = new ListView<>();
	private TextField searchField = new TextField();
	private User currentUSER;

	/**
	 * Initializes and displays the main application window.
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

	/** Constructs the main UI layout with navigation and content areas */
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

		// Finalize window setup
		Scene scene = new Scene(root, 800, 600);
		primaryStage.setTitle("Discussion Board");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/** Searches for questions by title and updates the list view */
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

	/** Updates the questions list view with current data */
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

	/** Opens the question creation dialog and refreshes the list */
	private void showCreateQuestionPage() {
		CreateQuestionPage createPage = new CreateQuestionPage(questionsList, currentUSER);
		createPage.showAndWait(); // Modal dialog
		refreshQuestionList();
		saveData(); // Persist changes
	}

	/** Persists questions and answers to the database */
	private void saveData() {
		try {
			databaseHelper.saveQuestions(questionsList);
			databaseHelper.saveAnswers(answersList);
		} catch (SQLException e) {
			System.err.println("Failed to save data: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/** Loads questions and answers from the database */
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

	/** Displays detailed view of a selected question including answers */
	private void showQuestionDetails(Question q) {
		questionDetails.getChildren().clear();

		// Question Display Section (existing code remains unchanged)
		Label titleLabel = new Label(q.getTitle());
		titleLabel.setStyle("-fx-font-size: 16pt; -fx-font-weight: bold;");

		Label authorLabel = new Label("Author: " + q.getName());
		authorLabel.setStyle("-fx-font-style: italic;");

		Label bodyLabel = new Label(q.getTextBody());
		bodyLabel.setWrapText(true);

		Separator separator = new Separator();

		// Answers Display Section
		VBox answersBox = new VBox(10);
		answersBox.setPadding(new Insets(10));

		Label answersLabel = new Label("Answers:");
		answersLabel.setStyle("-fx-font-weight: bold;");
		answersBox.getChildren().add(answersLabel);

		Map<UUID, Label> answerRepLabels = new HashMap<>(); // <<====

		// Display existing answers with voting buttons
		ArrayList<Answer> answers = answersList.getAnswersByUUID(q.getID());
		
		for (Answer a : answers) {
			VBox answerBox = new VBox(5);
			answerBox.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10;");
			
			if (a.getUnderReview()) {
			    Label statusLabel = new Label("Under Review");
			    statusLabel.setStyle("-fx-text-fill: #ff9800; -fx-font-weight: bold;");
			    answerBox.getChildren().add(0, statusLabel);
			} else {
			    Label statusLabel = new Label("Approved");
			    statusLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
			    answerBox.getChildren().add(0, statusLabel);
			}

			Label answerAuthor = new Label("Answered by: " + a.getName());
			answerAuthor.setStyle("-fx-font-style: italic;");

			Label answerBody = new Label(a.getTextBody());
			answerBody.setWrapText(true);

			// Voting buttons and reputation label
			HBox voteBox = new HBox(5);
			Button upButton = new Button("↑");
			Button downButton = new Button("↓");
			// Create the label and store it in the map // <<====
			Label repLabel = new Label(String.valueOf(a.getRepuation(databaseHelper)));
			answerRepLabels.put(a.getID(), repLabel); // <<==== Store the label associated with the answer ID

			// Style buttons
			upButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
			downButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

			// Button actions
			upButton.setOnAction(event -> {
				UUID currentUserId = currentUSER.getID();
				// Check if already upvoted (optional, depends on Answer class logic)
				// if (!a.getUpvotedBy().contains(currentUserId)) {
				answersList.increaseReputation(databaseHelper, a.getID(), currentUserId); // <<==== Call the method in
																							// Answers
				// Update ALL displayed reputation labels for this question // <<====
				for (Map.Entry<UUID, Label> entry : answerRepLabels.entrySet()) { // <<====
					Answer updatedAnswer = answersList.getByUUID(entry.getKey()); // <<==== Get the potentially updated
																					// answer
					if (updatedAnswer != null) { // <<==== Check if answer still exists
						entry.getValue().setText(Integer.toString(updatedAnswer.getRepuation(databaseHelper))); // <<====
																												// Update
																												// label
																												// text
					}
				}
				saveData(); // Persist changes to database
				// }
			});

			downButton.setOnAction(event -> {
				UUID currentUserId = currentUSER.getID();
				// Check if already downvoted (optional, depends on Answer class logic)
				// if (!a.getDownvotedBy().contains(currentUserId)) {
				answersList.decreaseReputation(databaseHelper, a.getID(), currentUserId); // <<==== Call the method in
																							// Answers
				// Update ALL displayed reputation labels for this question // <<====
				for (Map.Entry<UUID, Label> entry : answerRepLabels.entrySet()) { // <<====
					Answer updatedAnswer = answersList.getByUUID(entry.getKey()); // <<==== Get the potentially updated
																					// answer
					if (updatedAnswer != null) { // <<==== Check if answer still exists
						entry.getValue().setText(Integer.toString(updatedAnswer.getRepuation(databaseHelper))); // <<====
																												// Update
																												// label
																												// text
					}
				}
				saveData(); // Persist changes to database
				// }
			});

			voteBox.getChildren().addAll(upButton, downButton, repLabel);
			answerBox.getChildren().addAll(answerAuthor, answerBody, voteBox);
			answersBox.getChildren().add(answerBox);
			
			if (a.getUnderReview()) {
			    Button messageReviewerButton = new Button("Message Reviewer");
			    messageReviewerButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
			    
			    messageReviewerButton.setOnAction(e -> {
			        try {
			            List<Review> reviews = databaseHelper.getReviewsByAnswerId(a.getID());
			            if (reviews.isEmpty()) {
			                showAlert("No Review Available", "This answer has not been reviewed yet.");
			            } else {
			                Review review = reviews.get(0);
			                new MessageDialog(
			                    review.getId(),
			                    currentUSER.getID(),
			                    review.getReviewerId()
			                ).show();
			            }
			        } catch (SQLException ex) {
			            ex.printStackTrace();
			            showAlert("Database Error", "Could not retrieve review information.");
			        }
			    });
			    
			    HBox buttonContainer = new HBox(10);
			    buttonContainer.getChildren().addAll(messageReviewerButton);
			    answerBox.getChildren().add(buttonContainer);
			}
			
		}
		
		VBox reviewsBox = new VBox(10);
	    reviewsBox.setPadding(new Insets(10));
	    Label reviewsLabel = new Label("Reviews:");
	    reviewsLabel.setStyle("-fx-font-weight: bold;");
	    reviewsBox.getChildren().add(reviewsLabel);

	    try {
	        // Load reviews for this question
	        List<Review> questionReviews = databaseHelper.getReviewsByQuestionId(q.getID());
	        for (Review review : questionReviews) {
	            Label reviewLabel = new Label("[QUESTION REVIEW] " + review.getContent());
	            reviewLabel.setWrapText(true);
	            reviewLabel.setStyle("-fx-background-color: #fff3cd; -fx-padding: 5;");
	            reviewsBox.getChildren().add(reviewLabel);
	        }

	        // Load reviews for all answers
	        ArrayList<Answer> answers1 = answersList.getAnswersByUUID(q.getID());
	        for (Answer a : answers1) {
	            List<Review> answerReviews = databaseHelper.getReviewsByAnswerId(a.getID());
	            for (Review review : answerReviews) {
	                Label reviewLabel = new Label("[ANSWER REVIEW] " + review.getContent());
	                reviewLabel.setWrapText(true);
	                reviewLabel.setStyle("-fx-background-color: #d4edda; -fx-padding: 5;");
	                reviewsBox.getChildren().add(reviewLabel);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
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
			newAnswer.setUnderReview(true);
			answersList.addAnswer(newAnswer);
		

			nameField.clear();
			replyArea.clear();
			showQuestionDetails(q); // Refresh to show new answer
			saveData(); // Persist changes
		});

		replySection.getChildren().addAll(replyLabel, nameField, replyArea, postButton);

		// Combine all sections
		questionDetails.getChildren().addAll(titleLabel, authorLabel, bodyLabel, separator, answersBox, reviewsBox, new Separator(),
				replySection);
	}

	/** Validates user input for answers and replies */
	private boolean inputValid(String input) {
		input = input.strip();
		return !input.isEmpty() && input.length() <= 2048 && !input.contains("\"") && !input.contains("`");
	}

	/** Displays an error alert dialog */
	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}