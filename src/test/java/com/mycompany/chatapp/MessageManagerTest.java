/*
 * PROG5121 – Part 3 POE
 * JUnit 5 tests for the MessageManager class.
 * Tests use the exact Part 3 POE test data as specified in the rubric.
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

// Title   : JUnit 5 @BeforeEach Lifecycle Annotation
// Author  : JUnit Team – Official API
// Date    : 17 Jun 2025
// Version : 1.0
// Source  : https://junit.org/junit5/docs/current/user-guide/#writing-tests-annotations

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * MessageManagerTest – unit tests for the five message arrays, their counters,
 * and all report / search / delete methods.
 *
 * All tests use the Part 3 POE test data:
 *   Message 1: +27834557896  "Did you get the cake?"           → Sent
 *   Message 2: +27838884567  "Where are you? You are late!..." → Stored
 *   Message 3: +27834484567  "Yohoooo, I am at your gate."     → Disregarded
 *   Message 4: 0838884567    "It is dinner time !"              → Sent
 *   Message 5: +27838884567  "Ok, I am leaving without you."   → Stored
 *
 * @BeforeEach wipes every array and counter before each test.
 *
 * @author Jorryn Panjasuran 2025
 */
public class MessageManagerTest {

    @BeforeEach
    public void setUp() {
        MessageManager.resetForUnitTests();
    }

    // ===========================================================
    // Initial state — counters must all be zero after reset
    // ===========================================================

    @Test
    public void testSentCount_StartsAtZero() {
        assertEquals(0, MessageManager.getSentCount(),
                "Sent count should start at 0.");
    }

    @Test
    public void testStoreCount_StartsAtZero() {
        assertEquals(0, MessageManager.getStoreCount(),
                "Store count should start at 0.");
    }

    @Test
    public void testDiscardCount_StartsAtZero() {
        assertEquals(0, MessageManager.getDiscardCount(),
                "Discard count should start at 0.");
    }

    // ===========================================================
    // Part 3 POE Test: Sent Messages array correctly populated
    // "The system returns: 'Did you get the cake?', 'It is dinner time!'"
    // ===========================================================

    /**
     * After populateTestMessages(), sentMessages[0] must contain "Did you get the cake?"
     */
    @Test
    public void testSentMessages_FirstMessageBody() {
        MessageManager.populateTestMessages();
        assertEquals("Did you get the cake?",
                MessageManager.sentMessages[0].getMessage(),
                "First sent message should be 'Did you get the cake?'");
    }

    /**
     * After populateTestMessages(), sentMessages[1] must contain "It is dinner time !"
     */
    @Test
    public void testSentMessages_SecondMessageBody() {
        MessageManager.populateTestMessages();
        assertEquals("It is dinner time !",
                MessageManager.sentMessages[1].getMessage(),
                "Second sent message should be 'It is dinner time !'");
    }

    /**
     * Sent count must be exactly 2 after populateTestMessages().
     */
    @Test
    public void testPopulateTestMessages_SentCount() {
        MessageManager.populateTestMessages();
        assertEquals(2, MessageManager.getSentCount(),
                "populateTestMessages should add 2 sent messages.");
    }

    /**
     * Store count must be exactly 2 after populateTestMessages().
     */
    @Test
    public void testPopulateTestMessages_StoreCount() {
        MessageManager.populateTestMessages();
        assertEquals(2, MessageManager.getStoreCount(),
                "populateTestMessages should add 2 stored messages.");
    }

    /**
     * Discard count must be exactly 1 after populateTestMessages().
     */
    @Test
    public void testPopulateTestMessages_DiscardCount() {
        MessageManager.populateTestMessages();
        assertEquals(1, MessageManager.getDiscardCount(),
                "populateTestMessages should add 1 disregarded message.");
    }

    /**
     * Total across all three arrays must be 5.
     */
    @Test
    public void testPopulateTestMessages_TotalFive() {
        MessageManager.populateTestMessages();
        int total = MessageManager.getSentCount()
                  + MessageManager.getStoreCount()
                  + MessageManager.getDiscardCount();
        assertEquals(5, total,
                "Total messages across all arrays should be 5.");
    }

    // ===========================================================
    // Part 3 POE Test: Display the longest Message
    // "The system returns: 'Where are you? You are late! I have asked you to be on time.'"
    // ===========================================================

    /**
     * getLongestStoredMessage must return the body of message 2.
     */
    @Test
    public void testLongestStoredMessage() {
        MessageManager.populateTestMessages();
        String longest = MessageManager.getLongestStoredMessage();
        assertEquals("Where are you? You are late! I have asked you to be on time.",
                longest,
                "The longest stored message should be message 2.");
    }

    /**
     * getLongestStoredMessage must return empty string when there are no stored messages.
     */
    @Test
    public void testLongestStoredMessage_Empty() {
        String longest = MessageManager.getLongestStoredMessage();
        assertEquals("", longest,
                "Should return empty string when no stored messages exist.");
    }

    // ===========================================================
    // Part 3 POE Test: Search for messageID
    // "Test Data: message 4 — The system returns: 'It is dinner time!'"
    // ===========================================================

    /**
     * searchByMessageID using message 4's auto-generated ID must return "It is dinner time !"
     */
    @Test
    public void testSearchByMessageID_FindsMessage4() {
        MessageManager.populateTestMessages();
        // Message 4 is the second sent message (index 1)
        String msg4ID = MessageManager.sentMessages[1].getMessageID();
        String result = MessageManager.searchByMessageID(msg4ID);
        assertEquals("It is dinner time !", result,
                "Searching by message 4's ID should return 'It is dinner time !'");
    }

    /**
     * searchByMessageID for an ID that does not exist must return the not-found message.
     */
    @Test
    public void testSearchByMessageID_NotFound() {
        MessageManager.populateTestMessages();
        String result = MessageManager.searchByMessageID("9999999999");
        assertEquals("Message ID not found.", result,
                "Searching for a non-existent ID should return not-found message.");
    }

    // ===========================================================
    // Part 3 POE Test: Search all messages for a particular recipient
    // "Test Data: +27838884567 — returns message 2 and message 5"
    // ===========================================================

    /**
     * searchByRecipient("+27838884567") must return both message 2 and message 5.
     */
    @Test
    public void testSearchByRecipient_FindsBothMessages() {
        MessageManager.populateTestMessages();
        String result = MessageManager.searchByRecipient("+27838884567");
        assertTrue(result.contains("Where are you? You are late! I have asked you to be on time."),
                "Result should contain message 2.");
        assertTrue(result.contains("Ok, I am leaving without you."),
                "Result should contain message 5.");
    }

    /**
     * searchByRecipient for a number with no messages must return the not-found message.
     */
    @Test
    public void testSearchByRecipient_NotFound() {
        MessageManager.populateTestMessages();
        String result = MessageManager.searchByRecipient("+27000000000");
        assertEquals("No messages found for that recipient.", result,
                "Should return not-found message when recipient has no messages.");
    }

    // ===========================================================
    // Part 3 POE Test: Delete a message using a message hash
    // "Test Data: Test Message 2 — 'Where are you?...' successfully deleted."
    // ===========================================================

    /**
     * deleteByMessageHash using message 2's hash must return the success string.
     */
    @Test
    public void testDeleteByMessageHash_SuccessMessage() {
        MessageManager.populateTestMessages();
        // Message 2 is the first stored message (index 0)
        String hash = MessageManager.storedMessages[0].getMessageHash();
        String result = MessageManager.deleteByMessageHash(hash);
        assertTrue(result.contains("successfully deleted"),
                "Delete result should confirm successful deletion.");
        assertTrue(result.contains("Where are you? You are late! I have asked you to be on time."),
                "Delete result should name the deleted message.");
    }

    /**
     * After deleting message 2, the stored count must decrease by 1.
     */
    @Test
    public void testDeleteByMessageHash_ReducesStoreCount() {
        MessageManager.populateTestMessages();
        int before = MessageManager.getStoreCount();
        String hash = MessageManager.storedMessages[0].getMessageHash();
        MessageManager.deleteByMessageHash(hash);
        assertEquals(before - 1, MessageManager.getStoreCount(),
                "Deleting a stored message should reduce storeCount by 1.");
    }

    /**
     * Supplying a hash that does not exist must leave storeCount unchanged.
     */
    @Test
    public void testDeleteByMessageHash_NotFound() {
        MessageManager.populateTestMessages();
        int before = MessageManager.getStoreCount();
        String result = MessageManager.deleteByMessageHash("NONEXISTENT:HASH");
        assertEquals(before, MessageManager.getStoreCount(),
                "Deleting with an unknown hash must not change storeCount.");
        assertEquals("Message hash not found.", result,
                "Result should say hash not found.");
    }

    // ===========================================================
    // Part 3 POE Test: Display Report (does not throw)
    // ===========================================================

    /**
     * displayReport with sent messages must not throw any exception.
     */
    @Test
    public void testDisplayReport_WithMessages_NoException() {
        MessageManager.populateTestMessages();
        assertDoesNotThrow(() -> MessageManager.displayReport(),
                "displayReport with messages should not throw.");
    }

    /**
     * displayReport with no messages must not throw any exception.
     */
    @Test
    public void testDisplayReport_NoMessages_NoException() {
        assertDoesNotThrow(() -> MessageManager.displayReport(),
                "displayReport with empty array should not throw.");
    }

    /**
     * displayStoredReport with stored messages must not throw any exception.
     */
    @Test
    public void testDisplayStoredReport_WithMessages_NoException() {
        MessageManager.populateTestMessages();
        assertDoesNotThrow(() -> MessageManager.displayStoredReport(),
                "displayStoredReport with messages should not throw.");
    }

    // ===========================================================
    // removeSentMessageAtIndex
    // ===========================================================

    /**
     * Removing the message at index 0 must reduce sentCount by exactly 1.
     */
    @Test
    public void testRemoveSentMessage_ReducesCount() {
        MessageManager.populateTestMessages();
        int before = MessageManager.getSentCount();
        MessageManager.removeSentMessageAtIndex(0);
        assertEquals(before - 1, MessageManager.getSentCount(),
                "Removing one sent message should reduce sentCount by 1.");
    }

    /**
     * After removing index 0, what was at index 1 moves to index 0 (array compacts).
     */
    @Test
    public void testRemoveSentMessage_ArrayCompacted() {
        MessageManager.populateTestMessages();
        String secondMsg = MessageManager.sentMessages[1].getMessage();
        MessageManager.removeSentMessageAtIndex(0);
        assertEquals(secondMsg, MessageManager.sentMessages[0].getMessage(),
                "After removal at [0], the former [1] message must move to [0].");
    }

    /**
     * The vacated last slot must be null after compaction.
     */
    @Test
    public void testRemoveSentMessage_LastSlotNulled() {
        MessageManager.populateTestMessages();
        int before = MessageManager.getSentCount();
        MessageManager.removeSentMessageAtIndex(0);
        assertNull(MessageManager.sentMessages[before - 1],
                "The vacated last slot must be null after compaction.");
    }

    // ===========================================================
    // removeStoredMessageAtIndex
    // ===========================================================

    /**
     * Removing the stored message at index 0 must reduce storeCount by 1.
     */
    @Test
    public void testRemoveStoredMessage_ReducesCount() {
        MessageManager.populateTestMessages();
        int before = MessageManager.getStoreCount();
        MessageManager.removeStoredMessageAtIndex(0);
        assertEquals(before - 1, MessageManager.getStoreCount(),
                "Removing one stored message should reduce storeCount by 1.");
    }

    /**
     * After removing stored index 0, what was at index 1 moves to index 0.
     */
    @Test
    public void testRemoveStoredMessage_ArrayCompacted() {
        MessageManager.populateTestMessages();
        String secondMsg = MessageManager.storedMessages[1].getMessage();
        MessageManager.removeStoredMessageAtIndex(0);
        assertEquals(secondMsg, MessageManager.storedMessages[0].getMessage(),
                "After removal at [0], the former [1] stored message must move to [0].");
    }

    // ===========================================================
    // resetForUnitTests
    // ===========================================================

    /**
     * After a reset, sentCount must return to 0.
     */
    @Test
    public void testReset_ClearsSentCount() {
        MessageManager.populateTestMessages();
        MessageManager.resetForUnitTests();
        assertEquals(0, MessageManager.getSentCount(),
                "resetForUnitTests must set sentCount to 0.");
    }

    /**
     * After a reset, storeCount must return to 0.
     */
    @Test
    public void testReset_ClearsStoreCount() {
        MessageManager.populateTestMessages();
        MessageManager.resetForUnitTests();
        assertEquals(0, MessageManager.getStoreCount(),
                "resetForUnitTests must set storeCount to 0.");
    }

    /**
     * After a reset, discardCount must return to 0.
     */
    @Test
    public void testReset_ClearsDiscardCount() {
        MessageManager.populateTestMessages();
        MessageManager.resetForUnitTests();
        assertEquals(0, MessageManager.getDiscardCount(),
                "resetForUnitTests must set discardCount to 0.");
    }

    /**
     * After a reset, sentMessages[0] must be null.
     */
    @Test
    public void testReset_ArraysNulled() {
        MessageManager.populateTestMessages();
        MessageManager.resetForUnitTests();
        assertNull(MessageManager.sentMessages[0],
                "sentMessages[0] must be null after reset.");
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
// Title   : PROG5121 Lecture Slides
// Author  : The IIE / Rochelle Moodley (internal, unpublished)
// Date    : 2025
// ────────────────────────────────────────────────────────────────────
