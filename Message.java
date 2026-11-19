package com.mycompany.luxwynneapp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Message class handles tracking, validation, hashing, JSON storage and reporting
 * for individual QuickChat messages.
 * @author Student
 */
public class Message {

    public static final Path STORE_PATH = Paths.get("stored_messages.json");
    public static final String STORED_CONFIRMATION = "Message successfully stored.";

    // Matches one flat JSON object, e.g. {"messageID": "...", ...}
    private static final Pattern OBJECT_PATTERN = Pattern.compile("\\{[^{}]*\\}");

    private final String messageID;
    private final int messageNumber;
    private final String recipientCell;
    private final String messageText;
    private final String messageHash;
    private final String sendActionStatus;
    private final String flag;

    // Constructor used by the app: generates a new random message ID
    public Message(int messageNumber, String recipientCell, String messageText, int actionChoice) {
        this(generateMessageID(), messageNumber, recipientCell, messageText, actionChoice);
    }

    // Constructor used when the ID already exists, for example when a message is reloaded from JSON
    public Message(String messageID, int messageNumber, String recipientCell, String messageText, int actionChoice) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipientCell = recipientCell;
        this.messageText = messageText;
        this.messageHash = createMessageHash(messageID, messageNumber, messageText);
        this.sendActionStatus = sentMessage(actionChoice);
        this.flag = flagFor(actionChoice);
    }

    // Helper to generate a random 10-digit ID number for tracking
    private static String generateMessageID() {
        Random rand = new Random();
        long randomIdNum = 1000000000L + rand.nextInt(900000000);
        return String.valueOf(randomIdNum);
    }

    // Checks that the message ID does not exceed 10 characters
    public boolean checkMessageID(String id) {
        return id != null && id.length() <= 10;
    }

    // Validates the recipient cell number: international code followed by up to ten digits
    public boolean checkRecipientCell(String cellNumber) {
        return cellNumber != null && cellNumber.matches("^\\+\\d{1,3}\\d{1,10}$");
    }

    // Checks if message length is 250 characters or fewer and returns feedback
    public String checkMessageLength(String msgContent) {
        if (msgContent == null) {
            msgContent = "";
        }
        if (msgContent.length() <= 250) {
            return "Message ready to send.";
        }
        int characterExcess = msgContent.length() - 250;
        return "Message exceeds 250 characters by " + characterExcess + "; please reduce the size.";
    }

    // Creates the hash for this message
    public String createMessageHash() {
        return createMessageHash(messageID, messageNumber, messageText);
    }

    // Creates a hash from the first two digits of the ID, the message number,
    // and the first and last words of the message, in capitals
    public String createMessageHash(String id, int num, String text) {
        if (id == null || id.length() < 2 || text == null || text.trim().isEmpty()) {
            return "00:0:INVALID";
        }
        String idPrefix = id.substring(0, 2);
        String[] words = text.trim().split("\\s+");
        String firstWord = words[0].replaceAll("[^a-zA-Z]", "").toUpperCase();
        String lastWord = words[words.length - 1].replaceAll("[^a-zA-Z]", "").toUpperCase();
        return idPrefix + ":" + num + ":" + firstWord + lastWord;
    }

    // Handles user selection for sending, storing, or disregarding
    public String sentMessage(int menuSelection) {
        switch (menuSelection) {
            case 1:
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                return STORED_CONFIRMATION;
            default:
                return "Invalid option selected.";
        }
    }

    // Maps the menu choice to the flag saved in the JSON file
    private static String flagFor(int actionChoice) {
        switch (actionChoice) {
            case 1:
                return "Sent";
            case 2:
                return "Disregarded";
            case 3:
                return "Stored";
            default:
                return "Invalid";
        }
    }

    // Maps a saved flag back to the menu choice
    private static int actionFor(String flag) {
        switch (flag) {
            case "Sent":
                return 1;
            case "Disregarded":
                return 2;
            case "Stored":
                return 3;
            default:
                return 0;
        }
    }

    // ---------- JSON storage ----------

    // Stores this message in the JSON file, keeping any messages already saved
    public String storeMessage() {
        return storeMessage(STORE_PATH);
    }

    public String storeMessage(Path path) {
        List<Message> stored = loadStoredMessages(path);
        stored.removeIf(m -> m.messageID.equals(this.messageID));
        stored.add(this);
        return saveAll(stored, path) ? STORED_CONFIRMATION : "Message could not be stored; please try again.";
    }

    // Writes a list of messages to the JSON file as a JSON array
    public static boolean saveAll(List<Message> messages, Path path) {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < messages.size(); i++) {
            json.append("  ").append(messages.get(i).toJson());
            json.append(i < messages.size() - 1 ? ",\n" : "\n");
        }
        json.append("]\n");
        try {
            Files.write(path, json.toString().getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException e) {
            System.out.println("Could not write to " + path + ": " + e.getMessage());
            return false;
        }
    }

    // Reads the JSON file back into a list of messages
    public static List<Message> loadStoredMessages() {
        return loadStoredMessages(STORE_PATH);
    }

    public static List<Message> loadStoredMessages(Path path) {
        List<Message> messages = new ArrayList<>();
        if (!Files.exists(path)) {
            return messages;
        }
        try {
            String json = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            Matcher matcher = OBJECT_PATTERN.matcher(json);
            while (matcher.find()) {
                String object = matcher.group();
                messages.add(new Message(
                        readField(object, "messageID"),
                        parseIntSafe(readField(object, "messageNumber")),
                        readField(object, "recipientCell"),
                        readField(object, "messageText"),
                        actionFor(readField(object, "flag"))));
            }
        } catch (IOException e) {
            System.out.println("Could not read " + path + ": " + e.getMessage());
        }
        return messages;
    }

    // Converts this message to a single JSON object
    private String toJson() {
        return "{\"messageID\": \"" + escape(messageID) + "\", "
                + "\"messageNumber\": " + messageNumber + ", "
                + "\"recipientCell\": \"" + escape(recipientCell) + "\", "
                + "\"messageText\": \"" + escape(messageText) + "\", "
                + "\"flag\": \"" + escape(flag) + "\"}";
    }

    // Reads one field value from a flat JSON object (string or number)
    private static String readField(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(?:\"((?:[^\"\\\\]|\\\\.)*)\"|(-?\\d+))");
        Matcher matcher = pattern.matcher(json);
        if (!matcher.find()) {
            return "";
        }
        return matcher.group(1) != null ? unescape(matcher.group(1)) : matcher.group(2);
    }

    private static int parseIntSafe(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String escape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    private static String unescape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char next = s.charAt(++i);
                if (next == 'n') {
                    sb.append('\n');
                } else if (next == 'r') {
                    sb.append('\r');
                } else {
                    sb.append(next);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // ---------- Reporting and searching (work on any list of messages) ----------

    // Returns the full details of every message in the list
    public static String printMessages(List<Message> messages) {
        if (messages.isEmpty()) {
            return "No messages to display.\n";
        }
        StringBuilder sb = new StringBuilder();
        for (Message m : messages) {
            sb.append("Message ID: ").append(m.messageID).append("\n")
              .append("Message Hash: ").append(m.messageHash).append("\n")
              .append("Recipient: ").append(m.recipientCell).append("\n")
              .append("Message: ").append(m.messageText).append("\n\n");
        }
        return sb.toString();
    }

    // Report of the full details of all messages, with a total
    public static String displayReport(List<Message> messages) {
        return "=== STORED MESSAGES REPORT ===\n"
                + printMessages(messages)
                + "Total messages: " + messages.size() + "\n";
    }

    // Lists the sender and recipient of every message in the list
    public static String displaySenderAndRecipient(List<Message> messages, String sender) {
        if (messages.isEmpty()) {
            return "No stored messages.\n";
        }
        StringBuilder sb = new StringBuilder();
        for (Message m : messages) {
            sb.append("Sender: ").append(sender)
              .append(" | Recipient: ").append(m.recipientCell).append("\n");
        }
        return sb.toString();
    }

    // Returns the total number of messages in the list
    public static int returnTotalMessages(List<Message> messages) {
        return messages.size();
    }

    // Returns the longest message in the list, or null if the list is empty
    public static Message longestMessage(List<Message> messages) {
        Message longest = null;
        for (Message m : messages) {
            if (longest == null || m.messageText.length() > longest.messageText.length()) {
                longest = m;
            }
        }
        return longest;
    }

    // Returns the message with the given ID, or null if not found
    public static Message findByMessageID(List<Message> messages, String id) {
        for (Message m : messages) {
            if (m.messageID.equals(id)) {
                return m;
            }
        }
        return null;
    }

    // Returns every message sent to the given recipient
    public static List<Message> findByRecipient(List<Message> messages, String cellNumber) {
        List<Message> found = new ArrayList<>();
        for (Message m : messages) {
            if (m.recipientCell.equals(cellNumber)) {
                found.add(m);
            }
        }
        return found;
    }

    // Removes the message with the given hash and returns it, or null if not found
    public static Message deleteByHash(List<Message> messages, String hash) {
        Iterator<Message> it = messages.iterator();
        while (it.hasNext()) {
            Message m = it.next();
            if (m.messageHash.equalsIgnoreCase(hash)) {
                it.remove();
                return m;
            }
        }
        return null;
    }

    // ---------- Getters ----------

    public String getMessageID() { return messageID; }
    public int getMessageNumber() { return messageNumber; }
    public String getMessageHash() { return messageHash; }
    public String getRecipientCell() { return recipientCell; }
    public String getMessageText() { return messageText; }
    public String getSendActionStatus() { return sendActionStatus; }
    public String getFlag() { return flag; }
}
