package application;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.io.*;

/**
 * Represents a collection of Answer objects in the application.
 * Implements Serializable for object serialization and provides various operations
 * for managing and manipulating answers.
 */
public class Answers implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<Answer> answerArray;

    /**
     * Default constructor that initializes an empty list of answers.
     */
    public Answers() {
        this.answerArray = new ArrayList<Answer>();
    }

    /**
     * Gets the list of all answers.
     *
     * @return ArrayList containing all Answer objects
     */
    public ArrayList<Answer> getAnswerArray() {
        return answerArray;
    }

    /**
     * Adds an answer to the collection.
     *
     * @param answer The Answer object to add
     */
    public void addAnswer(Answer answer) {
        this.answerArray.add(answer);
    }

    /**
     * Retrieves all answers associated with a specific question UUID.
     *
     * @param questionUUID The UUID of the question
     * @return ArrayList of Answer objects matching the question UUID
     */
    public ArrayList<Answer> getAnswersByUUID(UUID questionUUID) {
        ArrayList<Answer> returnList = new ArrayList<>();
        for (int i = 0; i < answerArray.size(); i++) {
            if (answerArray.get(i).getQuestionID().equals(questionUUID)) {
                returnList.add(answerArray.get(i));
            }
        }
        return returnList;
    }

    /**
     * Retrieves an answer by its UUID.
     *
     * @param ID The UUID of the answer to find
     * @return The matching Answer object, or null if not found
     */
    public Answer getByUUID(UUID ID) {
        for (int i = 0; i < answerArray.size(); i++) {
            if (answerArray.get(i).getID().equals(ID)) {
                Answer workingAnswer = answerArray.get(i);
                return workingAnswer;
            }
        }
        return null;
    }

    /**
     * Removes an answer from the collection by its UUID.
     *
     * @param ID The UUID of the answer to remove
     * @return true if the answer was found and removed, false otherwise
     */
    public boolean removeByUUID(UUID ID) {
        for (int i = 0; i < answerArray.size(); i++) {
            if (answerArray.get(i).getID().equals(ID)) {
                answerArray.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves an answer by its index in the collection.
     *
     * @param i The index of the answer to retrieve
     * @return The Answer object at the specified index
     */
    public Answer get(int i) {
        return this.answerArray.get(i);
    }

    /**
     * Removes an answer from the collection by its index.
     *
     * @param i The index of the answer to remove
     * @return true if the removal was successful
     */
    public boolean removeByIndex(int i) {
        answerArray.remove(i);
        return true;
    }

    /**
     * Gets the number of answers in the collection.
     *
     * @return The size of the answer collection
     */
    public int getSize() {
        return answerArray.size();
    }

    /**
     * Updates the text of an answer at a specific index.
     *
     * @param i The index of the answer to update
     * @param text The new text content for the answer
     */
    public void update(int i, String text) {
        answerArray.get(i).setTextBody(text);
    }

    /**
     * Updates the text of an answer identified by its UUID.
     *
     * @param ID The UUID of the answer to update
     * @param text The new text content for the answer
     */
    public void updateByUUID(UUID ID, String text) {
        for (int i = 0; i < answerArray.size(); i++) {
            if (answerArray.get(i).getID().equals(ID)) {
                answerArray.get(i).setTextBody(text);
            }
        }
    }

    /**
     * Increases the reputation of an answer's author and updates all answers by the same author.
     *
     * @param databaseHelper The database helper to update reputation
     * @param answerId The UUID of the answer being upvoted
     * @param voterId The UUID of the user who is voting
     * @return The new reputation value, or -1 if the answer wasn't found
     */
    public int increaseReputation(DatabaseHelper databaseHelper, UUID answerId, UUID voterId) {
        Answer targetAnswer = getByUUID(answerId);
        if (targetAnswer != null) {
            int newReputation = targetAnswer.increaseReputation(databaseHelper, voterId);
            UUID targetUserUUID = targetAnswer.getUserUUID();
            // Update reputation for all answers by the same user in this list
            for (Answer answer : answerArray) {
                if (answer.getUserUUID().equals(targetUserUUID)) {
                    answer.setRepuation(newReputation);
                }
            }
            return newReputation;
        } else {
            System.err.println("Answer with ID " + answerId + " not found for increasing reputation.");
        }
        return -1;
    }

    /**
     * Decreases the reputation of an answer's author and updates all answers by the same author.
     *
     * @param databaseHelper The database helper to update reputation
     * @param answerId The UUID of the answer being downvoted
     * @param voterId The UUID of the user who is voting
     * @return The new reputation value, or -1 if the answer wasn't found
     */
    public int decreaseReputation(DatabaseHelper databaseHelper, UUID answerId, UUID voterId) {
        Answer targetAnswer = getByUUID(answerId);
        if (targetAnswer != null) {
            int newReputation = targetAnswer.decreaseReputation(databaseHelper, voterId);
            UUID targetUserUUID = targetAnswer.getUserUUID();
            // Update reputation for all answers by the same user in this list
            for (Answer answer : answerArray) {
                if (answer.getUserUUID().equals(targetUserUUID)) {
                    answer.setRepuation(newReputation);
                }
            }
            return newReputation;
        } else {
            System.err.println("Answer with ID " + answerId + " not found for decreasing reputation.");
        }
        return -1;
    }

    /**
     * Searches for answers containing the specified text.
     *
     * @param toSearch The text to search for (case-insensitive)
     * @return List of UUIDs of matching answers, or empty list if no matches found
     */
    public List<UUID> search(String toSearch) {
        List<UUID> indexs = new ArrayList<UUID>();
        if (toSearch == null || toSearch.trim().isEmpty()) {
            return indexs;
        }
        toSearch = toSearch.toLowerCase();
        for (int i = 0; i < answerArray.size(); i++) {
            if (answerArray.get(i).getTextBody().toLowerCase().contains(toSearch)) {
                indexs.add(answerArray.get(i).getID());
            }
        }
        return indexs;
    }

    // ===============================================================================
    // Save and Load
    // ===============================================================================

    /**
     * Saves the Answers collection to a file.
     *
     * @param filename The name of the file to save to
     * @return true if the save was successful, false otherwise
     */
    public boolean saveAnswers(String filename) {
        try (FileOutputStream filePath = new FileOutputStream(filename);
                ObjectOutputStream objectToSave = new ObjectOutputStream(filePath)) {
            objectToSave.writeObject(this);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Loads an Answers collection from a file.
     *
     * @param filename The name of the file to load from
     * @return The loaded Answers object, or a new empty Answers object if loading fails
     */
    public static Answers loadAnswers(String filename) {
        Answers questionArray = null;
        try (FileInputStream fileIn = new FileInputStream(filename);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            questionArray = (Answers) objectIn.readObject();
            System.out.println("The questions object has been loaded from " + filename);
        } catch (FileNotFoundException e) {
            return new Answers();
        } catch (IOException e) {
            e.printStackTrace();
            return new Answers();
        } catch (ClassNotFoundException e) {
            return new Answers();
        }
        return questionArray;
    }
}