package application;

import java.time.LocalDate;
import java.util.UUID;
import java.io.*;

/**
 * The Post class serves as the base class for all content posts in the system.
 * It implements Serializable to support object serialization for storage and transmission.
 * 
 * <p>Contains common attributes and behaviors for:
 * <ul>
 *   <li>Questions</li>
 *   <li>Answers</li>
 *   <li>Other post types</li>
 * </ul>
 * </p>
 * 
 * <p>Key features:
 * <ul>
 *   <li>Author information (name and UUID)</li>
 *   <li>Content body</li>
 *   <li>Creation timestamp</li>
 *   <li>Unique identifier</li>
 *   <li>Review status flag</li>
 * </ul>
 * </p>
 */
public class Post implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String name;
    protected String textBody;
    protected LocalDate date;
    private UUID userUUID;
    protected UUID ID;
    protected Boolean underReview;

    /**
     * Default constructor for serialization purposes.
     */
    public Post() {
        // Intentionally left empty for serialization
    }

    /**
     * Constructs a new Post with specified author, content, and user UUID.
     * Automatically generates creation date and unique ID.
     * 
     * @param name The display name of the author (defaults to "Anonymous" if empty)
     * @param text The content body of the post
     * @param user The UUID of the user creating the post
     */
    public Post(String name, String text, UUID user) {
        if (name.strip().length() == 0) {
            this.name = "Anonymous";
        } else {
            this.name = name;
        }
        this.textBody = text;
        this.date = LocalDate.now();
        this.ID = UUID.randomUUID();
        this.userUUID = user;
        this.underReview = false;
    }

    /**
     * Marks the post as being under review.
     */
    public void isUnderReview() {
        underReview = true;
    }

    /**
     * Marks the post as not being under review.
     */
    public void notUnderReview() {
        underReview = false;
    }
    
    /**
     * Gets the current review status of the post.
     * @return true if the post is under review, false otherwise
     */
    public Boolean getUnderReview() {
        return underReview;
    }
    
    /**
     * Sets the review status of the post.
     * @param rev The review status to set (true for under review)
     */
    public void setUnderReview(Boolean rev) {
        underReview = rev;
    }

    /**
     * Gets the UUID of the user who created the post.
     * @return The author's UUID
     */
    public UUID getUserUUID() {
        return userUUID;
    }

    /**
     * Sets the UUID of the user who created the post.
     * @param useruuid The author's UUID to set
     */
    public void setUserUUID(UUID useruuid) {
        userUUID = useruuid;
    }

    /**
     * Gets the unique identifier of the post.
     * @return The post's UUID
     */
    public UUID getID() {
        return ID;
    }

    /**
     * Sets the unique identifier of the post.
     * Note: Use with caution as this affects post identification.
     * 
     * @param iD The UUID to set as the post identifier
     */
    public void setID(UUID iD) {
        ID = iD;
    }

    /**
     * Gets the display name of the post author.
     * @return The author's display name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the display name of the post author.
     * @param name The author name to display
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the content body of the post.
     * @return The post content text
     */
    public String getTextBody() {
        return textBody;
    }

    /**
     * Sets the content body of the post.
     * @param textBody The content text to set
     */
    public void setTextBody(String textBody) {
        this.textBody = textBody;
    }

    /**
     * Gets the creation date of the post.
     * @return The date when the post was created
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Sets the creation date of the post.
     * Note: Typically only used when reconstructing persisted posts.
     * 
     * @param newDate The date to set as the creation date
     */
    public void setDate(LocalDate newDate) {
        date = newDate;
    }
}