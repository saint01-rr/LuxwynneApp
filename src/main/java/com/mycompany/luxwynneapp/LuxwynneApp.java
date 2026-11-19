package com.mycompany.luxwynneapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main application runner for user authentication
 * and the QuickChat messaging system.
 * @author Student
 */
public class LuxwynneApp {

    // Holds every array the brief asks for. All of them are filled at runtime, nothing is hard-coded.
    static class Session {
        List<Message> sentMessages = new ArrayList<>();
        List<Message> disregardedMessages = new ArrayList<>();
        List<Message> storedMessages = new ArrayList<>();
        List<String> messageHashes = new ArrayList<>();
        List<String> messageIDs = new ArrayList<>();
        int messageCounter = 0;
    }

    public static void main(String[] args) {
        Scanner inputScanner = new Scanner(System.in);
        Login userAuth = new Login();

        System.out.println("=== SYSTEM REGISTRATION ===");
        System.out.print("Enter First Name: ");
        String fName = inputScanner.nextLine();
        System.out.print("Enter Last Name: ");
        String lName = inputScanner.nextLine();
        System.out.print("Enter Username (must contain '_' and <= 5 chars): ");
        String uName = inputScanner.nextLine();
        System.out.print("Enter Password (>= 8 chars, 1 capital, 1 number, 1 special): ");
        String pWord = inputScanner.nextLine();
        System.out.print("Enter Cell Number (e.g., +27...): ");
        String cellNum = inputScanner.nextLine();

        String registrationMsg = userAuth.registerUser(uName, pWord, cellNum, fName, lName);
        System.out.println("\n" + registrationMsg);

        if (!registrationMsg.contains("Cell number successfully captured.")) {
            System.out.println("Registration requirements not met. Exiting application.");
            inputScanner.close();
            return;
        }

        System.out.println("\n=== SYSTEM LOGIN ===");
        System.out.print("Enter Username: ");
        String loginUser = inputScanner.nextLine();
        System.out.print("Enter Password: ");
        String loginPass = inputScanner.nextLine();

        boolean isLoggedIn = userAuth.loginUser(loginUser, loginPass);
        System.out.println(userAuth.returnLoginStatus(isLoggedIn));

        if (!isLoggedIn) {
            inputScanner.close();
            return;
        }

        // --- QUICKCHAT MESSAGING MODULE ---
        System.out.println("\nWelcome to QuickChat.");
        String senderName = fName + " " + lName;

        Session session = new Session();
        session.storedMessages.addAll(Message.loadStoredMessages());
        for (Message m : session.storedMessages) {
            session.messageIDs.add(m.getMessageID());
            session.messageHashes.add(m.getMessageHash());
        }
        session.messageCounter = session.storedMessages.size();

        boolean sessionRunning = true;
        while (sessionRunning) {
            System.out.println("\n--- QuickChat Main Menu ---");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            System.out.print("Select an option (1-4): ");

            int menuChoice = readInt(inputScanner);

            switch (menuChoice) {
                case 1:
                    sendMessages(inputScanner, session);
                    break;
                case 2:
                    System.out.println("\n--- Recently Sent Messages ---");
                    System.out.print(Message.printMessages(session.sentMessages));
                    break;
                case 3:
                    System.out.println("Shutting down QuickChat. Goodbye!");
                    sessionRunning = false;
                    break;
                case 4:
                    storedMenu(inputScanner, session, senderName);
                    break;
                default:
                    System.out.println("Invalid selection. Please choose 1, 2, 3 or 4.");
            }
        }
        inputScanner.close();
    }

    // Option 1: collects the messages, applies the delivery action and fills the arrays
    private static void sendMessages(Scanner in, Session s) {
        System.out.print("How many messages would you like to enter? ");
        int total = readInt(in);
        if (total <= 0) {
            System.out.println("Please enter a whole number greater than zero.");
            return;
        }

        for (int i = 0; i < total; i++) {
            System.out.println("\n--- Message Entry " + (i + 1) + " of " + total + " ---");

            System.out.print("Enter Recipient Cell Number (with international code): ");
            String recipient = in.nextLine();

            System.out.print("Enter Message text (max 250 characters): ");
            String body = in.nextLine();

            Message draft = new Message(s.messageCounter, recipient, body, 1);

            String lengthFeedback = draft.checkMessageLength(body);
            System.out.println(lengthFeedback);
            if (!lengthFeedback.equals("Message ready to send.")) {
                System.out.println("Please try entering this message again.");
                i--;
                continue;
            }

            if (!draft.checkRecipientCell(recipient)) {
                System.out.println("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.");
                i--;
                continue;
            }

            System.out.println("\nSelect Delivery Action:");
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            System.out.print("Your choice: ");
            int action = readInt(in);
            if (action < 1 || action > 3) {
                System.out.println("Invalid option. Please try this message again.");
                i--;
                continue;
            }

            int number = s.messageCounter++;
            Message msg = new Message(number, recipient, body, action);

            switch (action) {
                case 1:
                    s.sentMessages.add(msg);
                    break;
                case 2:
                    s.disregardedMessages.add(msg);
                    break;
                default:
                    System.out.println(msg.storeMessage());
                    s.storedMessages.add(msg);
            }
            s.messageIDs.add(msg.getMessageID());
            s.messageHashes.add(msg.getMessageHash());

            System.out.println("\n--- SUBMISSION SUMMARY ---");
            System.out.println("Message ID: " + msg.getMessageID());
            System.out.println("Message Hash: " + msg.getMessageHash());
            System.out.println("Recipient: " + msg.getRecipientCell());
            System.out.println("Message: " + msg.getMessageText());
            System.out.println("Status: " + msg.getSendActionStatus());
        }

        System.out.println("\nTotal messages sent: " + Message.returnTotalMessages(s.sentMessages));
    }

    // Option 4: Stored Messages menu (options a to f)
    private static void storedMenu(Scanner in, Session s, String senderName) {
        System.out.println("\n--- Stored Messages ---");
        System.out.println("a) Display the sender and recipient of all stored messages");
        System.out.println("b) Display the longest stored message");
        System.out.println("c) Search for a message ID");
        System.out.println("d) Search all stored messages for a recipient");
        System.out.println("e) Delete a message using its message hash");
        System.out.println("f) Display a report of all stored messages");
        System.out.println("x) Back to main menu");
        System.out.print("Select an option (a-f or x): ");
        String choice = in.nextLine().trim().toLowerCase();

        switch (choice) {
            case "a":
                System.out.print(Message.displaySenderAndRecipient(s.storedMessages, senderName));
                break;

            case "b": {
                Message longest = Message.longestMessage(s.storedMessages);
                if (longest == null) {
                    System.out.println("No stored messages.");
                } else {
                    System.out.println("Longest stored message: " + longest.getMessageText());
                }
                break;
            }

            case "c": {
                System.out.print("Enter Message ID: ");
                Message found = Message.findByMessageID(s.storedMessages, in.nextLine().trim());
                if (found == null) {
                    System.out.println("No stored message found with that ID.");
                } else {
                    System.out.println("Recipient: " + found.getRecipientCell());
                    System.out.println("Message: " + found.getMessageText());
                }
                break;
            }

            case "d": {
                System.out.print("Enter Recipient Cell Number: ");
                List<Message> found = Message.findByRecipient(s.storedMessages, in.nextLine().trim());
                if (found.isEmpty()) {
                    System.out.println("No stored messages for that recipient.");
                } else {
                    System.out.print(Message.printMessages(found));
                }
                break;
            }

            case "e": {
                System.out.print("Enter Message Hash to delete: ");
                String hash = in.nextLine().trim();
                Message removed = Message.deleteByHash(s.storedMessages, hash);
                if (removed == null) {
                    System.out.println("No stored message found with that hash.");
                    break;
                }
                s.messageHashes.remove(removed.getMessageHash());
                s.messageIDs.remove(removed.getMessageID());
                Message.saveAll(s.storedMessages, Message.STORE_PATH);
                System.out.println("Message: \"" + removed.getMessageText() + "\" successfully deleted.");
                break;
            }

            case "f":
                System.out.print(Message.displayReport(s.storedMessages));
                break;

            case "x":
                break;

            default:
                System.out.println("Invalid selection. Please choose a to f, or x to go back.");
        }
    }

    // Reads a whole number from the console; returns -1 if the input is not a number
    private static int readInt(Scanner in) {
        try {
            return Integer.parseInt(in.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
