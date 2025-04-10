package application;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.io.Serializable;

/**
 * The Review class represents a review of either a question or answer in the system.
 * It tracks the review status, content, and associated messages between reviewers and authors.
 */
public class Review implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /** Unique identifier for the review */
    private UUID id;
    
    /** UUID of the reviewer */
    private UUID reviewerId;
    
    /** UUID of the question being reviewed (optional) */
    private UUID questionId;
    
    /** UUID of the answer being reviewed (optional) */
    private UUID answerId;
    
    /** Content of the review */
    private String content;
    
    /** Current status of the review */
    private ReviewStatus status;
    
    /** Date when the review was created */
    private LocalDate date;
    
    /** List of messages associated with this review */
    private List<Message> messages = new ArrayList<>();

    /**
     * Constructs a new Review with the specified parameters.
     * Automatically generates an ID and sets creation date.
     * 
     * @param reviewerId UUID of the user performing the review
     * @param questionId UUID of the question being reviewed (can be null)
     * @param answerId UUID of the answer being reviewed (can be null)
     * @param content The review content text
     */
    public Review(UUID reviewerId, UUID questionId, UUID answerId, String content) {
        this.id = UUID.randomUUID();
        this.reviewerId = reviewerId;
        this.questionId = questionId;
        this.answerId = answerId;
        this.content = content;
        this.status = ReviewStatus.PENDING;
        this.date = LocalDate.now();
    }

    /**
     * Enum representing possible review statuses.
     */
    public enum ReviewStatus { 
        /** Review is awaiting action */
        PENDING, 
        /** Review has been approved */
        APPROVED, 
        /** Review has been rejected */
        REJECTED 
    }

    /**
     * Gets the unique identifier of this review.
     * @return The review UUID
     */
    public UUID getId() { return id; }

    /**
     * Sets the unique identifier of this review.
     * @param id The UUID to set
     */
    public void setId(UUID id) { this.id = id; }

    /**
     * Gets the UUID of the reviewer.
     * @return The reviewer's UUID
     */
    public UUID getReviewerId() { return reviewerId; }

    /**
     * Gets the UUID of the question being reviewed.
     * @return The question UUID (may be null)
     */
    public UUID getQuestionId() { return questionId; }

    /**
     * Gets the UUID of the answer being reviewed.
     * @return The answer UUID (may be null)
     */
    public UUID getAnswerId() { return answerId; }

    /**
     * Gets the content of the review.
     * @return The review text
     */
    public String getContent() { return content; }

    /**
     * Sets the content of the review.
     * @param content The new review text
     */
    public void setContent(String content) { this.content = content; }

    /**
     * Gets the current status of the review.
     * @return The review status
     */
    public ReviewStatus getStatus() { return status; }

    /**
     * Sets the status of the review.
     * @param status The new status to set
     */
    public void setStatus(ReviewStatus status) { this.status = status; }

    /**
     * Gets the creation date of the review.
     * @return The review date
     */
    public LocalDate getDate() { return date; }

    /**
     * Gets all messages associated with this review.
     * @return List of Message objects
     */
    public List<Message> getMessages() { return messages; }

    /**
     * Adds a message to this review's conversation.
     * @param message The Message to add
     */
    public void addMessage(Message message) { messages.add(message); }

    /**
     * Sets the creation date of the review.
     * @param date The date to set
     */
    public void setDate(LocalDate date) { this.date = date; }
}