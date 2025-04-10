package application;

import java.time.LocalDateTime;
import java.util.UUID;
import java.io.Serializable;

/**
 * The Message class represents a communication between two users in the system.
 * It implements Serializable to support object serialization for storage and transmission.
 * 
 * <p>Each message contains:
 * <ul>
 *   <li>Unique identifier</li>
 *   <li>Sender and receiver identifiers</li>
 *   <li>Message content</li>
 *   <li>Timestamp of creation</li>
 *   <li>Read status flag</li>
 * </ul>
 * </p>
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private UUID senderId;
    private UUID receiverId;
    private String content;
    private LocalDateTime timestamp;
    private boolean isRead;

    /**
     * Constructs a new Message with the specified sender, receiver, and content.
     * Automatically generates a unique ID and sets the creation timestamp.
     * 
     * @param senderId   The UUID of the message sender
     * @param receiverId The UUID of the message recipient
     * @param content    The text content of the message
     */
    public Message(UUID senderId, UUID receiverId, String content) {
        this.id = UUID.randomUUID();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }

    /**
     * Gets the unique identifier of this message.
     * @return The message UUID
     */
    public UUID getId() { return id; }

    /**
     * Gets the UUID of the message sender.
     * @return The sender's UUID
     */
    public UUID getSenderId() { return senderId; }

    /**
     * Gets the UUID of the message recipient.
     * @return The receiver's UUID
     */
    public UUID getReceiverId() { return receiverId; }

    /**
     * Gets the text content of the message.
     * @return The message content
     */
    public String getContent() { return content; }

    /**
     * Gets the timestamp when the message was created.
     * @return The creation timestamp
     */
    public LocalDateTime getTimestamp() { return timestamp; }

    /**
     * Checks if the message has been read by the recipient.
     * @return true if the message has been read, false otherwise
     */
    public boolean isRead() { return isRead; }

    /**
     * Marks the message as having been read by the recipient.
     */
    public void markAsRead() { isRead = true; }

    /**
     * Sets the unique identifier for this message.
     * Note: This should typically only be used when reconstructing persisted messages.
     * 
     * @param id The UUID to set as the message identifier
     */
    public void setId(UUID id) {
        this.id = id;
    }
}