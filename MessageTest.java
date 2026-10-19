package com.mycompany.luxwynneapp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for verifying message validation, hashing, and length checks.
 * @author Student
 */
public class MessageTest {

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
}