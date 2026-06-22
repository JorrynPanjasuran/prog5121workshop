/*
 * PROG5121 – Part 3 POE
 * I wrote these JUnit 5 tests for the MessageManager class.
 * I use the SwiftDeliver Part 3 POE test data I defined in my scenario.
 *
 * Author: ST10448822
 * Date: 2025
 */

package com.mycompany.chatapp;

// I use JUnit 5 assertion methods throughout these tests.
// Title   : JUnit 5 Assertion Methods
// Author  : JUnit Team – Official API
// Date    : 23 Jun 2025
// Version : 1.0
// Source  : https://junit.org/junit5/docs/current/api/

// I use @BeforeEach to reset the message arrays before every test.
// Title   : JUnit 5 @BeforeEach Lifecycle Annotation
// Author  : JUnit Team – Official API
// Date    : 23 Jun 2025
// Version : 1.0
// Source  : https://junit.org/junit5/docs/current/user-guide/#writing-tests-annotations

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * MessageManagerTest – I test the five message arrays, their counters,
 * and all report / search / delete methods.
 *
 * I use the SwiftDeliver Part 3 POE test data I created:
 *   Message 1: +27761234567  "Has the pizza left the kitchen yet?"          → Sent
 *   Message 2: +27769876543  "Your order is stuck in traffic on the N1,
 *                              estimated arrival is now 8:15 PM."           → Stored
 *   Message 3: +27761112233  "Oops, wrong order sent to table six."         → Disregarded
 *   Message 4: 0769876543    "Rider is outside now!"                        → Sent
 *   Message 5: +27769876543  "Order confirmed and signed for, thanks!"      → Stored
 *
 * I annotated @BeforeEach to wipe every array and counter before each test.
 *
 * @author ST10448822 2025
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
    // "The system returns: 'Has the pizza left the kitchen yet?',
    //  'Rider is outside now!'"
    // ===========================================================

    /**
     * I expect sentMessages[0] to contain the first SwiftDeliver sent message.
     */
    @Test
    public void testSentMessages_FirstMessageBody() {
        MessageManager.populateTestMessages();
        assertEquals("Has the pizza left the kitchen yet?",
                MessageManager.sentMessages[0].getMessage(),
                "First sent message should be 'Has the pizza left the kitchen yet?'");
    }

    /**
     * I expect sentMessages[1] to contain the second SwiftDeliver sent message.
     */
    @Test
    public void testSentMessages_SecondMessageBody() {
        MessageManager.populateTestMessages();
        assertEquals("Rider is outside now!",
                MessageManager.sentMessages[1].getMessage(),
                "Second sent message should be 'Rider is outside now!'");
    }

    /**
     * I expect exactly 2 sent messages after populateTestMessages().
     */
    @Test
    public void testPopulateTestMessages_SentCount() {
        MessageManager.populateTestMessages();
        assertEquals(2, MessageManager.getSentCount(),
                "populateTestMessages should add 2 sent messages.");
    }

    /**
     * I expect exactly 2 stored messages after populateTestMessages().
     */
    @Test
    public void testPopulateTestMessages_StoreCount() {
        MessageManager.populateTestMessages();
        assertEquals(2, MessageManager.getStoreCount(),
                "populateTestMessages should add 2 stored messages.");
    }

    /**
     * I expect exactly 1 disregarded message after populateTestMessages().
     */
    @Test
    public void testPopulateTestMessages_DiscardCount() {
        MessageManager.populateTestMessages();
        assertEquals(1, MessageManager.getDiscardCount(),
                "populateTestMessages should add 1 disregarded message.");
    }

    /**
     * I expect the total across all three arrays to equal 5.
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
    // "The system returns: 'Your order is stuck in traffic on the N1,
    //  estimated arrival is now 8:15 PM.'"
    // ===========================================================

    /**
     * I expect getLongestStoredMessage to return the body of SwiftDeliver message 2.
     */
    @Test
    public void testLongestStoredMessage() {
        MessageManager.populateTestMessages();
        String longest = MessageManager.getLongestStoredMessage();
        assertEquals("Your order is stuck in traffic on the N1, estimated arrival is now 8:15 PM.",
                longest,
                "The longest stored message should be SwiftDeliver message 2.");
    }

    /**
     * I expect getLongestStoredMessage to return empty string when there are no stored messages.
     */
    @Test
    public void testLongestStoredMessage_Empty() {
        String longest = MessageManager.getLongestStoredMessage();
        assertEquals("", longest,
                "Should return empty string when no stored messages exist.");
    }

    // ===========================================================
    // Part 3 POE Test: Search for messageID
    // "Test Data: message 4 — The system returns: 'Rider is outside now!'"
    // ===========================================================

    /**
     * I search by message 4's auto-generated ID and expect "Rider is outside now!".
     */
    @Test
    public void testSearchByMessageID_FindsMessage4() {
        MessageManager.populateTestMessages();
        // Message 4 is the second sent message (index 1)
        String msg4ID = MessageManager.sentMessages[1].getMessageID();
        String result = MessageManager.searchByMessageID(msg4ID);
        assertEquals("Rider is outside now!", result,
                "Searching by message 4's ID should return 'Rider is outside now!'");
    }

    /**
     * I expect the not-found message when I search for an ID that does not exist.
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
    // "Test Data: +27769876543 — returns message 2 and message 5"
    // ===========================================================

    /**
     * I expect searchByRecipient("+27769876543") to return both message 2 and message 5.
     */
    @Test
    public void testSearchByRecipient_FindsBothMessages() {
        MessageManager.populateTestMessages();
        String result = MessageManager.searchByRecipient("+27769876543");
        assertTrue(result.contains("Your order is stuck in traffic on the N1, estimated arrival is now 8:15 PM."),
                "Result should contain SwiftDeliver message 2.");
        assertTrue(result.contains("Order confirmed and signed for, thanks!"),
                "Result should contain SwiftDeliver message 5.");
    }

    /**
     * I expect the not-found message when I search for a number with no messages.
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
    // "Test Data: SwiftDeliver message 2 successfully deleted."
    // ===========================================================

    /**
     * I expect deleteByMessageHash using message 2's hash to return the success string.
     */
    @Test
    public void testDeleteByMessageHash_SuccessMessage() {
        MessageManager.populateTestMessages();
        // Message 2 is the first stored message (index 0)
        String hash = MessageManager.storedMessages[0].getMessageHash();
        String result = MessageManager.deleteByMessageHash(hash);
        assertTrue(result.contains("successfully deleted"),
                "Delete result should confirm successful deletion.");
        assertTrue(result.contains("Your order is stuck in traffic on the N1, estimated arrival is now 8:15 PM."),
                "Delete result should name the deleted message.");
    }

    /**
     * I expect the store count to decrease by 1 after deleting message 2.
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
     * I expect storeCount to remain unchanged when I supply a hash that does not exist.
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
     * I expect displayReport with sent messages to not throw any exception.
     */
    @Test
    public void testDisplayReport_WithMessages_NoException() {
        MessageManager.populateTestMessages();
        assertDoesNotThrow(() -> MessageManager.displayReport(),
                "displayReport with messages should not throw.");
    }

    /**
     * I expect displayReport with no messages to not throw any exception.
     */
    @Test
    public void testDisplayReport_NoMessages_NoException() {
        assertDoesNotThrow(() -> MessageManager.displayReport(),
                "displayReport with empty array should not throw.");
    }

    /**
     * I expect displayStoredReport with stored messages to not throw any exception.
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
     * I expect removing the message at index 0 to reduce sentCount by exactly 1.
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
     * I expect the array to compact: after removing index 0, what was at index 1
     * moves to index 0.
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
     * I expect the vacated last slot to be null after compaction.
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
     * I expect removing the stored message at index 0 to reduce storeCount by 1.
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
     * I expect the array to compact: after removing stored index 0, what was at
     * index 1 moves to index 0.
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
     * I expect sentCount to return to 0 after a reset.
     */
    @Test
    public void testReset_ClearsSentCount() {
        MessageManager.populateTestMessages();
        MessageManager.resetForUnitTests();
        assertEquals(0, MessageManager.getSentCount(),
                "resetForUnitTests must set sentCount to 0.");
    }

    /**
     * I expect storeCount to return to 0 after a reset.
     */
    @Test
    public void testReset_ClearsStoreCount() {
        MessageManager.populateTestMessages();
        MessageManager.resetForUnitTests();
        assertEquals(0, MessageManager.getStoreCount(),
                "resetForUnitTests must set storeCount to 0.");
    }

    /**
     * I expect discardCount to return to 0 after a reset.
     */
    @Test
    public void testReset_ClearsDiscardCount() {
        MessageManager.populateTestMessages();
        MessageManager.resetForUnitTests();
        assertEquals(0, MessageManager.getDiscardCount(),
                "resetForUnitTests must set discardCount to 0.");
    }

    /**
     * I expect sentMessages[0] to be null after a reset.
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
// Date    : 23 Jun 2025
// Version : 1.0
// Source  : https://junit.org/junit5/docs/current/api/
//
// Title   : JUnit 5 @BeforeEach Lifecycle Annotation
// Author  : JUnit Team – Official API
// Date    : 23 Jun 2025
// Version : 1.0
// Source  : https://junit.org/junit5/docs/current/user-guide/#writing-tests-annotations
//
// Title   : PROG5121 Lecture Slides
// Author  : The IIE / Rochelle Moodley (internal, unpublished)
// Date    : 2025
// ────────────────────────────────────────────────────────────────────
