package application;

import java.sql.SQLException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class StaffHomePage {
    private User currentUSER;
    private static final DatabaseHelper databaseHelper = new DatabaseHelper();
    private Questions questionsList = new Questions();
    private Answers answersList = new Answers();
    private VBox questionDetails = new VBox();
    private ScrollPane answersScroll = new ScrollPane();
    private ListView<Question> questionsListView = new ListView<>();
    private TextField searchField = new TextField();

    public void show(Stage primaryStage, User user) {
        currentUSER = user;
        try {
            databaseHelper.connectToDatabase();
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }

        loadData();
        createMainUI(primaryStage);
    }

    private void createMainUI(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setPrefWidth(300);

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
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Label questionsLabel = new Label("Questions");
        questionsLabel.setStyle("-fx-font-size: 14pt; -fx-font-weight: bold;");

        refreshQuestionList();

        questionsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null)
                showQuestionDetails(newVal);
        });

        Button newQuestionButton = new Button("New Question");
        newQuestionButton.setMaxWidth(Double.MAX_VALUE);
        newQuestionButton.setStyle("-fx-font-weight: bold; -fx-background-color: #2196F3; -fx-text-fill: white;");
        newQuestionButton.setOnAction(e -> showCreateQuestionPage());

        leftPanel.getChildren().addAll(searchBar, questionsLabel, questionsListView, newQuestionButton);
        VBox.setVgrow(questionsListView, Priority.ALWAYS);

        VBox centerPanel = new VBox(10);
        centerPanel.setPadding(new Insets(10));
        questionDetails.setSpacing(10);
        answersScroll.setContent(questionDetails);
        answersScroll.setFitToWidth(true);
        centerPanel.getChildren().addAll(new Label("Selected Question"), answersScroll);

        root.setLeft(leftPanel);
        root.setCenter(centerPanel);

        Button viewMessagesButton = new Button("View Messages");
        viewMessagesButton.setOnAction(e -> showStaffMessages());
        leftPanel.getChildren().add(viewMessagesButton);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Discussion Board");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void searchQuestionsByTitle() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            refreshQuestionList();
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

    private void showCreateQuestionPage() {
	CreateQuestionPage createPage = new CreateQuestionPage(questionsList, currentUSER);
	createPage.showAndWait();
	refreshQuestionList();
	saveData();
    }

    private void saveData() {
	try {
	    databaseHelper.saveQuestions(questionsList);
	    databaseHelper.saveAnswers(answersList);
	} catch (SQLException e) {
	    System.err.println("Failed to save data: " + e.getMessage());
	    e.printStackTrace();
	}
    }

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

    private void showQuestionDetails(Question q) {
	questionDetails.getChildren().clear();

	Label titleLabel = new Label(q.getTitle());
	titleLabel.setStyle("-fx-font-size: 16pt; -fx-font-weight: bold;");

	Label authorLabel = new Label("Author: " + q.getName());
	authorLabel.setStyle("-fx-font-style: italic;");

	Label bodyLabel = new Label(q.getTextBody());
	bodyLabel.setWrapText(true);

	VBox answersBox = new VBox(10);
	ArrayList<Answer> answers = answersList.getAnswersByUUID(q.getID());
	for (Answer a : answers) {
	    VBox answerBox = new VBox(5);

	    Label answerAuthor = new Label("Answered by: " + a.getName());
	    answerAuthor.setStyle("-fx-font-style: italic;");

	    Label answerBody = new Label(a.getTextBody());
	    answerBody.setWrapText(true);

	    HBox reviewControls = new HBox(5);
	    Button approveButton = new Button("Approve");
	    approveButton.setOnAction(e -> {
	        a.setUnderReview(false);
	        try {
	            databaseHelper.saveAnswers(answersList);
	            showQuestionDetails(q); 
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	    });

	    reviewControls.getChildren().addAll(approveButton);
	    answerBox.getChildren().addAll(answerAuthor, answerBody, reviewControls);

	    answersBox.getChildren().add(answerBox);
	}

	VBox replySection = new VBox(10);

	TextArea replyArea = new TextArea();

	Button postButton = new Button("Post Reply");
	postButton.setOnAction(e -> {});

	replySection.getChildren().add(postButton);

	questionDetails.getChildren().addAll(titleLabel, authorLabel, bodyLabel, answersBox, replySection);
    }

    private boolean inputValid(String input) {
	input = input.strip();
	return !input.isEmpty() && input.length() <= 2048 && !input.contains("\"") && !input.contains("`");
    }

    private void showAlert(String title, String message) {}

    private void showStaffMessages() {}
}
