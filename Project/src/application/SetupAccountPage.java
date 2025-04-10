package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.Node;
import java.sql.SQLException;
import java.util.UUID;
import java.util.stream.Collectors;



public class SetupAccountPage {
    private final DatabaseHelper databaseHelper;

    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: #f5f5f5;");

        // Left side - Requirements
        VBox leftPanel = new VBox(15);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setStyle("-fx-pref-width: 300px;");

        Label usernameReqTitle = new Label("Username Requirements:");
        usernameReqTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label usernameReqContent = new Label(
            "• Minimum length of 4 characters and no longer than 16\n" +
            "• Must start with a letter\n" +
            "• Can include ., _, or - but must be surrounded by letters/numbers"
        );
        usernameReqContent.setStyle("-fx-font-size: 13px;");

        Label passwordReqTitle = new Label("Password Requirements:");
        passwordReqTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label passwordReqContent = new Label(
            "• At least one upper case letter\n" +
            "• At least one lower case letter\n" +
            "• At least one digit\n" +
            "• At least one special character\n" +
            "• At least 8 characters"
        );
        passwordReqContent.setStyle("-fx-font-size: 13px;");

        leftPanel.getChildren().addAll(
            usernameReqTitle, usernameReqContent,
            new Separator(),
            passwordReqTitle, passwordReqContent
        );

        // Right side - Input Form
        VBox rightPanel = new VBox(15);
        rightPanel.setPadding(new Insets(10));
        rightPanel.setStyle("-fx-pref-width: 300px;");

        // Input Fields
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        styleTextField(usernameField);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        styleTextField(passwordField);

        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter invitation code");
        styleTextField(inviteCodeField);

        // Role Selection (Vertical Checkboxes)
        Label roleLabel = new Label("Select Your Roles:");
        roleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox rolesContainer = new VBox(5);
        rolesContainer.setPadding(new Insets(10));
        rolesContainer.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");

        CheckBox studentCheck = createStyledCheckBox("Student", true);
        CheckBox adminCheck = createStyledCheckBox("Admin", false);
        CheckBox reviewerCheck = createStyledCheckBox("Reviewer", false);
        CheckBox instructorCheck = createStyledCheckBox("Instructor", false);
        CheckBox staffCheck = createStyledCheckBox("Staff", false);

        rolesContainer.getChildren().addAll(studentCheck, adminCheck, reviewerCheck, instructorCheck, staffCheck);

        // Error Label
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");

        // Submit Button
        Button submitButton = new Button("Create Account");
        submitButton.setStyle("-fx-font-size: 14px; -fx-pref-width: 200px; -fx-pref-height: 35px; " +
                            "-fx-background-color: #4CAF50; -fx-text-fill: white;");

        rightPanel.getChildren().addAll(
            usernameField, passwordField, inviteCodeField,
            roleLabel, rolesContainer,
            submitButton, errorLabel
        );

        // Main Layout
        HBox contentBox = new HBox(30);
        contentBox.getChildren().addAll(leftPanel, rightPanel);
        mainLayout.getChildren().add(contentBox);

        // Event Handling
        submitButton.setOnAction(a -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String code = inviteCodeField.getText();

            // Collect selected roles
            ObservableList<String> selectedRoles = FXCollections.observableArrayList();
            for (Node node : rolesContainer.getChildren()) {
                if (node instanceof CheckBox) {
                    CheckBox cb = (CheckBox) node;
                    if (cb.isSelected()) {
                        selectedRoles.add(cb.getText());
                    }
                }
            }

            // Validate at least one role selected
            if (selectedRoles.isEmpty()) {
                errorLabel.setText("Please select at least one role");
                return;
            }
            
//            System.out.println();

            try {
                String userError = UserNameRecognizer.checkForValidUserName(username);
                if (userError.isEmpty()) {
                    if (!databaseHelper.doesUserExist(username)) {
                        String passError = PasswordEvaluator.evaluatePassword(password);
                        if (passError.isEmpty()) {
                            if (databaseHelper.validateInvitationCode(code) || true) {
                                // For now using first role selected
                                // You might want to modify your User class to handle multiple roles
                            	
                                User user = new User(username, password,selectedRoles.stream().toList());
                                
                                databaseHelper.register(user);
                                new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
                            } else {
                            	/// dead code, leaving it intentionally.
                                errorLabel.setText("Invalid invitation code");
                            }
                        } else {
                            errorLabel.setText(passError.replace("; ", "\n"));
                        }
                    } else {
                        errorLabel.setText("Username already exists");
                    }
                } else {
                    errorLabel.setText(userError);
                }
            } catch (SQLException e) {
                errorLabel.setText("Database error. Please try again.");
                e.printStackTrace();
            }
        });

        primaryStage.setScene(new Scene(mainLayout, 700, 500));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }

    private void styleTextField(TextField field) {
        field.setStyle("-fx-font-size: 14px; -fx-pref-width: 250px; -fx-pref-height: 30px;");
    }

    private CheckBox createStyledCheckBox(String text, boolean selected) {
        CheckBox checkBox = new CheckBox(text);
        checkBox.setSelected(selected);
        checkBox.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
        return checkBox;
    }
}