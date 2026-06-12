/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package chatapplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
/**
 *
 * @author ndumi
 */
public class ChatApplication {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            login login = new login();
            
            // === REGISTRATION ===
            while (true) {
                System.out.println(" Let's get you started!");
                System.out.println("Please provide the details below to create your RMQuickChat account.");
                
                System.out.print(" Enter a username (must include '_' and be 5 characters or fewer): ");
                String username = sc.nextLine();
                if (username.isEmpty()) return;
                
                System.out.print(" Create a password (at least 8 characters, includes uppercase, number & symbol): ");
                String password = sc.nextLine();
                if (password.isEmpty()) return;
                
                System.out.print(" Enter your cellphone (with international code, e.g. +27838968976): ");
                String cellphone = sc.nextLine();
                if (cellphone.isEmpty()) return;
                
                String regMessage = login.registerUser(username.trim(), password, cellphone.trim());
                System.out.println(regMessage);
                
                if (regMessage.equals("Registration successful.")) break;
            }
            
            // === LOGIN ===
            boolean loggedIn = false;
            String loggedUsername = null;
            while (!loggedIn) {
                System.out.println("\n Login to continue");
                System.out.print(" Username: ");
                String username = sc.nextLine();
                if (username.isEmpty()) return;
                
                System.out.print(" Password: ");
                String password = sc.nextLine();
                if (password.isEmpty()) return;
                
                boolean ok = login.loginUser(username.trim(), password);
                String msg = login.returnLoginStatus(ok, username.trim());
                System.out.println(msg);
                
                if (ok) {
                    loggedIn = true;
                    loggedUsername = username.trim();
                }
            }
            
            System.out.println("\n Welcome to RMQuickChat, " + loggedUsername + "!");
            
            // === MESSAGE COUNT INPUT ===
            int messagesToEnter = 0;
            while (messagesToEnter <= 0) {
                System.out.print("\n How many messages would you like to send today? ");
                String nm = sc.nextLine();
                try {
                    messagesToEnter = Integer.parseInt(nm.trim());
                    if (messagesToEnter <= 0)
                        System.out.println(" Please enter a number greater than zero.");
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
            }
            

            List<Message> allMessages = new ArrayList<>();
        boolean quit = false;
        
        while (!quit) {
            System.out.println("\nWhat would you like to do next?");
            System.out.println("1.Send Messages");
            System.out.println("2.View recent Messages");
            System.out.println("3.Stored Messages");
            System.out.println("4.Exit");
            System.out.print("Enter choice: ");
            
            String choice = sc.nextLine();
            if (choice == null) return;
            
            switch (choice.trim()) {
                case "1" -> {
                    // Send new messages
                    if (allMessages.size() >= messagesToEnter) {
                        System.out.println("⚠ You've already sent your " + messagesToEnter + " message(s).");
                        break;
                    }
                    
                    int remaining = messagesToEnter - allMessages.size();
                    for (int i = 0; i < remaining; i++) {
                        System.out.print(String.format("\n📩 Enter recipient for message %d (include +27): ", allMessages.size() + 1));
                        String recipient = sc.nextLine();
                        if (recipient.isEmpty()) break;
                        
                        System.out.print("Type your message (max 250 characters): ");
                        String messageText = sc.nextLine();
                        if (messageText == null) break;
                        
                        // Validate message length
                        if (messageText.length() > 250) {
                            System.out.println(" Message too long! Keep it under 250 characters.");
                            i--;
                            continue;
                        }
                        
                        // Create and send message
                        Message m = new Message(allMessages.size() + 1, recipient.trim(), messageText);
                        String actionResult = m.sendMessage(); // This should set status to SENT
                        System.out.println("✓ " + actionResult);
                        
                        // Store to JSON file using org.json library
                        try {
                            Message.storeMessagesToJson(
                                    Collections.singletonList(m),
                                    System.getProperty("user.home") + "/stored_messages.json");
                            System.out.println(" Message saved to JSON at " +
                                    System.getProperty("user.home") + "/stored_messages.json");
                        } catch (IOException ex) {
                            System.out.println("✗ Failed to save message: " + ex.getMessage());
                        }
                        
                        allMessages.add(m);
                        System.out.println(m.printMessageDetails());
                    }
                    
                    int totalSent = Message.returnTotalMessages(allMessages);
                    System.out.println("\n? Total messages sent: " + totalSent);
                    }
                    
                case "2" -> {
                    // View recent messages
                    System.out.println("\n========== RECENT MESSAGES ==========");
                    if (allMessages.isEmpty()) {
                        System.out.println("No messages sent yet.");
                    } else {
                        System.out.println(Message.printMessages(allMessages));
                    }
                    System.out.println("=====================================");
                    }
                    
                case "3" -> {
                    // Stored Messages - Part 3 functionality
                    StoredMessageManager manager = new StoredMessageManager();
                    
                    // Populate arrays from existing messages
                    for (Message m : allMessages) {
                        if (null != m.getStatus()) switch (m.getStatus()) {
                            case SENT -> manager.addSentMessage(m.getMessageText());
                            case DISREGARDED -> manager.addDisregardedMessage(m.getMessageText());
                            case STORED -> manager.addStoredMessage(m.getMessageText());
                            default -> {
                            }
                        }
                        manager.addMessageHash(m.getMessageHash());
                        manager.addMessageId(m.getMessageId());
                    }
                    
                    // Also read from JSON file
                    manager.readStoredMessagesFromJson(
                            System.getProperty("user.home") + "/stored_messages.json");
                    
                    // Stored Messages Sub-menu
                    boolean storedQuit = false;
                    while (!storedQuit) {
                        System.out.println("\n----- STORED MESSAGES MENU -----");
                        System.out.println("a. Display sender and recipient of all stored messages");
                        System.out.println("b. Display the longest stored message");
                        System.out.println("c. Search for a message ID");
                        System.out.println("d. Search messages by recipient");
                        System.out.println("e. Delete a message by hash");
                        System.out.println("f. Display full report");
                        System.out.println("g. Back to main menu");
                        System.out.print("Enter choice: ");
                        
                        String storedChoice = sc.nextLine();
                        
                        switch (storedChoice.trim().toLowerCase()) {
                            case "a" -> System.out.println(manager.displayStoredMessagesSenderRecipient(loggedUsername, allMessages));
                            case "b" -> System.out.println(manager.displayLongestStoredMessage());
                            case "c" -> {
                                System.out.print("Enter Message ID to search: ");
                                String searchId = sc.nextLine();
                                System.out.println(manager.searchByMessageId(searchId, allMessages));
                            }
                            case "d" -> {
                                System.out.print("Enter recipient to search: ");
                                String searchRecipient = sc.nextLine();
                                System.out.println(manager.searchMessagesByRecipient(searchRecipient, allMessages));
                            }
                            case "e" -> {
                                System.out.print("Enter message hash to delete: ");
                                String deleteHash = sc.nextLine();
                                System.out.println(manager.deleteByMessageHash(deleteHash, allMessages));
                            }
                            case "f" -> System.out.println(manager.displayStoredMessagesReport(allMessages));
                            case "g" -> storedQuit = true;
                            default -> System.out.println("Invalid choice. Please select a-g.");
                        }
                    }
                    }
                    
                case "4" -> {
                    // Exit
                    quit = true;
                    System.out.println("\nThank you for using QuickChat!");
                    System.out.println("See you soon!");
                    }
                    
                default -> System.out.println("Please choose 1, 2, 3, or 4.");
            }
        }
        
        sc.close();
        }
    }
}