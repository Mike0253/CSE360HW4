package application;

import java.lang.String;
import java.time.LocalDate;
import java.util.UUID;
import java.io.Serializable;

/**
 * The Question class represents a question post in the system, extending the base Post class.
 * It includes additional question-specific attributes like title and chosen answer.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Inherits all basic post properties from Post class</li>
 *   <li>Maintains a question title</li>
 *   <li>Tracks the chosen best answer</li>
 *   <li>Supports serialization for storage/transmission</li>
 * </ul>
 * </p>
 */
public class Question extends Post implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private UUID chosenAnswer;

    /**
     * Default constructor for serialization purposes.
     */
    public Question() {
        // Intentionally left empty for serialization
    }

    /**
     * Constructs a new Question with specified author, title, content, and user UUID.
     * 
     * @param name  The display name of the author (defaults to "Anonymous" if empty via Post class)
     * @param title The title/subject of the question
     * @param text  The content body of the question
     * @param user  The UUID of the user creating the question
     */
    public Question(String name, String title, String text, UUID user) {
        super(name, text, user);
        this.title = title;
    }

    /**
     * Gets the title of the question.
     * @return The question title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the question.
     * @param title The new title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the UUID of the chosen best answer for this question.
     * @return The UUID of the chosen answer, or null if none selected
     */
    public UUID getChosenAnswer() {
        return chosenAnswer;
    }

    /**
     * Sets the chosen best answer for this question.
     * @param best_answer The UUID of the answer to mark as best
     */
    public void setChosenAnswer(UUID best_answer) {
        chosenAnswer = best_answer;
    }
}