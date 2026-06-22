/*
 * PROG5121 – Part 3 POE
 * JUnit 5 tests for the Message class.
 * Tests: ID generation, ID validation, recipient validation,
 *        message length validation, hash creation, sendOptions(),
 *        printDetails(), flags, and getters.
 *
 * Author: Jorryn Panjasuran
 * Date: 2025
 */

package com.mycompany.chatapp;

// Title   : JUnit 5 Assertion Methods
// Author  : JUnit Team – Official API
// Date    : 17 Jun 2025
// Version : 1.0
// Source  : https://junit.org/junit5/docs/current/api/

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * MessageTest – unit tests for all static validators, hash creation,
 * send routing, flags, and output formatting in the Message class.
 *
 * NOTE: testGenerateMessageID_Unique was intentionally removed because
 * two random 10-digit numbers could theoretically match, making any
 * "must differ" assertion unreliable across test runs.
 *
 * NOTE: Message.totalMessages is a static counter that is never reset
 * between tests. All sendOptions tests therefore only verify the return
 * string, not the absolute counter value, to avoid order-dependent failures.
 *
 * @author Jorryn Panjasuran 2025
 */
public class MessageTest {

    // ===========================================================
    // generateMessageID
    // ===========================================================

    /**
     * Generated ID must always be exactly 10 characters long.
     */
    @Test
    public void testGenerateMessageID_Length() {
        String id = Message.generateMessageID();
        assertEquals(10, id.length(),
                "Generated message ID must be exactly 10 characters.");
    }

    /**
     * Generated ID must contain only digit characters (0–9).
     */
    @Test
    public void testGenerateMessageID_NumericOnly() {
        String id = Message.generateMessageID();
        assertTrue(id.matches("\\d{10}"),
                "Generated message ID must contain digits only.");
    }

    // ===========================================================
    // checkMessageID
    // ===========================================================

    /**
     * A 10-character string is a valid message ID.
     */
    @Test
    public void testCheckMessageID_Valid() {
        assertTrue(Message.checkMessageID("1234567890"),
                "10-digit ID should be valid.");
    }

    /**
     * An 11-character string exceeds the 10-character limit → invalid.
     */
    @Test
    public void testCheckMessageID_TooLong() {
        assertFalse(Message.checkMessageID("12345678901"),
                "ID longer than 10 chars should be invalid.");
    }

    /**
     * An empty string is not a valid ID.
     */
    @Test
    public void testCheckMessageID_Empty() {
        assertFalse(Message.checkMessageID(""),
                "Empty string should be an invalid ID.");
    }

    /**
     * null must return false — no NullPointerException allowed.
     */
    @Test
    public void testCheckMessageID_Null() {
        assertFalse(Message.checkMessageID(null),
                "Null ID should return false, not throw.");
    }

    // ===========================================================
    // checkRecipientCell
    // ===========================================================

    /**
     * "+27834567890" starts with '+' and is 12 chars → valid.
     */
    @Test
    public void testCheckRecipientCell_Valid() {
        assertTrue(Message.checkRecipientCell("+27834567890"),
                "Valid +27 recipient number should pass.");
    }

    /**
     * "0834567890" does not start with '+' → invalid.
     */
    @Test
    public void testCheckRecipientCell_NoPlus() {
        assertFalse(Message.checkRecipientCell("0834567890"),
                "Number without '+' prefix should fail recipient check.");
    }

    /**
     * "+271" is only 4 characters (minimum is 11) → invalid.
     */
    @Test
    public void testCheckRecipientCell_TooShort() {
        assertFalse(Message.checkRecipientCell("+271"),
                "Number shorter than 11 chars should fail recipient check.");
    }

    /**
     * "+27834567890123" is 15 characters (maximum is 13) → invalid.
     */
    @Test
    public void testCheckRecipientCell_TooLong() {
        assertFalse(Message.checkRecipientCell("+27834567890123"),
                "Number longer than 13 chars should fail recipient check.");
    }

    // ===========================================================
    // validateMessageLength
    // ===========================================================

    /**
     * A message of exactly 250 characters → "Message ready to send."
     */
    @Test
    public void testValidateMessageLength_Exactly250() {
        String msg = "A".repeat(250);
        assertEquals("Message ready to send.", Message.validateMessageLength(msg),
                "Message of exactly 250 chars should be ready to send.");
    }

    /**
     * A short message well under the limit → "Message ready to send."
     */
    @Test
    public void testValidateMessageLength_Under250() {
        assertEquals("Message ready to send.", Message.validateMessageLength("Hello!"),
                "Short message should be ready to send.");
    }

    /**
     * A 251-character message is 1 char over the limit.
     * The error message must contain "1".
     */
    @Test
    public void testValidateMessageLength_OneOver() {
        String msg = "A".repeat(251);
        String result = Message.validateMessageLength(msg);
        assertFalse(result.equals("Message ready to send."),
                "Message 1 char over limit must NOT be ready to send.");
        assertTrue(result.contains("1"),
                "Error message should state 1 character over the limit.");
    }

    /**
     * A 300-character message is 50 chars over the limit.
     * The error message must contain "50".
     */
    @Test
    public void testValidateMessageLength_FiftyOver() {
        String msg = "B".repeat(300);
        String result = Message.validateMessageLength(msg);
        assertFalse(result.equals("Message ready to send."),
                "Message 50 chars over limit must NOT be ready to send.");
        assertTrue(result.contains("50"),
                "Error message should state 50 characters over the limit.");
    }

    // ===========================================================
    // createMessageHash
    // ===========================================================

    /**
     * Hash format: first 2 chars of ID + ":" + message number + ":"
     * + first word + last word, all uppercase.
     * Input: ID="1234567890", msgNum=0, msg="Hello World"
     * Expected: "12:0:HELLOWORLD"
     */
    @Test
    public void testCreateMessageHash_Format() {
        String hash = Message.createMessageHash("1234567890", 0, "Hello World");
        assertEquals("12:0:HELLOWORLD", hash,
                "Hash must follow the pattern XX:N:FIRSTLAST in uppercase.");
    }

    /**
     * Single-word message: last word falls back to "NA".
     * Input: ID="AB12345678", msgNum=1, msg="Hello"
     * Expected: "AB:1:HELLONA"
     */
    @Test
    public void testCreateMessageHash_SingleWord() {
        String hash = Message.createMessageHash("AB12345678", 1, "Hello");
        assertEquals("AB:1:HELLONA", hash,
                "Single-word message hash should append NA for the missing last word.");
    }

    /**
     * Hash output must always be fully uppercase regardless of input case.
     */
    @Test
    public void testCreateMessageHash_IsUppercase() {
        String hash = Message.createMessageHash("ab12345678", 2, "hi there");
        assertEquals(hash.toUpperCase(), hash,
                "Message hash must be fully uppercase.");
    }

    /**
     * The correct message number must be embedded between the two colons.
     * Input: msgNum=5 → hash must start with "12:5:"
     */
    @Test
    public void testCreateMessageHash_MessageNumber() {
        String hash = Message.createMessageHash("1234567890", 5, "Go now");
        assertTrue(hash.startsWith("12:5:"),
                "Hash must embed the correct message number after the first colon.");
    }

    // ===========================================================
    // sendOptions – return value only
    // (static Message.totalMessages is never reset between tests,
    //  so we only assert on the returned String, not the counter)
    // ===========================================================

    /**
     * sendOptions("send") must return the sent success string.
     */
    @Test
    public void testSendOptions_Send() {
        Message msg = new Message("+27834567890", "Test message", 0);
        String result = msg.sendOptions("send");
        assertEquals("Message successfully sent.", result,
                "sendOptions('send') should return the sent success message.");
    }

    /**
     * sendOptions("discard") must return the discard prompt string.
     * POE requirement: "Press 0 to delete the message."
     */
    @Test
    public void testSendOptions_Discard() {
        Message msg = new Message("+27834567890", "Test message", 0);
        String result = msg.sendOptions("discard");
        assertEquals("Press 0 to delete the message.", result,
                "sendOptions('discard') should return 'Press 0 to delete message.'");
    }

    /**
     * sendOptions("store") must return the stored success string.
     */
    @Test
    public void testSendOptions_Store() {
        Message msg = new Message("+27834567890", "Test message", 0);
        String result = msg.sendOptions("store");
        assertEquals("Message successfully stored.", result,
                "sendOptions('store') should return the stored success message.");
    }

    /**
     * An unrecognised option must return "Invalid option."
     */
    @Test
    public void testSendOptions_InvalidOption() {
        Message msg = new Message("+27834567890", "Test message", 0);
        assertEquals("Invalid option.", msg.sendOptions("xyz"),
                "Unrecognised option should return 'Invalid option.'");
    }

    /**
     * sendOptions is case-insensitive: "SEND" must behave the same as "send".
     */
    @Test
    public void testSendOptions_CaseInsensitive() {
        Message msg = new Message("+27834567890", "Test message", 0);
        assertEquals("Message successfully sent.", msg.sendOptions("SEND"),
                "sendOptions should accept uppercase input.");
    }

    // ===========================================================
    // printDetails
    // ===========================================================

    /**
     * printDetails() must include the recipient phone number.
     */
    @Test
    public void testPrintDetails_ContainsRecipient() {
        Message msg = new Message("+27834567890", "Hi there", 0);
        assertTrue(msg.printDetails().contains("+27834567890"),
                "printDetails() must include the recipient number.");
    }

    /**
     * printDetails() must include the message body text.
     */
    @Test
    public void testPrintDetails_ContainsMessageBody() {
        Message msg = new Message("+27834567890", "Hi there", 0);
        assertTrue(msg.printDetails().contains("Hi there"),
                "printDetails() must include the message body.");
    }

    /**
     * printDetails() must include the auto-generated message ID.
     */
    @Test
    public void testPrintDetails_ContainsMessageID() {
        Message msg = new Message("+27834567890", "Hi there", 0);
        assertTrue(msg.printDetails().contains(msg.getMessageID()),
                "printDetails() must include the message ID.");
    }

    // ===========================================================
    // Status flags (default values after construction)
    // ===========================================================

    /**
     * A newly constructed Message has isSent = true by default.
     */
    @Test
    public void testMessage_DefaultIsSent() {
        Message msg = new Message("+27834567890", "Hello", 0);
        assertTrue(msg.isSent(),
                "New message isSent flag should default to true.");
    }

    /**
     * A newly constructed Message has isReceived = true by default.
     */
    @Test
    public void testMessage_DefaultIsReceived() {
        Message msg = new Message("+27834567890", "Hello", 0);
        assertTrue(msg.isReceived(),
                "New message isReceived flag should default to true.");
    }

    /**
     * A newly constructed Message has isRead = true by default.
     */
    @Test
    public void testMessage_DefaultIsRead() {
        Message msg = new Message("+27834567890", "Hello", 0);
        assertTrue(msg.isRead(),
                "New message isRead flag should default to true.");
    }

    // ===========================================================
    // Getters and setMessageType
    // ===========================================================

    /**
     * getRecipient() must return the exact value passed to the constructor.
     */
    @Test
    public void testGetRecipient() {
        Message msg = new Message("+27834567890", "Hello", 0);
        assertEquals("+27834567890", msg.getRecipient(),
                "getRecipient() should return the constructor value.");
    }

    /**
     * getMessage() must return the exact body text passed to the constructor.
     */
    @Test
    public void testGetMessage() {
        Message msg = new Message("+27834567890", "Hello world", 0);
        assertEquals("Hello world", msg.getMessage(),
                "getMessage() should return the constructor body text.");
    }

    /**
     * setMessageType / getMessageType round-trip must work correctly.
     */
    @Test
    public void testSetAndGetMessageType() {
        Message msg = new Message("+27834567890", "Hello", 0);
        msg.setMessageType("stored");
        assertEquals("stored", msg.getMessageType(),
                "getMessageType should return the value set by setMessageType.");
    }

    /**
     * The default messageType immediately after construction must be "sent".
     */
    @Test
    public void testDefaultMessageType_IsSent() {
        Message msg = new Message("+27834567890", "Hello", 0);
        assertEquals("sent", msg.getMessageType(),
                "Default messageType should be 'sent'.");
    }
}

// ───────────────────────── CODE ATTRIBUTION ─────────────────────────
// Title   : JUnit 5 Assertion Methods
// Author  : JUnit Team – Official API
// Date    : 17 Jun 2025
// Version : 1.0
// Source  : https://junit.org/junit5/docs/current/api/
//
// Title   : String.repeat(int) – Java 11
// Author  : Oracle Java SE 11 API
// Date    : 17 Jun 2025
// Version : 1.0
// Source  : https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#repeat(int)
//
// Title   : Random-ID & StringBuilder Pattern
// Author  : Oracle Java SE 8 API
// Date    : 17 Jun 2025
// Version : 1.0
// Sources :
//   https://docs.oracle.com/javase/8/docs/api/java/util/Random.html
//   https://docs.oracle.com/javase/8/docs/api/java/lang/StringBuilder.html
//
// Title   : PROG5121 Lecture Slides
// Author  : The IIE / Rochelle Moodley (internal, unpublished)
// Date    : 2025
// ────────────────────────────────────────────────────────────────────
