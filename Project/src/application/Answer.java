package application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.String;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents an answer to a question in the application.
 * Extends the Post class and implements Serializable for object serialization.
 */
public class Answer extends Post implements Serializable {
    private static final long serialVersionUID = 1L;
    protected UUID questionID;
    protected int repuation;
    private List<UUID> upvotedBy = new ArrayList<>();
    private List<UUID> downvotedBy = new ArrayList<>();

    /**
     * Default constructor for Answer.
     * Initializes the upvotedBy and downvotedBy lists.
     */
    public Answer() {
        this.upvotedBy = new ArrayList<>();
        this.downvotedBy = new ArrayList<>();
    }

    /**
     * Gets the reputation of the answer's author.
     *
     * @param databaseHelper The database helper to retrieve reputation data
     * @return The reputation value of the author
     */
    public int getRepuation(DatabaseHelper databaseHelper) {
        repuation = databaseHelper.getReputation(this.getUserUUID());
        return repuation;
    }

    /**
     * Sets the reputation of the answer's author.
     *
     * @param repuation The reputation value to set
     */
    public void setRepuation(int repuation) {
        this.repuation = repuation;
    }

    /**
     * Increases the reputation of the answer's author by 1.
     *
     * @param databaseHelper The database helper to update reputation
     * @return The updated reputation value
     */
    public int increaseReputation(DatabaseHelper databaseHelper) {
        this.repuation = databaseHelper.updateReputation(this.getUserUUID(), 1);
        return this.repuation;
    }

    /**
     * Decreases the reputation of the answer's author by 1.
     *
     * @param databaseHelper The database helper to update reputation
     * @return The updated reputation value
     */
    public int decreaseReputation(DatabaseHelper databaseHelper) {
        this.repuation = databaseHelper.updateReputation(this.getUserUUID(), -1);
        return this.repuation;
    }

    /**
     * Constructs an Answer with specified parameters.
     *
     * @param name The name/title of the answer
     * @param text The content/body of the answer
     * @param inID The UUID of the question this answer belongs to
     * @param user The UUID of the user who created this answer
     */
    public Answer(String name, String text, UUID inID, UUID user) {
        super(name, text, user);
        questionID = inID;
        this.underReview = true;
    }

    /**
     * Gets the UUID of the question this answer belongs to.
     *
     * @return The question's UUID
     */
    public UUID getQuestionID() {
        return questionID;
    }

    /**
     * Sets the UUID of the question this answer belongs to.
     *
     * @param questionID The question's UUID to set
     */
    public void setQuestionID(UUID questionID) {
        this.questionID = questionID;
    }
    
    /**
     * Gets the list of user UUIDs who upvoted this answer.
     *
     * @return List of user UUIDs who upvoted
     */
    public List<UUID> getUpvotedBy() { return upvotedBy; }
    
    /**
     * Sets the list of user UUIDs who upvoted this answer.
     *
     * @param upvotedBy List of user UUIDs who upvoted
     */
    public void setUpvotedBy(List<UUID> upvotedBy) { this.upvotedBy = upvotedBy; }
    
    /**
     * Gets the list of user UUIDs who downvoted this answer.
     *
     * @return List of user UUIDs who downvoted
     */
    public List<UUID> getDownvotedBy() { return downvotedBy; }
    
    /**
     * Sets the list of user UUIDs who downvoted this answer.
     *
     * @param downvotedBy List of user UUIDs who downvoted
     */
    public void setDownvotedBy(List<UUID> downvotedBy) { this.downvotedBy = downvotedBy; }

    /**
     * Increases the reputation of the answer's author by 1 if the voter hasn't already upvoted.
     * Removes downvote if the voter had previously downvoted.
     *
     * @param databaseHelper The database helper to update reputation
     * @param voterId The UUID of the user who is voting
     * @return The updated reputation value
     */
    public int increaseReputation(DatabaseHelper databaseHelper, UUID voterId) {
        if (upvotedBy.contains(voterId)) {
            return this.repuation;
        }
        if (downvotedBy.contains(voterId)) {
            downvotedBy.remove(voterId);
        }
        upvotedBy.add(voterId);
        System.out.println(this.getUserUUID());
        this.repuation = databaseHelper.updateReputation(this.getUserUUID(), 1);
        return this.repuation;
    }

    /**
     * Decreases the reputation of the answer's author by 1 if the voter hasn't already downvoted.
     * Removes upvote if the voter had previously upvoted.
     *
     * @param databaseHelper The database helper to update reputation
     * @param voterId The UUID of the user who is voting
     * @return The updated reputation value
     */
    public int decreaseReputation(DatabaseHelper databaseHelper, UUID voterId) {
        if (downvotedBy.contains(voterId)) {
            return this.repuation;
        }
        if (upvotedBy.contains(voterId)) {
            upvotedBy.remove(voterId);
        }
        downvotedBy.add(voterId);
        this.repuation = databaseHelper.updateReputation(this.getUserUUID(), -1);
        return this.repuation;
    }
}