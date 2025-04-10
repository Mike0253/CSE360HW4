package application;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.io.*;

/**
 * The Questions class manages a collection of Question objects and their associated UUIDs.
 * It provides functionality for storing, retrieving, and searching questions,
 * as well as serialization support for persistence.
 */
public class Questions implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /** List containing all Question objects */
    private ArrayList<Question> questionArray;
    
    /** Parallel list containing UUIDs of all questions for quick lookup */
    private ArrayList<UUID> UUIDArray;

    /**
     * Constructs an empty Questions collection.
     */
    public Questions() {
        this.questionArray = new ArrayList<Question>();
        this.UUIDArray = new ArrayList<UUID>();
    }

    /**
     * Gets the list of all questions.
     * @return ArrayList containing all Question objects
     */
    public ArrayList<Question> getQuestionArray() {
        return questionArray;
    }

    /**
     * Gets the list of all question UUIDs.
     * @return ArrayList containing all question UUIDs
     */
    public ArrayList<UUID> getUUIDArray() {
        return UUIDArray;
    }

    /**
     * Adds a question to the collection.
     * @param question The Question object to add
     */
    public void addQuestion(Question question) {
        this.UUIDArray.add(question.getID());
        this.questionArray.add(question);
    }

    /**
     * Retrieves a question by its UUID.
     * @param ID The UUID of the question to find
     * @return The matching Question object, or null if not found
     */
    public Question getByUUID(UUID ID) {
        for (int i = 0; i < UUIDArray.size(); i++) {
            if (UUIDArray.get(i).equals(ID)) {
                return questionArray.get(i);
            }
        }
        return null;
    }

    /**
     * Removes a question by its UUID.
     * @param ID The UUID of the question to remove
     * @return true if the question was found and removed, false otherwise
     */
    public boolean removeByUUID(UUID ID) {
        for (int i = 0; i < UUIDArray.size(); i++) {
            if (UUIDArray.get(i).equals(ID)) {
                UUIDArray.remove(i);
                questionArray.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the question at the specified index.
     * @param i The index of the question to retrieve
     * @return The Question object at the given index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public Question get(int i) {
        return this.questionArray.get(i);
    }

    /**
     * Removes the question at the specified index.
     * @param i The index of the question to remove
     * @return true if the question was successfully removed
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public boolean removeByIndex(int i) {
        UUIDArray.remove(i);
        questionArray.remove(i);
        return true;
    }

    /**
     * Gets the number of questions in the collection.
     * @return The size of the question collection
     */
    public int getSize() {
        return questionArray.size();
    }

    /**
     * Gets the UUID of the question at the specified index.
     * @param i The index of the UUID to retrieve
     * @return The UUID at the given index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public UUID getUUIDbyIndex(int i) {
        return UUIDArray.get(i);
    }

    /**
     * Updates the text content of the question at the specified index.
     * @param i The index of the question to update
     * @param text The new text content
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public void update(int i, String text) {
        questionArray.get(i).setTextBody(text);
    }

    /**
     * Searches for questions containing the specified text in either title or body.
     * The search is case-insensitive.
     * 
     * @param toSearch The text to search for
     * @return List of UUIDs for questions containing the search text,
     *         or empty list if no matches found or search text is empty
     */
    public List<UUID> search(String toSearch) {
        List<UUID> indexes = new ArrayList<UUID>();
        if (toSearch == null || toSearch.trim().isEmpty()) {
            return indexes;
        }
        toSearch = toSearch.toLowerCase();
        for (int i = 0; i < questionArray.size(); i++) {
            if (questionArray.get(i).getTitle().toLowerCase().contains(toSearch)
                    || questionArray.get(i).getTextBody().toLowerCase().contains(toSearch)) {
                indexes.add(questionArray.get(i).getID());
            }
        }
        return indexes;
    }

    // ===============================================================================
    // Save and Load Methods
    // ===============================================================================

    /**
     * Saves the question collection to a file.
     * @param filename The name of the file to save to
     * @return true if the save was successful, false otherwise
     */
    public boolean saveQuestions(String filename) {
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
     * Loads a question collection from a file.
     * @param filename The name of the file to load from
     * @return The loaded Questions object, or a new empty collection if loading fails
     */
    public static Questions loadQuestions(String filename) {
        Questions questionArray = null;
        try (FileInputStream fileIn = new FileInputStream(filename);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            questionArray = (Questions) objectIn.readObject();
            System.out.println("The questions object has been loaded from " + filename);
        } catch (FileNotFoundException e) {
            return new Questions();
        } catch (IOException e) {
            e.printStackTrace();
            return new Questions();
        } catch (ClassNotFoundException e) {
            return new Questions();
        }
        return questionArray;
    }
}