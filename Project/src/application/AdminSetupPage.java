package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * The SetupAdmin class handles the setup process for creating an administrator
 * account. This is intended to be used by the first user to initialize the
 * system with admin credentials.
 */
public class AdminSetupPage {

	private final DatabaseHelper databaseHelper;
	
	/**
     * Constructs an AdminSetupPage with a database helper
     * @param databaseHelper The database helper for user registration
     */
	public AdminSetupPage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}
	
	/**
     * Shows the admin setup page in the provided stage
     * @param primaryStage The primary stage to display the setup interface
     */
	public void show(Stage primaryStage) {
		// labels and requirements for user name and password
		Label usernameLabel = new Label();
		usernameLabel.setStyle("-fx-font-size: 20px;");
		usernameLabel.setText("Username Requirements:");

		Label usernameRequirements = new Label();
		usernameRequirements.setStyle("-fx-font-size: 14px;");
		usernameRequirements.setText(
				"- Minimum length of 4 characters and no longer than 16 characters\n" + "- Must start with a letter\n"
						+ "- Can include ., _, or - but must be sourounded by a letter or a number");

		Label passwordLabel = new Label();
		passwordLabel.setStyle("-fx-font-size: 20px;");
		passwordLabel.setText("Password Requirements:");

		Label passwordRequirements = new Label();
		passwordRequirements.setStyle("-fx-font-size: 14px;");
		passwordRequirements.setText("- At least one upper case letter\n" + "- At least one lower case letter\n"
				+ "- At least one digit\n" + "- At least one special character\n" + "- At least 8 characters");

		// Label to display error messages for invalid input or registration issues
		Label errorLabel = new Label();
		errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

		// Input fields for userName and password
		TextField userNameField = new TextField();
		userNameField.setPromptText("Enter Admin userName");
		userNameField.setMaxWidth(250);

		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Enter Password");
		passwordField.setMaxWidth(250);

		Button setupButton = new Button("Setup");

		setupButton.setOnAction(a -> {
			// Retrieve user input
			String userName = userNameField.getText();
			String password = passwordField.getText();
			try {
				// from setupAccountPage
				// Input has been provided, let's see if it is a valid date or not
				String errUserMessage = UserNameRecognizer.checkForValidUserName(userName);
				if (errUserMessage == "") {
					// Check if the user already exists
					// check if password is acceptable strength
					String errPasswordMessage = PasswordEvaluator.evaluatePassword(password);
					if (errPasswordMessage == "") {

						// Validate the invitation code
						// Create a new User object with admin role and register in the database
						User user = new User(userName, password, "admin");
						databaseHelper.register(user);
						System.out.println("Administrator setup completed.");

						// Navigate to the Welcome Login Page
						new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
					} else {
						// split errors to fit better in the text box
						errorLabel.setText(errPasswordMessage.replace("; ", "\n"));
					}
				} else {
					errorLabel.setText(errUserMessage);
				}
			} catch (SQLException e) {
				System.err.println("Database error: " + e.getMessage());
				e.printStackTrace();
			}
		});

		VBox input = new VBox(10);
		input.setStyle("-fx-padding: 20; -fx-alignment: center;");
		input.getChildren().addAll(userNameField, passwordField, setupButton, errorLabel);

		VBox infoInput = new VBox(10);
		infoInput.setStyle("-fx-alignment: center-left;");
		infoInput.getChildren().addAll(usernameLabel, usernameRequirements, passwordLabel, passwordRequirements);

		HBox layout = new HBox(10);
		layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
		layout.getChildren().addAll(infoInput, input);

		primaryStage.setScene(new Scene(layout, 800, 400));
		primaryStage.setTitle("Administrator Setup");
		primaryStage.show();
	}
}
