/*
 * PROG5121 – Part 3 POE
 * JUnit 5 tests for the MessageManager class.
 * Tests: array counters, populateTestMessages, removeSentMessageAtIndex,
 *        deleteByMessageHash, searchByMessageID, searchByRecipient,
 *        showLongestMessage, displayReport, and resetForUnitTests.
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
 * MessageManagerTest – unit tests for the three message arrays,
 * their counters, removal and deletion logic, and all report methods.
 *
 * @BeforeEach wipes every array and counter before each test so that
 * no test carries state into the next one.
 *
 * @author Jorryn Panjasuran 2025
 */
public class MessageManagerTest {

    /**
     * Wipes all arrays and zeroes all counters before every single test.
     * This guarantees every test starts from a known empty state.
     */
    @BeforeEach
    public void setUp() {
        MessageManager.resetForUnitTests();
    }

    // ===========================================================
    // Initial state — counters must all be zero after reset
    // ===========================================================

    /**
     * sentCount must be 0 immediately after a reset.
     */
    @Test
    public void testSentCount_StartsAtZero() {
        assertEquals(0, MessageManager.getSentCount(),
                "Sent count should start at 0.");
    }

    /**
     * storeCount must be 0 immediately after a reset.
     */
    @Test
    public void testStoreCount_StartsAtZero() {
        assertEquals(0, MessageManager.getStoreCount(),
                "Store count should start at 0.");
    }

    /**
     * discardCount must be 0 immediately after a reset.
     */
    @Test
    public void testDiscardCount_StartsAtZero() {
        assertEquals(0, MessageManager.getDiscardCount(),
                "Discard count should start at 0.");
    }

    /**
     * Sum of all three counters must be 0 after a reset.
     */
    @Test
    public void testAllCounters_ZeroAfterReset() {
        int total = MessageManager.getSentCount()
                  + MessageManager.getStoreCount()
                  + MessageManager.getDiscardCount();
        assertEquals(0, total,
                "All counters combined should equal 0 after reset.");
    }

    // ===========================================================
    // populateTestMessages — counter checks
    // ===========================================================

    /**
     * populateTestMessages must add exactly 2 messages to the sent array.
     */
    @Test
    public void testPopulateTestMessages_SentCount() {
        MessageManager.populateTestMessages();
        assertEquals(2, MessageManager.getSentCount(),
                "populateTestMessages should add 2 sent messages.");
    }

    /**
     * populateTestMessages must add exactly 1 message to the disregarded array.
     */
    @Test
    public void testPopulateTestMessages_DiscardCount() {
        MessageManager.populateTestMessages();
        assertEquals(1, MessageManager.getDiscardCount(),
                "populateTestMessages should add 1 disregarded message.");
    }

    /**
     * populateTestMessages must add exactly 2 messages to the stored array.
     */
    @Test
    public void testPopulateTestMessages_StoreCount() {
        MessageManager.populateTestMessages();
        assertEquals(2, MessageManager.getStoreCount(),
                "populateTestMessages should add 2 stored messages.");
    }

    /**
     * Total messages across all three arrays after populateTestMessages must be 5.
     */
    @Test
    public void testPopulateTestMessages_TotalFive() {
        MessageManager.populateTestMessages();
        int total = MessageManager.getSentCount()
                  + MessageManager.getDiscardCount()
                  + MessageManager.getStoreCount();
        assertEquals(5, total,
                "populateTestMessages should place 5 messages in total across all arrays.");
    }

    // ===========================================================
    // populateTestMessages — content checks
    // ===========================================================

    /**
     * The first sent message must be addressed to +27834557896.
     */
    @Test
    public void testPopulateTestMessages_FirstSentRecipient() {
        MessageManager.populateTestMessages();
        assertEquals("+27834557896",
                MessageManager.sentMessages[0].getRecipient(),
                "First sent message should be addressed to +27834557896.");
    }

    /**
     * The second sent message must be addressed to +27831231234.
     */
    @Test
    public void testPopulateTestMessages_SecondSentRecipient() {
        MessageManager.populateTestMessages();
        assertEquals("+27831231234",
                MessageManager.sentMessages[1].getRecipient(),
                "Second sent message should be addressed to +27831231234.");
    }

    /**
     * storedMessages[0] must not be null after populate.
     */
    @Test
    public void testPopulateTestMessages_StoredNotNull() {
        MessageManager.populateTestMessages();
        assertNotNull(MessageManager.storedMessages[0],
                "storedMessages[0] should not be null after populate.");
    }

    /**
     * disregardedMessages[0] must not be null after populate.
     */
    @Test
    public void testPopulateTestMessages_DisregardedNotNull() {
        MessageManager.populateTestMessages();
        assertNotNull(MessageManager.disregardedMessages[0],
                "disregardedMessages[0] should not be null after populate.");
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
     * After removing index 0, the message that was at index 1 must now be at index 0
     * (the array must compact with no gaps).
     */
    @Test
    public void testRemoveSentMessage_ArrayCompacted() {
        MessageManager.populateTestMessages();
        // Record the recipient of the message that is currently at index 1
        String secondRecipient = MessageManager.sentMessages[1].getRecipient();
        MessageManager.removeSentMessageAtIndex(0);
        // After compaction, what was [1] must now be at [0]
        assertEquals(secondRecipient, MessageManager.sentMessages[0].getRecipient(),
                "After removal at [0], the former [1] message must move to [0].");
    }

    /**
     * The slot at the old end of the array must be null after removal
     * (no dangling reference left behind).
     */
    @Test
    public void testRemoveSentMessage_LastSlotNulled() {
        MessageManager.populateTestMessages();
        int before = MessageManager.getSentCount();   // 2
        MessageManager.removeSentMessageAtIndex(0);
        // Slot [before - 1] = slot [1] must now be null
        assertNull(MessageManager.sentMessages[before - 1],
                "The vacated last slot must be null after compaction.");
    }

    /**
     * Removing both messages one at a time must leave sentCount at 0.
     */
    @Test
    public void testRemoveSentMessage_RemoveAll() {
        MessageManager.populateTestMessages();   // sentCount = 2
        MessageManager.removeSentMessageAtIndex(0);  // sentCount = 1
        MessageManager.removeSentMessageAtIndex(0);  // sentCount = 0
        assertEquals(0, MessageManager.getSentCount(),
                "Removing all sent messages should leave sentCount at 0.");
    }

    // ===========================================================
    // deleteByMessageHash
    // ===========================================================

    /**
     * Deleting by the correct hash must reduce sentCount by 1.
     */
    @Test
    public void testDeleteByMessageHash_ReducesCount() {
        MessageManager.populateTestMessages();
        int before = MessageManager.getSentCount();
        String hash = MessageManager.sentMessages[0].getMessageHash();
        MessageManager.deleteByMessageHash(hash);
        assertEquals(before - 1, MessageManager.getSentCount(),
                "Deleting by hash should reduce sentCount by 1.");
    }

    /**
     * Supplying a hash that does not exist must leave sentCount unchanged.
     */
    @Test
    public void testDeleteByMessageHash_NotFound() {
        MessageManager.populateTestMessages();
        int before = MessageManager.getSentCount();
        MessageManager.deleteByMessageHash("NONEXISTENT:HASH");
        assertEquals(before, MessageManager.getSentCount(),
                "Deleting with an unknown hash must not change sentCount.");
    }

    /**
     * After deletion the removed message's recipient must no longer appear
     * anywhere in the remaining sentMessages slots.
     */
    @Test
    public void testDeleteByMessageHash_MessageGone() {
        MessageManager.populateTestMessages();
        String hash             = MessageManager.sentMessages[0].getMessageHash();
        String deletedRecipient = MessageManager.sentMessages[0].getRecipient();
        MessageManager.deleteByMessageHash(hash);

        // Check every remaining slot — none should hold the deleted recipient
        for (int i = 0; i < MessageManager.getSentCount(); i++) {
            assertNotEquals(deletedRecipient,
                    MessageManager.sentMessages[i].getRecipient(),
                    "Deleted message's recipient must no longer appear in sentMessages.");
        }
    }

    // ===========================================================
    // searchByMessageID — must not throw
    // ===========================================================

    /**
     * Searching for a message ID that exists must not throw any exception.
     */
    @Test
    public void testSearchByMessageID_ExistingID_NoException() {
        MessageManager.populateTestMessages();
        String existingID = MessageManager.sentMessages[0].getMessageID();
        assertDoesNotThrow(() -> MessageManager.searchByMessageID(existingID),
                "Searching for an existing ID should not throw.");
    }

    /**
     * Searching for a message ID that does not exist must not throw any exception.
     */
    @Test
    public void testSearchByMessageID_NotFound_NoException() {
        MessageManager.populateTestMessages();
        assertDoesNotThrow(() -> MessageManager.searchByMessageID("9999999999"),
                "Searching for a missing ID should not throw.");
    }

    // ===========================================================
    // searchByRecipient — must not throw
    // ===========================================================

    /**
     * Searching for a recipient that has sent messages must not throw.
     */
    @Test
    public void testSearchByRecipient_ExistingRecipient_NoException() {
        MessageManager.populateTestMessages();
        assertDoesNotThrow(() -> MessageManager.searchByRecipient("+27834557896"),
                "Searching for an existing recipient should not throw.");
    }

    /**
     * Searching for a recipient with no messages must not throw.
     */
    @Test
    public void testSearchByRecipient_NotFound_NoException() {
        MessageManager.populateTestMessages();
        assertDoesNotThrow(() -> MessageManager.searchByRecipient("+27000000000"),
                "Searching for a missing recipient should not throw.");
    }

    // ===========================================================
    // showLongestMessage — must not throw
    // ===========================================================

    /**
     * showLongestMessage when there are sent messages must not throw.
     */
    @Test
    public void testShowLongestMessage_WithMessages_NoException() {
        MessageManager.populateTestMessages();
        assertDoesNotThrow(() -> MessageManager.showLongestMessage(),
                "showLongestMessage with messages should not throw.");
    }

    /**
     * showLongestMessage when the sent array is empty must not throw.
     */
    @Test
    public void testShowLongestMessage_NoMessages_NoException() {
        // setUp() already reset everything — no messages exist
        assertDoesNotThrow(() -> MessageManager.showLongestMessage(),
                "showLongestMessage with an empty array should not throw.");
    }

    // ===========================================================
    // displayReport — must not throw
    // ===========================================================

    /**
     * displayReport when there are sent messages must not throw.
     */
    @Test
    public void testDisplayReport_WithMessages_NoException() {
        MessageManager.populateTestMessages();
        assertDoesNotThrow(() -> MessageManager.displayReport(),
                "displayReport with messages should not throw.");
    }

    /**
     * displayReport when the sent array is empty must not throw.
     */
    @Test
    public void testDisplayReport_NoMessages_NoException() {
        // setUp() already reset everything — no messages exist
        assertDoesNotThrow(() -> MessageManager.displayReport(),
                "displayReport with an empty array should not throw.");
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
     * After a reset, sentMessages[0] must be null — the array is truly cleared.
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
// Title   : Arrays.fill() Array Reset
// Author  : Oracle Arrays API; W3Schools
// Date    : 17 Jun 2025
// Version : 1.0
// Sources :
//   https://docs.oracle.com/javase/8/docs/api/java/util/Arrays.html
//   https://www.w3schools.com/java/ref_arrays_fill.asp
//
// Title   : PROG5121 Lecture Slides
// Author  : The IIE / Rochelle Moodley (internal, unpublished)
// Date    : 2025
// ────────────────────────────────────────────────────────────────────
