package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or
 * quit the application.
 */
public class WelcomeLoginPage {

	private final DatabaseHelper databaseHelper;

	public WelcomeLoginPage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public void show(Stage primaryStage, User user) {

		VBox layout = new VBox(5);
		layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

		Label welcomeLabel = new Label("Welcome!!");
		welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
		
		Label selectLable = new Label("Please select what type you would like to log in as");
		selectLable.setStyle("-fx-font-size: 16px;");

		layout.getChildren().addAll(welcomeLabel,selectLable);
		
		String role = user.getRole();
		System.out.println(role);

		String[] roles = role.split(",");

		for (int i = 0; i < roles.length; i++) {
			String currentRole = roles[i]; 
			Button continueButton = new Button(roles[i].toUpperCase());
			continueButton.setOnAction(a -> {
				if (currentRole.equals("admin")) {
					new AdminHomePage().show(primaryStage,user);
				} else if (currentRole.equals("student")) {
					new StudentHomePage().show(primaryStage,user);
				} else if (currentRole.equals("reviewer")) {
					new ReviewerHomePage().show(primaryStage,user);
				}
//				}
			});
			layout.getChildren().add(continueButton);
		}
		
		// Button to navigate to the user's respective page based on their role
//		Button continueButton = new Button("Continue to your Page");
//		continueButton.setOnAction(a -> {
//
//			if (role.equals("admin")) {
//				new AdminHomePage().show(primaryStage);
//			} else if (role.equals("user")) {
//				new UserHomePage().show(primaryStage);
//			} else if (role.equals("reviewer")) {
//				new ReviewerHomePage().show(primaryStage);
//			}
//		});

		// Button to quit the application
		Button quitButton = new Button("Quit");
		quitButton.setOnAction(a -> {
			databaseHelper.closeConnection();
			Platform.exit(); // Exit the JavaFX application
		});

		// "Invite" button for admin to generate invitation codes
		if ("admin".equals(user.getRole())) {
			Button inviteButton = new Button("Invite");
			inviteButton.setOnAction(a -> {
				new InvitationPage().show(databaseHelper, primaryStage);
			});
			layout.getChildren().add(inviteButton);
		}

		layout.getChildren().add(quitButton);
		Scene welcomeScene = new Scene(layout, 800, 400);

		// Set the scene to primary stage
		primaryStage.setScene(welcomeScene);
		primaryStage.setTitle("Welcome Page");
	}
}