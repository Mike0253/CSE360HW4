package application;

import java.time.LocalDate;
import java.util.UUID;

/**
 * The privateMessage class represents a private communication between two users,
 * optionally associated with a specific review. Messages contain metadata about
 * the participants, content, and timing of the communication.
 */
public class privateMessage {
    /** Unique identifier for the message */
    public UUID uuid;
    /** Recipient's user UUID */
    public UUID toUUID;
    /** Sender's user UUID */
    public UUID fromUUID;
    /** Associated review UUID (optional) */
    public UUID reviewUUID;
    /** Content of the message */
    public String textBody;
    /** Date when the message was sent */
    public LocalDate date;

    /**
     * Constructs a privateMessage without review association.
     *
     * @param uuid     Unique identifier for the message
     * @param textBody Content of the message
     * @param date     Date when the message was sent
     * @param fromUUID Sender's user UUID
     * @param toUUID   Recipient's user UUID
     */
    public privateMessage(UUID uuid, String textBody, LocalDate date, UUID fromUUID, UUID toUUID) {
        this.uuid = uuid;
        this.textBody = textBody;
        this.date = date;
        this.fromUUID = fromUUID;
        this.toUUID = toUUID;
    }

    /**
     * Constructs a new privateMessage with review association.
     * Automatically generates UUID and sets current date.
     *
     * @param textBody  Content of the message
     * @param fromUUID  Sender's user UUID
     * @param toUUID    Recipient's user UUID
     * @param reviewId  Associated review UUID
     */
    public privateMessage(String textBody, UUID fromUUID, UUID toUUID, UUID reviewId) {
        this.uuid = UUID.randomUUID();
        this.textBody = textBody;
        this.date = LocalDate.now();
        this.fromUUID = fromUUID;
        this.toUUID = toUUID;
        this.reviewUUID = reviewId;
    }

    /**
     * Constructs a privateMessage with all fields specified.
     *
     * @param uuid      Unique identifier for the message
     * @param textBody  Content of the message
     * @param date      Date when the message was sent
     * @param fromUUID  Sender's user UUID
     * @param toUUID    Recipient's user UUID
     * @param reviewId  Associated review UUID
     */
    public privateMessage(UUID uuid, String textBody, LocalDate date, UUID fromUUID, UUID toUUID, UUID reviewId) {
        this.uuid = uuid;
        this.textBody = textBody;
        this.date = date;
        this.fromUUID = fromUUID;
        this.toUUID = toUUID;
        this.reviewUUID = reviewId;
    }

    /**
     * Gets the unique identifier of the message.
     * @return The message UUID
     */
    public UUID getID() {
        return uuid;
    }

    /**
     * Sets the unique identifier of the message.
     * @param uuid The UUID to set
     */
    public void setID(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Gets the recipient's user UUID.
     * @return The recipient's UUID
     */
    public UUID getToUUID() {
        return toUUID;
    }

    /**
     * Sets the recipient's user UUID.
     * @param toUUID The recipient's UUID to set
     */
    public void setToUUID(UUID toUUID) {
        this.toUUID = toUUID;
    }

    /**
     * Gets the sender's user UUID.
     * @return The sender's UUID
     */
    public UUID getFromUUID() {
        return fromUUID;
    }

    /**
     * Sets the sender's user UUID.
     * @param fromUUID The sender's UUID to set
     */
    public void setFromUUID(UUID fromUUID) {
        this.fromUUID = fromUUID;
    }

    /**
     * Gets the associated review UUID.
     * @return The review UUID (may be null)
     */
    public UUID getReviewUUID() {
        return reviewUUID;
    }

    /**
     * Sets the associated review UUID.
     * @param reviewUUID The review UUID to associate
     */
    public void setReviewUUID(UUID reviewUUID) {
        this.reviewUUID = reviewUUID;
    }

    /**
     * Gets the content of the message.
     * @return The message text
     */
    public String getTextBody() {
        return textBody;
    }

    /**
     * Sets the content of the message.
     * @param textBody The message text to set
     */
    public void setTextBody(String textBody) {
        this.textBody = textBody;
    }

    /**
     * Gets the date when the message was sent.
     * @return The message date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Sets the date when the message was sent.
     * @param date The date to set
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }
}