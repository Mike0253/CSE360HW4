package application;

import java.util.*;

/**
 * The Reviewer class represents a user with reviewing capabilities in the application.
 * It extends the User class and maintains a collection of reviews and private messages.
 */
public class Reviewer extends User {
    
    /**
     * A map storing reviews made by this reviewer, where keys are UUIDs of reviewed questions
     * and values are the review texts.
     */
    private HashMap<UUID, String> reviews;
    
    /**
     * A list of private messages associated with this reviewer.
     */
    private privateMessageList pm_list;

    /**
     * Constructs a new Reviewer instance with the specified username, password, and role bits.
     * Initializes the reviews map and private message list.
     *
     * @param userName the username of the reviewer
     * @param password the password of the reviewer
     * @param role_bits the role bits representing the reviewer's permissions
     */
    public Reviewer(String userName, String password, int role_bits) {
        super(userName, password, role_bits);
        reviews = new HashMap<UUID, String>();
        pm_list = new privateMessageList();
    }
    
    /**
     * Retrieves the reviewer's data from the database, including reviews and private messages.
     * 
     * @return true if the retrieval was successful, false otherwise
     */
    public boolean retrieveFromDatabase() {
        /// load in reviews and pms from the database.
        /// TODO:
        return true;
    }
    
    /**
     * Gets all reviews made by this reviewer.
     * 
     * @return a HashMap where keys are UUIDs of reviewed questions and values are review texts
     */
    public HashMap<UUID, String> getReviews() {
        return reviews;
    }
    
    /**
     * Adds or updates a review in the reviewer's collection of reviews.
     * This method should be used both for creating new reviews and editing existing ones.
     * 
     * @param review_uuid the UUID of the question being reviewed
     * @param review_text the text content of the review
     */
    public void addReview(UUID review_uuid, String review_text) {
        reviews.put(review_uuid, review_text);
    }
    
    /**
     * Deletes a review from the reviewer's collection.
     * 
     * @param review_uuid the UUID of the review to be removed
     */
    public void delReview(UUID review_uuid) {
        reviews.remove(review_uuid);
    }
    
    /**
     * Gets the private message list associated with this reviewer.
     * 
     * @return the privateMessageList containing all private messages for this reviewer
     */
    public privateMessageList getPMList() {
        return pm_list;
    }
}