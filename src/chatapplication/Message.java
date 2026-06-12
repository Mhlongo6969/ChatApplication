/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chatapplication;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 *
 * @author ndumi
 */
public class Message {

/**
 * Send the message - sets status to SENT
     * @return 
 */

    public enum Status { SENT, DISREGARDED, STORED }

    private String messageId; // 10-digit string
    private int messageNumber; // 0-based index of the message
    private String recipient; // recipient phone string
    private String messageText; // message content
    private String messageHash; // auto-generated
    private Status status;

    // Constructor used by runtime (generates random messageId)
    public Message(int messageNumber, String recipient, String messageText) {
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageId = generateMessageId();
        this.messageHash = createMessageHash();
        this.status = null;
    }

    // Constructor used in tests where we want a deterministic messageId
    public Message(String messageId, int messageNumber, String recipient, String messageText) {
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageId = messageId;
        this.messageHash = createMessageHash();
        this.status = null;
    }

    // Generate a 10-digit message ID (leading zeros allowed)
    public static String generateMessageId() {
        Random rnd = new Random();
        long v = Math.abs(rnd.nextLong()) % 1_000_000_0000L; // up to 10 digits
        return String.format("%010d"
                , v);
    }

    // Method: checkMessageID - ensures it's exactly 10 digits
    public boolean checkMessageID() {
        return this.messageId!= null && this.messageId.matches("\\d{10}");
    }

    // Method: checkRecipientCell - we reuse the SA international format: +27 followed by 9 digits
    public boolean checkRecipientCell() {
        if (this.recipient == null) return false;
        return this.recipient.matches("^\\+27\\d{9}$");
    }

    // Method: createMessageHash - first two digits of messageId : messageNumber : first+last words
    public String createMessageHash() {
        String firstTwo = this.messageId!= null && this.messageId.length() >= 2? this.messageId.substring(0,2) : "00";
        String combined = "";
        if (this.messageText!= null &&!this.messageText.trim().isEmpty()) {
            String[] parts = this.messageText.trim().split("\\s+");
            String first = parts.length > 0? parts[0].replaceAll("[^A-Za-z0-9]", "") : "";
            String last = parts.length > 0? parts[parts.length - 1].replaceAll("[^A-Za-z0-9]", "") : "";
            combined = (first + last).toUpperCase();
        }
        return String.format("%s:%d:%s", firstTwo, this.messageNumber, combined);
    }

    // Method: validateMessageLength
    public static String validateMessageLength(String text) {
        if (text == null) text = "";
        int len = text.length();
        if (len <= 250) return "Message ready to send.";
        int excess = len - 250;
        return String.format("Message exceeds 250 characters by %d, please reduce size.", excess);
    }

    // Method: performAction(int) - non-GUI action that is testable
    // 0 send, 1 disregard, 2 store
    public String performAction(int actionCode) {
        switch (actionCode) {
            case 0 -> {
                this.status = Status.SENT;
                return "Message successfully sent";
            }
            case 1 -> {
                this.status = Status.DISREGARDED;
                return "Press 0 to delete message.";
            }
            case 2 -> {
                this.status = Status.STORED;
                return "Message successfully stored.";
            }
            default -> {
                this.status = Status.DISREGARDED;
                return "Action cancelled, message disregarded.";
            }
            
        }
    }

    // Method: printMessageDetails - returns full details
    public String printMessageDetails() {
        return String.format("MessageID: %s\nMessage Hash: %s\nRecipient: %s\nMessage: %s",
                this.messageId, this.messageHash, this.recipient, this.messageText);
    }
    /**
 * Returns count of messages whose status == SENT
     * @param list
     * @return 
 */
public static int returnTotalMessages(List<Message> list) {
    if (list == null) return 0;
    
    int count = 0;
    for (Message m : list) {
        if (m.getStatus() == Status.SENT) {
            count++;
        }
    }
    return count;
}
   

    // Method: printMessages - returns a concatenated string of all messages
    public static String printMessages(List<Message> list) {
        if (list == null || list.isEmpty()) return "No messages.";
        StringBuilder sb = new StringBuilder();
        for (Message m : list) {
            sb.append(m.printMessageDetails());
            sb.append("\n-----------------\n");
        }
        return sb.toString();
    }
    /**
 * Send the message - sets status to SENT
     * @return 
 */
public String sendMessage(){
    this.status = Status.SENT;
    return "Message sent successfully!";
}

/**
 * Store messages to JSON file using org.json library
     * @param list
     * @param filepath
     * @throws java.io.IOException
 */
public static void storeMessagesToJson(List<Message> list, String filepath) throws IOException {
    if (list == null) {
        list = java.util.Collections.emptyList();
    }
    
    JSONArray jsonArray = new JSONArray();
    
    for (Message m : list) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("messageId", m.getMessageId());
        jsonObject.put("messageNumber", m.getMessageNumber());
        jsonObject.put("recipient", m.getRecipient());
        jsonObject.put("messageText", m.getMessageText());
        jsonObject.put("messageHash", m.getMessageHash());
        jsonObject.put("status", m.getStatus() != null ? m.getStatus().name() : "UNKNOWN");
        
        jsonArray.put(jsonObject);
    }
    
    try (FileWriter file = new FileWriter(filepath, false)) {
        file.write(jsonArray.toString(4)); // Pretty print with 4-space indent
        file.flush();
    }
}
    // Getters for tests
    public String getMessageId() { return messageId; }
    public int getMessageNumber() { return messageNumber; }
    public String getRecipient() { return recipient; }
    public String getMessageText() { return messageText; }
    public String getMessageHash() { return messageHash; }
    public Status getStatus() { return status; }
}
    

