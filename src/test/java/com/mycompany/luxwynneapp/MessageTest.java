package com.mycompany.luxwynneapp;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for verifying message validation, hashing, length checks,
 * and the Part 3 array, search, delete, report and JSON storage features.
 * @author Student
 */
public class MessageTest {

    // ---------- Part 2 tests ----------

    @Test
    public void testMessageIDValid() {
        Message msg = new Message(0, "+27718693002", "Test message", 1);
        assertTrue(msg.checkMessageID(msg.getMessageID()));
    }

    @Test
    public void testMessageLengthValid() {
        Message msg = new Message(0, "+27718693002", "Hi Mike, can you join us for dinner tonight?", 1);
        assertEquals("Message ready to send.", msg.checkMessageLength(msg.getMessageText()));
    }

    @Test
    public void testMessageLengthInvalid() {
        // Creates a string longer than 250 characters to test length failure
        String longText = "A".repeat(260);
        Message msg = new Message(0, "+27718693002", longText, 1);
        assertTrue(msg.checkMessageLength(msg.getMessageText()).contains("exceeds 250 characters"));
    }

    @Test
    public void testRecipientCellValid() {
        Message msg = new Message(0, "+27718693002", "Test message", 1);
        assertTrue(msg.checkRecipientCell("+27718693002"));
    }

    @Test
    public void testRecipientCellInvalid() {
        Message msg = new Message(0, "08575975889", "Test message", 1);
        assertFalse(msg.checkRecipientCell("08575975889"));
    }

    @Test
    public void testMessageHashCreation() {
        Message msg = new Message(0, "+27718693002", "Hi tonight", 1);
        String hash = msg.createMessageHash("0012345678", 0, "Hi tonight");
        assertEquals("00:0:HITONIGHT", hash);
    }

    @Test
    public void testSendActionOptions() {
        Message msg = new Message(0, "+27718693002", "Test message", 1);
        assertEquals("Message successfully sent.", msg.sentMessage(1));
        assertEquals("Press 0 to delete the message.", msg.sentMessage(2));
        assertEquals("Message successfully stored.", msg.sentMessage(3));
    }

    // ---------- Part 3 tests ----------

    // Builds the messages from the Part 3 test data.
    // Flags: 1 = Sent, 2 = Disregard, 3 = Stored
    private List<Message> partThreeMessages() {
        List<Message> list = new ArrayList<>();
        list.add(new Message(0, "+27834557896", "Did you get the cake?", 1));
        list.add(new Message(1, "+27838884567", "Where are you? You are late! I have asked you to be on time.", 3));
        list.add(new Message(2, "+27834484567", "Yohoooo, I am at your gate.", 2));
        list.add(new Message(3, "0838884567", "It is dinner time !", 1));
        list.add(new Message(4, "+27838884567", "Ok, I am leaving without you.", 3));
        return list;
    }

    @TempDir
    Path tempDir;

    @Test
    public void testSentMessagesArrayPopulated() {
        List<Message> sent = new ArrayList<>();
        for (Message m : partThreeMessages()) {
            if (m.getFlag().equals("Sent")) {
                sent.add(m);
            }
        }
        assertEquals(2, sent.size());
        assertEquals("Did you get the cake?", sent.get(0).getMessageText());
        assertEquals("It is dinner time !", sent.get(1).getMessageText());
    }

    @Test
    public void testLongestMessage() {
        Message longest = Message.longestMessage(partThreeMessages());
        assertEquals("Where are you? You are late! I have asked you to be on time.", longest.getMessageText());
    }

    @Test
    public void testFindByMessageID() {
        List<Message> list = partThreeMessages();
        Message found = Message.findByMessageID(list, list.get(3).getMessageID());
        assertNotNull(found);
        assertEquals("It is dinner time !", found.getMessageText());
    }

    @Test
    public void testFindByRecipient() {
        List<Message> found = Message.findByRecipient(partThreeMessages(), "+27838884567");
        assertEquals(2, found.size());
        assertEquals("Where are you? You are late! I have asked you to be on time.", found.get(0).getMessageText());
        assertEquals("Ok, I am leaving without you.", found.get(1).getMessageText());
    }

    @Test
    public void testDeleteByHash() {
        List<Message> list = partThreeMessages();
        Message deleted = Message.deleteByHash(list, list.get(1).getMessageHash());
        assertNotNull(deleted);
        assertEquals("Where are you? You are late! I have asked you to be on time.", deleted.getMessageText());
        assertEquals(4, list.size());
    }

    @Test
    public void testReturnTotalMessages() {
        assertEquals(5, Message.returnTotalMessages(partThreeMessages()));
    }

    @Test
    public void testPrintMessagesIncludesDetails() {
        String printed = Message.printMessages(partThreeMessages());
        assertTrue(printed.contains("Recipient: +27834557896"));
        assertTrue(printed.contains("Message: It is dinner time !"));
    }

    @Test
    public void testStoredMessagesJsonRoundTrip() {
        List<Message> stored = new ArrayList<>();
        stored.add(new Message(1, "+27838884567", "Where are you? You are late!", 3));
        stored.add(new Message(4, "+27838884567", "He said \"hi\" there", 3));

        Path file = tempDir.resolve("stored_test.json");
        assertTrue(Message.saveAll(stored, file));

        List<Message> loaded = Message.loadStoredMessages(file);
        assertEquals(2, loaded.size());
        assertEquals(stored.get(0).getMessageID(), loaded.get(0).getMessageID());
        assertEquals("Stored", loaded.get(0).getFlag());
        assertEquals("He said \"hi\" there", loaded.get(1).getMessageText());
        assertEquals(4, loaded.get(1).getMessageNumber());
    }
}