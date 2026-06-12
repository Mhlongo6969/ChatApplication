package chatapplication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Part 3 - Store Data and Display Task Report
 * Manages arrays for sent, disregarded, stored messages, hashes, and IDs.
 * Provides search, delete, and report functionality.
 * 
 * @author ndumi
 */
public class StoredMessageManager {
    
    // Arrays to store message data (no hard-coding)
                            private final String[] sentMessages;
                        private final String[] disregardedMessages;
                    private final String[] storedMessages;
                private final String[] messageHashes;
            private final String[] messageIds;
    
    // Track actual counts (since arrays are fixed size after initialization)
                                private int sentCount = 0;
                            private int disregardedCount = 0;
                        private int storedCount = 0;
                    private int hashCount = 0;
                private int idCount = 0;
    
    // Constants for array sizes
    private static final int MAX_SIZE = 100;
    
    /**
     * Constructor - initializes all arrays
     */
    public StoredMessageManager() {
        this.sentMessages = new String[MAX_SIZE];
        this.disregardedMessages = new String[MAX_SIZE];
        this.storedMessages = new String[MAX_SIZE];
        this.messageHashes = new String[MAX_SIZE];
        this.messageIds = new String[MAX_SIZE];
    }
    
    // ==================== ARRAY POPULATION METHODS ====================
    
    /**
     * Add a message to the Sent Messages array
     * @param message
     */
    public void addSentMessage(String message) {
        if (sentCount < MAX_SIZE && message != null) {
            sentMessages[sentCount++] = message;
        }
    }
    
    /**
     * Add a message to the Disregarded Messages array
     * @param message
     */
    public void addDisregardedMessage(String message) {
        if (disregardedCount < MAX_SIZE && message != null) {
            disregardedMessages[disregardedCount++] = message;
        }
    }
    
    /**
     * Add a message to the Stored Messages array
     * @param message
     */
    public void addStoredMessage(String message) {
        if (storedCount < MAX_SIZE && message != null) {
            storedMessages[storedCount++] = message;
        }
    }
    
    /**
     * Add a message hash to the Message Hash array
     * @param hash
     */
    public void addMessageHash(String hash) {
        if (hashCount < MAX_SIZE && hash != null) {
            messageHashes[hashCount++] = hash;
        }
    }
    
    /**
     * Add a message ID to the Message ID array
     * @param id
     */
    public void addMessageId(String id) {
        if (idCount < MAX_SIZE && id != null) {
            messageIds[idCount++] = id;
        }
    }
    /**
 * Read stored messages from JSON file using org.json library
     * @param filepath
 */
public void readStoredMessagesFromJson(String filepath) {
    try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(filepath))) {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        
        JSONArray jsonArray = new JSONArray(sb.toString());
        
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            
            String messageText = obj.getString("messageText");
            String status = obj.optString("status", "UNKNOWN");
            
            if (null != status) // Populate arrays based on status
            switch (status) {
                case "SENT":
                    addSentMessage(messageText);
                    break;
                case "DISREGARDED":
                    addDisregardedMessage(messageText);
                    break;
                case "STORED":
                    addStoredMessage(messageText);
                    break;
                default:
                    break;
            }
            
            // Always populate hashes and IDs
            if (obj.has("messageHash")) {
                addMessageHash(obj.getString("messageHash"));
            }
            if (obj.has("messageId")) {
                addMessageId(obj.getString("messageId"));
            }
        }
        
    } catch (java.io.IOException e) {
        System.out.println("Error reading JSON file: " + e.getMessage());
    } catch (org.json.JSONException e) {
        System.out.println("Error parsing JSON: " + e.getMessage());
    }
}
    
 
    
    
    // ==================== MENU OPTION 4 METHODS ====================
    
    /**
     * 4a. Display sender and recipient of all stored messages
     * Returns formatted string with sender (logged user) and recipient
     * @param loggedInUser
     * @param allMessages
     * @return 
     */
    public String displayStoredMessagesSenderRecipient(String loggedInUser, List<Message> allMessages) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Stored Messages - Sender & Recipient ===\n");
        
        int storedIndex = 0;
        for (Message m : allMessages) {
            if (m.getStatus() == Message.Status.STORED && storedIndex < storedCount) {
                sb.append(String.format("Sender: %s | Recipient: %s\n", 
                    loggedInUser, m.getRecipient()));
                storedIndex++;
            }
        }
        
        if (storedIndex == 0) {
            sb.append("No stored messages found.\n");
        }
        
        return sb.toString();
    }
    
    /**
     * 4b. Display the longest stored message
     * Returns the longest message from stored messages array
     * @return 
     */
    public String displayLongestStoredMessage() {
        if (storedCount == 0) {
            return "No stored messages available.";
        }
        
        String longest = storedMessages[0];
        for (int i = 1; i < storedCount; i++) {
            if (storedMessages[i] != null && storedMessages[i].length() > longest.length()) {
                longest = storedMessages[i];
            }
        }
        
        return "Longest stored message: \"" + longest + "\"";
    }
    
    /**
     * 4c. Search for a message ID and display corresponding recipient and message
     * Returns formatted result or not found message
     * @param searchId
     * @param allMessages
     * @return 
     */
    public String searchByMessageId(String searchId, List<Message> allMessages) {
        for (Message m : allMessages) {
            if (m.getMessageId() != null && m.getMessageId().equals(searchId)) {
                return String.format("Message ID: %s\nRecipient: %s\nMessage: %s", 
                    searchId, m.getRecipient(), m.getMessageText());
            }
        }
        return "Message ID not found: " + searchId;
    }
    
    /**
     * 4d. Search for all messages stored for a particular recipient
     * Returns all messages (sent or stored) for the given recipient
     * @param recipient
     * @param allMessages
     * @return 
     */
    public String searchMessagesByRecipient(String recipient, List<Message> allMessages) {
        StringBuilder sb = new StringBuilder();
        sb.append("Messages for recipient: ").append(recipient).append("\n");
        
        boolean found = false;
        for (Message m : allMessages) {
            if (m.getRecipient() != null && m.getRecipient().equals(recipient)) {
                sb.append("- ").append(m.getMessageText()).append("\n");
                found = true;
            }
        }
        
        if (!found) {
            sb.append("No messages found for this recipient.\n");
        }
        
        return sb.toString();
    }
    
    /**
     * 4e. Delete a message using the message hash
     * Returns confirmation or failure message
     * @param hash
     * @param allMessages
     * @return 
     */
    public String deleteByMessageHash(String hash, List<Message> allMessages) {
        for (int i = 0; i < allMessages.size(); i++) {
            Message m = allMessages.get(i);
            if (m.getMessageHash() != null && m.getMessageHash().equals(hash)) {
                String deletedMsg = m.getMessageText();
                allMessages.remove(i);
                // Also remove from arrays
                removeFromArrays(hash);
                return String.format("Message: \"%s\" successfully deleted.", deletedMsg);
            }
        }
        return "Message with hash not found: " + hash;
    }
    
    /**
     * Helper to remove message from internal arrays when deleted
     */
    private void removeFromArrays(String hash) {
        // Remove from stored messages array if present
        for (int i = 0; i < storedCount; i++) {
            if (storedMessages[i] != null && storedMessages[i].contains(hash)) {
                // Shift remaining elements
                for (int j = i; j < storedCount - 1; j++) {
                    storedMessages[j] = storedMessages[j + 1];
                }
                storedMessages[--storedCount] = null;
                break;
            }
        }
        
        // Remove from hashes array
        for (int i = 0; i < hashCount; i++) {
            if (messageHashes[i] != null && messageHashes[i].equals(hash)) {
                for (int j = i; j < hashCount - 1; j++) {
                    messageHashes[j] = messageHashes[j + 1];
                }
                messageHashes[--hashCount] = null;
                break;
            }
        }
    }
    
    /**
     * 4f. Display a report listing full details of all stored messages
     * Returns comprehensive report with all message details
     * @param allMessages
     * @return 
     */
    public String displayStoredMessagesReport(List<Message> allMessages) {
        StringBuilder sb = new StringBuilder();
        sb.append("========== STORED MESSAGES REPORT ==========\n");
        sb.append(String.format("%-15s %-20s %-30s %-10s\n", 
            "Message ID", "Message Hash", "Recipient", "Message Text"));
        sb.append("------------------------------------------------------------------------\n");
        
        int count = 0;
        for (Message m : allMessages) {
            if (m.getStatus() == Message.Status.STORED) {
                sb.append(String.format("%-15s %-20s %-30s %-10s\n",
                    truncate(m.getMessageId(), 15),
                    truncate(m.getMessageHash(), 20),
                    truncate(m.getRecipient(), 30),
                    truncate(m.getMessageText(), 40)));
                count++;
            }
        }
        
        if (count == 0) {
            sb.append("No stored messages to display.\n");
        }
        
        sb.append("------------------------------------------------------------------------\n");
        sb.append("Total stored messages: ").append(count).append("\n");
        sb.append("==========================================\n");
        
        return sb.toString();
    }
    
    /**
     * Helper to truncate strings for display
     */
    private String truncate(String str, int length) {
        if (str == null) return "N/A";
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }
    
    // ==================== GETTERS FOR TESTS ====================
    
    public String[] getSentMessages() {
        return sentMessages;
    }
    
    public String[] getDisregardedMessages() {
        return disregardedMessages;
    }
    
    public String[] getStoredMessages() {
        return storedMessages;
    }
    
    public String[] getMessageHashes() {
        return messageHashes;
    }
    
    public String[] getMessageIds() {
        return messageIds;
    }
    
    public int getSentCount() {
        return sentCount;
    }
    
    public int getDisregardedCount() {
        return disregardedCount;
    }
    
    public int getStoredCount() {
        return storedCount;
    }
    
    public int getHashCount() {
        return hashCount;
    }
    
    public int getIdCount() {
        return idCount;
    }
}
 



