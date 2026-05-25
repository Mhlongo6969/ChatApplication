/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package chatapplication;

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
                Scanner sc = new Scanner(System.in);
        login login = new login();

        // === REGISTRATION ===
        while (true) {
            System.out.println("📝 Let's get you started!");
            System.out.println("Please provide the details below to create your RMQuickChat account.");

            System.out.print("👤 Enter a username (must include '_' and be 5 characters or fewer): ");
            String username = sc.nextLine();
            if (username.isEmpty()) return;

            System.out.print("🔒 Create a password (at least 8 characters, includes uppercase, number & symbol): ");
            String password = sc.nextLine();
            if (password.isEmpty()) return;

            System.out.print("📱 Enter your cellphone (with international code, e.g. +27838968976): ");
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
            System.out.println("\n🔑 Login to continue");
            System.out.print("👤 Username: ");
            String username = sc.nextLine();
            if (username.isEmpty()) return;

            System.out.print("🔒 Password: ");
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

        System.out.println("\n🎉 Welcome to RMQuickChat, " + loggedUsername + "!");

        // === MESSAGE COUNT INPUT ===
        int messagesToEnter = 0;
        while (messagesToEnter <= 0) {
            System.out.print("\n✉️ How many messages would you like to send today? ");
            String nm = sc.nextLine();
            try {
                messagesToEnter = Integer.parseInt(nm.trim());
                if (messagesToEnter <= 0)
                    System.out.println("⚠️ Please enter a number greater than zero.");
            } catch (NumberFormatException e) {
                System.out.println("🚫 Please enter a valid number.");
            }
        }

        List<Message> allMessages = new ArrayList<>();
        boolean quit = false;

        while (!quit) {
            System.out.println("\n What would you like to do next?");
            System.out.println("1️⃣ Send new messages");
            System.out.println("2️⃣ View recent messages");
            System.out.println("3️⃣ Exit");
            System.out.print("Enter choice: ");
            String choice = sc.nextLine();
            if (choice == null) return;

            switch (choice.trim()) {
                case "1":
                    if (allMessages.size() >= messagesToEnter) {
                        System.out.println("🚫 You’ve already sent your " + messagesToEnter + " message(s).");
                        break;
                    }

                    int remaining = messagesToEnter - allMessages.size();
                    for (int i = 0; i < remaining; i++) {
                        System.out.print(String.format("📨 Enter recipient for message %d (include +27): ", i + 1));
                        String recipient = sc.nextLine();
                        if (recipient.isEmpty()) break;

                        System.out.print(" Type your message (max 250 characters): ");
                        String messageText = sc.nextLine();
                        if (messageText == null) break;

                        // Validate message length
                        if (messageText.length() > 250) {
                            System.out.println("⚠️ Message too long! Keep it under 250 characters.");
                            i--;
                            continue;
                        }

                        System.out.println("✅ Message sent successfully!");

                        // Create and process message
                        Message m = new Message(allMessages.size(), recipient.trim(), messageText);
 //                    String actionResult = m.sendMessage(); // changed from sendMessageViaDialog
 //                      System.out.println(actionResult);

                        if (m.getStatus() == Message.Status.STORED) {
                            try {
                                Message.storeMessagesToJson(
                                        Collections.singletonList(m),
                                        System.getProperty("user.home") + "/stored_messages.json");
                                System.out.println("💾 Message saved to JSON at " + 
                                    System.getProperty("user.home") + "/stored_messages.json");
                            } catch (Exception ex) {
                                System.out.println("❌ Failed to save message: " + ex.getMessage());
                            }
                        }

                        System.out.println(m.printMessageDetails());
                        allMessages.add(m);
                    }

                    int totalSent = Message.returnTotalMessages(allMessages);
                    System.out.println("? Total messages sent: " + totalSent);
                    break;

                case "2":
                    System.out.println("?Feature coming soon! You’ll be able to view your message history here.");
                    break;

                case "3":
                    quit = true;
                    break;

                default:
                    System.out.println("Please choose 1, 2, or 3.");
            }
        }

        System.out.println("\n Thank you for using QuickChat!");
        System.out.println("See you soon!");
        sc.close();
    }
}
        // TODO code application logic here
    

