package com.mycompany.luxwynneapp;

import java.util.Random;

/**
 * Message class handles tracking, validation, and processing 
 * for individual QuickChat messages.
 * @author Student
 */
public class Message {
    private String messageID;
    private int messageNumber;
    private String recipientCell;
    private String messageText;
    private String messageHash;
    private String sendActionStatus;

    // Constructor to initialize message attributes
    public Message(int messageNumber, String recipientCell, String messageText, int actionChoice) {
        this.messageNumber = messageNumber;
        this.recipientCell = recipientCell;
        this.messageText = messageText;
        this.messageID = generateMessageID();
        this.messageHash = createMessageHash();
        this.sendActionStatus = sentMessage(actionChoice);
    }

    // Helper to generate a random 10-digit ID number for tracking
    private String generateMessageID() {
        Random rand = new Random();
        long randomIdNum = 1000000000L + rand.nextInt(900000000);
        return String.valueOf(randomIdNum);
    }

    // Checks that the message ID does not exceed 10 characters
    public boolean checkMessageID(String id) {
        if (id != null && id.length() <= 10) {
            return true;
        }
        return false;
    }

    // Validates the recipient cell number format
    public boolean checkRecipientCell(String cellNumber) {
        return cellNumber != null && cellNumber.matches("^\\+\\d{1,3}\\d{1,10}$");
    }

    // Checks if message length is under 250 characters and returns feedback
    public String checkMessageLength(String msgContent) {
        if (msgContent != null && msgContent.length() <= 250) {
            return "Message ready to send.";
        } else {
            int characterExcess = msgContent.length() - 250;
            return "Message exceeds 250 characters by " + characterExcess + "; please reduce the size.";
        }
    }

    // Automatically creates the message hash format
    public String createMessageHash() {
        if (messageID == null || messageID.length() < 2 || messageText == null || messageText.trim().isEmpty()) {
            return "00:0:INVALID";
        }
        String idPrefix = messageID.substring(0, 2);
        String[] splitWords = messageText.trim().split("\\s+");
        String firstWordOnly = splitWords[0].replaceAll("[^a-zA-Z]", "").toUpperCase();
        String lastWordOnly = splitWords[splitWords.length - 1].replaceAll("[^a-zA-Z]", "").toUpperCase();
        
        return idPrefix + ":" + messageNumber + ":" + firstWordOnly + lastWordOnly;
    }

    // Overloaded method to allow testing hash generation with specific parameters
    public String createMessageHash(String id, int num, String text) {
        if (id == null || id.length() < 2 || text == null || text.trim().isEmpty()) {
            return "00:0:INVALID";
        }
        String idPrefix = id.substring(0, 2);
        String[] splitWords = text.trim().split("\\s+");
        String firstWordOnly = splitWords[0].replaceAll("[^a-zA-Z]", "").toUpperCase();
        String lastWordOnly = splitWords[splitWords.length - 1].replaceAll("[^a-zA-Z]", "").toUpperCase();
        
        return idPrefix + ":" + num + ":" + firstWordOnly + lastWordOnly;
    }

    // Handles user selection for sending, storing, or discarding
    public String sentMessage(int menuSelection) {
        switch (menuSelection) {
            case 1:
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                return "Message successfully stored.";
            default:
                return "Invalid option selected.";
        }
    }

    // Getter methods
    public String getMessageID() { return messageID; }
    public String getMessageHash() { return messageHash; }
    public String getRecipientCell() { return recipientCell; }
    public String getMessageText() { return messageText; }
    public String getSendActionStatus() { return sendActionStatus; }
}