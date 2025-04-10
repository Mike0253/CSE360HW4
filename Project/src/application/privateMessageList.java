package application;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The privateMessageList class manages a collection of privateMessage objects.
 * It provides methods for adding, retrieving, updating, and searching through messages.
 */
public class privateMessageList {
    private ArrayList<privateMessage> privateMessageArray;

    /**
     * Constructs an empty privateMessageList.
     */
    public privateMessageList() {
        this.privateMessageArray = new ArrayList<privateMessage>();
    }

    /**
     * Gets the underlying ArrayList of private messages.
     * @return The list of private messages
     */
    public ArrayList<privateMessage> getPrivateMessageArray() {
        return privateMessageArray;
    }

    /**
     * Replaces the current list of private messages with a new list.
     * @param privateMessageArray The new list of private messages
     */
    public void setPrivateMessageArray(ArrayList<privateMessage> privateMessageArray) {
        this.privateMessageArray = privateMessageArray;
    }

    /**
     * Adds a private message to the list.
     * @param PM The private message to add
     */
    public void addPrivateMessage(privateMessage PM) {
        this.privateMessageArray.add(PM);
    }

    /**
     * Retrieves a private message by its UUID.
     * @param ID The UUID of the message to find
     * @return The matching private message, or null if not found
     */
    public privateMessage getByUUID(UUID ID) {
        for (int i = 0; i < privateMessageArray.size(); i++) {
            if (privateMessageArray.get(i).getID().equals(ID)) {
                return privateMessageArray.get(i);
            }
        }
        return null;
    }

    /**
     * Gets the private message at the specified index.
     * @param i The index of the message to retrieve
     * @return The private message at the given index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public privateMessage get(int i) {
        return this.privateMessageArray.get(i);
    }

    /**
     * Removes the private message at the specified index.
     * @param i The index of the message to remove
     * @return true if the message was successfully removed
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public boolean removeByIndex(int i) {
        privateMessageArray.remove(i);
        return true;
    }

    /**
     * Gets the number of private messages in the list.
     * @return The size of the message list
     */
    public int getSize() {
        return privateMessageArray.size();
    }

    /**
     * Updates the text content of a private message identified by its UUID.
     * @param ID The UUID of the message to update
     * @param text The new text content
     */
    public void updateByUUID(UUID ID, String text) {
        for (int i = 0; i < privateMessageArray.size(); i++) {
            if (privateMessageArray.get(i).getID().equals(ID)) {
                privateMessageArray.get(i).setTextBody(text);
                break;
            }
        }
    }

    /**
     * Searches for private messages containing the specified text.
     * The search is case-insensitive.
     * 
     * @param toSearch The text to search for
     * @return List of UUIDs for messages containing the search text,
     *         or empty list if no matches found or search text is empty
     */
    public List<UUID> search(String toSearch) {
        List<UUID> indexes = new ArrayList<UUID>();
        if (toSearch == null || toSearch.trim().isEmpty()) {
            return indexes;
        }
        toSearch = toSearch.toLowerCase();
        for (int i = 0; i < privateMessageArray.size(); i++) {
            if (privateMessageArray.get(i).getTextBody().toLowerCase().contains(toSearch)) {
                indexes.add(privateMessageArray.get(i).getID());
            }
        }
        return indexes;
    }
}