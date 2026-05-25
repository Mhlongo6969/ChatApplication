/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chatapplication;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 *
 * @author ndumi
 */
public class Message {
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
        return String.format("%010d", v);
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
            case 0:
                this.status = Status.SENT;
                return "Message successfully sent";
            case 1:
                this.status = Status.DISREGARDED;
                return "Press 0 to delete message.";
            case 2:
                this.status = Status.STORED;
                return "Message successfully stored.";
            default:
                this.status = Status.DISREGARDED;
                return "Action cancelled, message disregarded.";
            
        }
    }

    // Method: printMessageDetails - returns full details
    public String printMessageDetails() {
        return String.format("MessageID: %s\nMessage Hash: %s\nRecipient: %s\nMessage: %s",
                this.messageId, this.messageHash, this.recipient, this.messageText);
    }

    // Method: returnTotalMessages - returns count of messages whose status == SENT
    public static int returnTotalMessages(List<Message> list) {
        if (list == null) return 0;
        int count = 0;
        for (Message m : list) if (m.status == Status.SENT) count++;
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

    // Method: storeMessagesToJson - writes messages to a file as a JSON array
    public static void storeMessagesToJson(List<Message> list, String filepath) throws IOException {
        if (list == null) list = java.util.Collections.emptyList();
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < list.size(); i++) {
            Message m = list.get(i);
            sb.append(" {\n");
            sb.append(" \"messageId\": \"").append(m.messageId).append("\",\n");
            sb.append(" \"messageNumber\": ").append(m.messageNumber).append(",\n");
            sb.append(" \"recipient\": \"").append(escapeJson(m.recipient)).append("\",\n");
            sb.append(" \"messageText\": \"").append(escapeJson(m.messageText)).append("\",\n");
            sb.append(" \"messageHash\": \"").append(escapeJson(m.messageHash)).append("\",\n");
            sb.append(" \"status\": \"").append(m.status == null? "UNKNOWN" : m.status.name()).append("\"\n");
            sb.append(" }");
            if (i < list.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]\n");
        try (FileWriter fw = new FileWriter(filepath, false)) {
            fw.write(sb.toString());
        }
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    // Getters for tests
    public String getMessageId() { return messageId; }
    public int getMessageNumber() { return messageNumber; }
    public String getRecipient() { return recipient; }
    public String getMessageText() { return messageText; }
    public String getMessageHash() { return messageHash; }
    public Status getStatus() { return status; }
}
    

