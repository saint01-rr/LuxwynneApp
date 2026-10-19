package com.mycompany.luxwynneapp;

import java.util.Scanner;
import java.util.ArrayList;

/**
 * Main application runner for user authentication 
 * and the QuickChat messaging system.
 * @author Student
 */
public class LuxwynneApp {
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
        ArrayList<Message> messageLog = new ArrayList<>();
        boolean sessionRunning = true;

        while (sessionRunning) {
            System.out.println("\n--- QuickChat Main Menu ---");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.print("Select an option (1-3): ");
            
            String menuChoiceStr = inputScanner.nextLine();
            int menuChoice;
            try {
                menuChoice = Integer.parseInt(menuChoiceStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 3.");
                continue;
            }

            switch (menuChoice) {
                case 1:
                    System.out.print("How many messages would you like to enter? ");
                    int totalMessages = Integer.parseInt(inputScanner.nextLine());

                    for (int i = 0; i < totalMessages; i++) {
                        System.out.println("\n--- Message Entry " + (i + 1) + " of " + totalMessages + " ---");
                        
                        System.out.print("Enter Recipient Cell Number (with international code): ");
                        String recipientNumber = inputScanner.nextLine();

                        System.out.print("Enter Message text (max 250 characters): ");
                        String messageBody = inputScanner.nextLine();

                        // Test length first
                        Message validationDummy = new Message(i, recipientNumber, messageBody, 1);
                        String lengthFeedback = validationDummy.checkMessageLength(messageBody);
                        System.out.println(lengthFeedback);

                        if (!lengthFeedback.equals("Message ready to send.")) {
                            System.out.println("Please try entering this message again.");
                            i--; // retry current index
                            continue;
                        }

                        if (!validationDummy.checkRecipientCell(recipientNumber)) {
                            System.out.println("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.");
                            i--;
                            continue;
                        }

                        System.out.println("\nSelect Delivery Action:");
                        System.out.println("1) Send Message");
                        System.out.println("2) Disregard Message");
                        System.out.println("3) Store Message to send later");
                        System.out.print("Your choice: ");
                        int actionChoice = Integer.parseInt(inputScanner.nextLine());

                        Message completedMessage = new Message(i, recipientNumber, messageBody, actionChoice);
                        messageLog.add(completedMessage);

                        System.out.println("\n--- SUBMISSION SUMMARY ---");
                        System.out.println("Message ID: " + completedMessage.getMessageID());
                        System.out.println("Message Hash: " + completedMessage.getMessageHash());
                        System.out.println("Recipient: " + completedMessage.getRecipientCell());
                        System.out.println("Message: " + completedMessage.getMessageText());
                        System.out.println("Status: " + completedMessage.getSendActionStatus());
                    }
                    
                    System.out.println("\nTotal messages handled in this session: " + messageLog.size());
                    break;

                case 2:
                    System.out.println("Coming Soon.");
                    break;

                case 3:
                    System.out.println("Shutting down QuickChat. Goodbye!");
                    sessionRunning = false;
                    break;

                default:
                    System.out.println("Invalid selection. Please choose 1, 2, or 3.");
            }
        }
        inputScanner.close();
    }
}