package application;

import java.util.UUID;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The MessageDialog class provides a user interface for private messaging between users.
 * It displays a conversation thread and allows sending new messages related to a specific review.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Displays message history in a scrollable list</li>
 *   <li>Provides input area for new messages</li>
 *   <li>Automatically loads existing messages</li>
 *   <li>Formats messages with sender info and timestamps</li>
 * </ul>
 * </p>
 */
public class MessageDialog {
    private Stage stage;
    private UUID reviewUUID;
    private UUID currentUserUUID;
    private UUID otherUserUUID;
    private DatabaseHelper dbHelper = new DatabaseHelper();
    private ListView<privateMessage> messageListView = new ListView<>();
    private TextArea messageArea = new TextArea();

    /**
     * Constructs a new MessageDialog for a specific review conversation.
     * 
     * @param reviewUUID The UUID of the review this conversation is about
     * @param currentUserUUID The UUID of the currently logged-in user
     * @param otherUserUUID The UUID of the other participant in the conversation
     */
    public MessageDialog(UUID reviewUUID, UUID currentUserUUID, UUID otherUserUUID) {
        this.reviewUUID = reviewUUID;
        this.currentUserUUID = currentUserUUID;
        this.otherUserUUID = otherUserUUID;
        stage = new Stage();
        buildUI();
        loadMessages(); // Load existing messages when dialog opens
    }

    /**
     * Loads and displays all messages related to the current review.
     */
    private void loadMessages() {
        privateMessageList messages = dbHelper.getMessageByResponceUUID(reviewUUID);
        messageListView.getItems().setAll(messages.getPrivateMessageArray());
    }

    /**
     * Builds the user interface components for the message dialog.
     */
    private void buildUI() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Configure message list view
        messageListView.setCellFactory(lv -> new MessageListCell());
        root.setCenter(messageListView);

        // Configure input area
        VBox inputBox = new VBox(10);
        messageArea.setPromptText("Type your message...");
        messageArea.setWrapText(true);
        messageArea.setPrefHeight(60);

        // Configure send button
        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessage());

        // Layout input components
        HBox buttonBox = new HBox(10, sendButton);
        inputBox.getChildren().addAll(messageArea, buttonBox);
        root.setBottom(inputBox);

        // Set up the stage
        stage.setScene(new Scene(root, 400, 300));
        stage.setTitle("Direct Messages");
    }

    /**
     * Sends a new message and updates the conversation view.
     * Validates that the message text is not empty before sending.
     */
    private void sendMessage() {
        String text = messageArea.getText().trim();
        if (!text.isEmpty()) {
            privateMessage msg = new privateMessage(
                text, 
                currentUserUUID, 
                otherUserUUID, 
                reviewUUID
            );
            dbHelper.saveMessage(msg);
            messageArea.clear();
            loadMessages(); // Refresh the message list
        }
    }

    /**
     * Displays the message dialog to the user.
     */
    public void show() {
        stage.show();
    }

    /**
     * Custom ListCell implementation for formatting individual messages in the conversation.
     * Displays sender information, message content, and timestamp.
     */
    private class MessageListCell extends ListCell<privateMessage> {
        @Override
        protected void updateItem(privateMessage msg, boolean empty) {
            super.updateItem(msg, empty);
            if (empty || msg == null) {
                setText(null);
                setGraphic(null);
            } else {
                VBox container = new VBox(5);
                // Determine sender display name
                String senderName = msg.getFromUUID().equals(currentUserUUID) ? "You" : 
                    (currentUserUUID.equals(msg.getToUUID()) ? "Reviewer" : "Student");
                
                // Create message components
                Label senderLabel = new Label(senderName);
                Label contentLabel = new Label(msg.getTextBody());
                Label timeLabel = new Label("Sent: " + msg.getDate().toString());
                
                // Apply styling
                senderLabel.setStyle("-fx-font-weight: bold;");
                timeLabel.setStyle("-fx-font-size: 0.8em; -fx-text-fill: #666;");
                
                // Assemble message display
                container.getChildren().addAll(senderLabel, contentLabel, timeLabel);
                setGraphic(container);
            }
        }
    }
}