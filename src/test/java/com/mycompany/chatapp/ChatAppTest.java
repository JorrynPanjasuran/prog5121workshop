/*
 * PROG5121 – Part 3 POE
 * JUnit 5 unit tests for Login, Message, and MessageManager classes.
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ChatAppTest – covers all testable methods in Login, Message, and MessageManager.
 *
 * Run in NetBeans: right-click the file → Test File (or press Ctrl+F6).
 *
 * @author Jorryn Panjasuran 2025
 */
public class ChatAppTest {

    // Reset message arrays before every test so tests don't affect each other
    @BeforeEach
    public void setUp() {
        MessageManager.resetForUnitTests();
    }

    // ===========================================================
    // LOGIN – checkUserName
    // ===========================================================

    /**
     * Username "kyl_1" contains underscore and is exactly 5 chars → valid.
     */
    @Test
    public void testCheckUserName_Valid() {
        assertTrue(Login.checkUserName("kyl_1"),
                "Username with underscore and 5 chars should be valid.");
    }

    /**
     * Username "kyle_1" is 6 characters long → invalid (exceeds 5-char limit).
     */
    @Test
    public void testCheckUserName_TooLong() {
        assertFalse(Login.checkUserName("kyle_1"),
                "Username longer than 5 chars should be invalid.");
    }

    /**
     * Username "kyle1" has no underscore → invalid.
     */
    @Test
    public void testCheckUserName_NoUnderscore() {
        assertFalse(Login.checkUserName("kyle1"),
                "Username without underscore should be invalid.");
    }

    // ===========================================================
    // LOGIN – checkPasswordComplexity
    // ===========================================================

    /**
     * "Password1!" meets all requirements: 8+ chars, uppercase, digit, special.
     */
    @Test
    public void testCheckPasswordComplexity_Valid() {
        assertTrue(Login.checkPasswordComplexity("Password1!"),
                "Password meeting all rules should be valid.");
    }

    /**
     * "password1!" has no uppercase letter → invalid.
     */
    @Test
    public void testCheckPasswordComplexity_NoUppercase() {
        assertFalse(Login.checkPasswordComplexity("password1!"),
                "Password with no uppercase should be invalid.");
    }

    /**
     * "Pass1!" is only 6 characters → invalid (needs 8+).
     */
    @Test
    public void testCheckPasswordComplexity_TooShort() {
        assertFalse(Login.checkPasswordComplexity("Pass1!"),
                "Password shorter than 8 chars should be invalid.");
    }

    /**
     * "Password!" has no digit → invalid.
     */
    @Test
    public void testCheckPasswordComplexity_NoDigit() {
        assertFalse(Login.checkPasswordComplexity("Password!"),
                "Password with no digit should be invalid.");
    }

    /**
     * "Password1" has no special character → invalid.
     */
    @Test
    public void testCheckPasswordComplexity_NoSpecialChar() {
        assertFalse(Login.checkPasswordComplexity("Password1"),
                "Password with no special character should be invalid.");
    }

    // ===========================================================
    // LOGIN – checkCellPhoneNumber
    // ===========================================================

    /**
     * "+27838968976" starts with +27 and has exactly 9 digits after → valid.
     */
    @Test
    public void testCheckCellPhoneNumber_Valid() {
        assertTrue(Login.checkCellPhoneNumber("+27838968976"),
                "Valid South African number should pass.");
    }

    /**
     * "0838968976" does not start with +27 → invalid.
     */
    @Test
    public void testCheckCellPhoneNumber_NoCountryCode() {
        assertFalse(Login.checkCellPhoneNumber("0838968976"),
                "Number without +27 prefix should be invalid.");
    }

    /**
     * "+2783896897" has only 8 digits after +27 (needs 9) → invalid.
     */
    @Test
    public void testCheckCellPhoneNumber_TooShort() {
        assertFalse(Login.checkCellPhoneNumber("+2783896897"),
                "Number with fewer than 9 digits after +27 should be invalid.");
    }

    /**
     * "+278389689761" has 10 digits after +27 → invalid.
     */
    @Test
    public void testCheckCellPhoneNumber_TooLong() {
        assertFalse(Login.checkCellPhoneNumber("+278389689761"),
                "Number with more than 9 digits after +27 should be invalid.");
    }

    // ===========================================================
    // LOGIN – register()
    // ===========================================================

    /**
     * All valid fields → register() returns the success message.
     */
    @Test
    public void testRegister_Success() {
        Login user = new Login("kyl_1", "Password1!", "+27838968976", "Kyle", "Smith");
        String result = user.register();
        assertTrue(result.contains("successfully captured"),
                "Successful registration should return a success message.");
    }

    /**
     * Bad username → register() returns an error about username format.
     */
    @Test
    public void testRegister_BadUsername() {
        Login user = new Login("kyle1", "Password1!", "+27838968976", "Kyle", "Smith");
        String result = user.register();
        assertTrue(result.toLowerCase().contains("username"),
                "Bad username should produce a username error message.");
    }

    /**
     * Bad password → register() returns an error about password format.
     */
    @Test
    public void testRegister_BadPassword() {
        Login user = new Login("kyl_1", "pass", "+27838968976", "Kyle", "Smith");
        String result = user.register();
        assertTrue(result.toLowerCase().contains("password"),
                "Bad password should produce a password error message.");
    }

    /**
     * Bad cellphone → register() returns an error about cellphone format.
     */
    @Test
    public void testRegister_BadCellphone() {
        Login user = new Login("kyl_1", "Password1!", "0838968976", "Kyle", "Smith");
        String result = user.register();
        assertTrue(result.toLowerCase().contains("cell"),
                "Bad cellphone should produce a cellphone error message.");
    }

    // ===========================================================
    // LOGIN – login() and loginStatusMessage()
    // ===========================================================

    /**
     * Correct credentials → login() returns true.
     */
    @Test
    public void testLogin_CorrectCredentials() {
        Login user = new Login("kyl_1", "Password1!", "+27838968976", "Kyle", "Smith");
        assertTrue(user.login("kyl_1", "Password1!"),
                "Correct credentials should return true.");
    }

    /**
     * Wrong password → login() returns false.
     */
    @Test
    public void testLogin_WrongPassword() {
        Login user = new Login("kyl_1", "Password1!", "+27838968976", "Kyle", "Smith");
        assertFalse(user.login("kyl_1", "WrongPass1!"),
                "Wrong password should return false.");
    }

    /**
     * Wrong username → login() returns false.
     */
    @Test
    public void testLogin_WrongUsername() {
        Login user = new Login("kyl_1", "Password1!", "+27838968976", "Kyle", "Smith");
        assertFalse(user.login("ab_cd", "Password1!"),
                "Wrong username should return false.");
    }

    /**
     * Successful login → loginStatusMessage returns welcome with first and last name.
     */
    @Test
    public void testLoginStatusMessage_Success() {
        Login user = new Login("kyl_1", "Password1!", "+27838968976", "Kyle", "Smith");
        String msg = user.loginStatusMessage(true);
        assertTrue(msg.contains("Kyle") && msg.contains("Smith"),
                "Successful login message should contain the user's full name.");
    }

    /**
     * Failed login → loginStatusMessage returns an error message.
     */
    @Test
    public void testLoginStatusMessage_Failure() {
        Login user = new Login("kyl_1", "Password1!", "+27838968976", "Kyle", "Smith");
        String msg = user.loginStatusMessage(false);
        assertTrue(msg.toLowerCase().contains("incorrect"),
                "Failed login message should say credentials are incorrect.");
    }

    // ===========================================================
    // MESSAGE – generateMessageID
    // ===========================================================

    /**
     * generateMessageID always produces exactly 10 characters.
     */
    @Test
    public void testGenerateMessageID_Length() {
        String id = Message.generateMessageID();
        assertEquals(10, id.length(),
                "Generated message ID must be exactly 10 characters.");
    }

    /**
     * generateMessageID produces only numeric characters.
     */
    @Test
    public void testGenerateMessageID_NumericOnly() {
        String id = Message.generateMessageID();
        assertTrue(id.matches("\\d{10}"),
                "Generated message ID must contain only digits.");
    }

    // ===========================================================
    // MESSAGE – checkMessageID
    // ===========================================================

    /**
     * A 10-digit ID is valid.
     */
    @Test
    public void testCheckMessageID_Valid() {
        assertTrue(Message.checkMessageID("1234567890"),
                "10-digit ID should be valid.");
    }

    /**
     * An 11-character ID exceeds the 10-character limit → invalid.
     */
    @Test
    public void testCheckMessageID_TooLong() {
        assertFalse(Message.checkMessageID("12345678901"),
                "ID longer than 10 chars should be invalid.");
    }

    /**
     * An empty ID string → invalid.
     */
    @Test
    public void testCheckMessageID_Empty() {
        assertFalse(Message.checkMessageID(""),
                "Empty ID should be invalid.");
    }

    // ===========================================================
    // MESSAGE – checkRecipientCell
    // ===========================================================

    /**
     * "+27834567890" is a valid South African number (starts with +, 12 chars).
     */
    @Test
    public void testCheckRecipientCell_Valid() {
        assertTrue(Message.checkRecipientCell("+27834567890"),
                "Valid +27 number should pass recipient check.");
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
     * "+271" is too short (under 11 chars) → invalid.
     */
    @Test
    public void testCheckRecipientCell_TooShort() {
        assertFalse(Message.checkRecipientCell("+271"),
                "Number shorter than 11 chars should fail recipient check.");
    }

    // ===========================================================
    // MESSAGE – validateMessageLength
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
     * A message under 250 characters → "Message ready to send."
     */
    @Test
    public void testValidateMessageLength_Under250() {
        assertEquals("Message ready to send.", Message.validateMessageLength("Hello!"),
                "Short message should be ready to send.");
    }

    /**
     * A message of 251 characters → error message stating 1 char over limit.
     */
    @Test
    public void testValidateMessageLength_Over250() {
        String msg = "A".repeat(251);
        String result = Message.validateMessageLength(msg);
        assertTrue(result.contains("1"),
                "Message 1 char over limit should report being 1 over.");
    }

    /**
     * A 300-character message → error message stating 50 chars over limit.
     */
    @Test
    public void testValidateMessageLength_50Over() {
        String msg = "B".repeat(300);
        String result = Message.validateMessageLength(msg);
        assertTrue(result.contains("50"),
                "Message 50 chars over limit should report 50 over.");
    }

    // ===========================================================
    // MESSAGE – createMessageHash
    // ===========================================================

    /**
     * Hash must be uppercase and follow the pattern XX:N:FIRSTLAST.
     */
    @Test
    public void testCreateMessageHash_Format() {
        String hash = Message.createMessageHash("1234567890", 0, "Hello World");
        // Expected: "12:0:HELLOWORLD"
        assertEquals("12:0:HELLOWORLD", hash,
                "Hash should be first2ofID:msgNum:FirstWordLastWord in uppercase.");
    }

    /**
     * Single-word message — first and last word are the same, hash ends with NANA? No:
     * first = word, last = "NA" when there is only one word.
     */
    @Test
    public void testCreateMessageHash_SingleWord() {
        String hash = Message.createMessageHash("AB12345678", 1, "Hello");
        assertEquals("AB:1:HELLONA", hash,
                "Single-word message hash should use NA for the missing last word.");
    }

    /**
     * Hash output is always uppercase.
     */
    @Test
    public void testCreateMessageHash_Uppercase() {
        String hash = Message.createMessageHash("ab12345678", 2, "hi there");
        assertEquals(hash, hash.toUpperCase(),
                "Message hash must be fully uppercase.");
    }

    // ===========================================================
    // MESSAGE – sendOptions
    // ===========================================================

    /**
     * sendOptions("send") increments totalMessages and returns success string.
     */
    @Test
    public void testSendOptions_Send() {
        Message msg = new Message("+27834567890", "Test message", 0);
        int before = Message.returnTotalMessages();
        String result = msg.sendOptions("send");
        assertEquals("Message successfully sent.", result,
                "sendOptions('send') should return success message.");
        assertEquals(before + 1, Message.returnTotalMessages(),
                "Total messages counter should increment after send.");
    }

    /**
     * sendOptions("discard") returns the delete prompt string.
     */
    @Test
    public void testSendOptions_Discard() {
        Message msg = new Message("+27834567890", "Test message", 0);
        String result = msg.sendOptions("discard");
        assertEquals("Press 0 to delete message.", result,
                "sendOptions('discard') should return the delete prompt.");
    }

    /**
     * sendOptions("store") returns success string.
     */
    @Test
    public void testSendOptions_Store() {
        Message msg = new Message("+27834567890", "Test message", 0);
        String result = msg.sendOptions("store");
        assertEquals("Message successfully stored.", result,
                "sendOptions('store') should return stored success message.");
    }

    /**
     * sendOptions with an unrecognised option returns the invalid string.
     */
    @Test
    public void testSendOptions_Invalid() {
        Message msg = new Message("+27834567890", "Test message", 0);
        String result = msg.sendOptions("xyz");
        assertEquals("Invalid option.", result,
                "Unrecognised option should return 'Invalid option.'");
    }

    // ===========================================================
    // MESSAGE – printDetails
    // ===========================================================

    /**
     * printDetails() must include the recipient and message body.
     */
    @Test
    public void testPrintDetails_ContainsFields() {
        Message msg = new Message("+27834567890", "Hi there", 0);
        String details = msg.printDetails();
        assertTrue(details.contains("+27834567890") && details.contains("Hi there"),
                "printDetails() must include recipient and message body.");
    }

    // ===========================================================
    // MESSAGEMANAGER – array counters
    // ===========================================================

    /**
     * All counters start at zero after reset.
     */
    @Test
    public void testCounters_StartAtZero() {
        assertEquals(0, MessageManager.getSentCount(),    "Sent count should start at 0.");
        assertEquals(0, MessageManager.getStoreCount(),   "Store count should start at 0.");
        assertEquals(0, MessageManager.getDiscardCount(), "Discard count should start at 0.");
    }

    /**
     * populateTestMessages adds 2 sent, 1 disregarded, 2 stored.
     */
    @Test
    public void testPopulateTestMessages_Counts() {
        MessageManager.populateTestMessages();
        assertEquals(2, MessageManager.getSentCount(),
                "populateTestMessages should add 2 sent messages.");
        assertEquals(1, MessageManager.getDiscardCount(),
                "populateTestMessages should add 1 disregarded message.");
        assertEquals(2, MessageManager.getStoreCount(),
                "populateTestMessages should add 2 stored messages.");
    }

    // ===========================================================
    // MESSAGEMANAGER – removeSentMessageAtIndex
    // ===========================================================

    /**
     * Removing a message at index 0 reduces sentCount by 1.
     */
    @Test
    public void testRemoveSentMessageAtIndex_ReducesCount() {
        MessageManager.populateTestMessages();
        int before = MessageManager.getSentCount();
        MessageManager.removeSentMessageAtIndex(0);
        assertEquals(before - 1, MessageManager.getSentCount(),
                "Removing a sent message should reduce sentCount by 1.");
    }

    /**
     * After removal, the array has no gaps (slot 0 holds what was slot 1).
     */
    @Test
    public void testRemoveSentMessageAtIndex_ArrayCompacted() {
        MessageManager.populateTestMessages();
        // Record what was at index 1 before removal
        String secondRecipient = MessageManager.sentMessages[1].getRecipient();
        MessageManager.removeSentMessageAtIndex(0);
        // After removing index 0, what was index 1 moves to index 0
        assertEquals(secondRecipient, MessageManager.sentMessages[0].getRecipient(),
                "Array should compact: former index 1 becomes index 0 after removal.");
    }

    // ===========================================================
    // MESSAGE – flags
    // ===========================================================

    /**
     * A newly constructed message has all flags set to true (it is sent/received/read).
     */
    @Test
    public void testMessage_DefaultFlags() {
        Message msg = new Message("+27834567890", "Hello", 0);
        assertTrue(msg.isSent(),     "New message isSent flag should be true.");
        assertTrue(msg.isReceived(), "New message isReceived flag should be true.");
        assertTrue(msg.isRead(),     "New message isRead flag should be true.");
    }

    /**
     * setMessageType correctly stores and retrieves the type.
     */
    @Test
    public void testMessage_SetMessageType() {
        Message msg = new Message("+27834567890", "Hello", 0);
        msg.setMessageType("stored");
        assertEquals("stored", msg.getMessageType(),
                "getMessageType should return the value set by setMessageType.");
    }
}

// ───────────────────────── CODE ATTRIBUTION ─────────────────────────
// Title   : JUnit 5 Assertion Methods
// Author  : JUnit Team – Official API
// Date    : 17 Jun 2025
// Version : 1.0
// Source  : https://junit.org/junit5/docs/current/api/
//
// Title   : JUnit 5 @BeforeEach Lifecycle Annotation
// Author  : JUnit Team – Official API
// Date    : 17 Jun 2025
// Version : 1.0
// Source  : https://junit.org/junit5/docs/current/user-guide/#writing-tests-annotations
//
// Title   : String.repeat(int) (Java 11)
// Author  : Oracle Java SE 11 API
// Date    : 17 Jun 2025
// Version : 1.0
// Source  : https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#repeat(int)
//
// Title   : PROG5121 Lecture Slides
// Author  : The IIE / Rochelle Moodley (internal, unpublished)
// Date    : 2025
// ────────────────────────────────────────────────────────────────────
