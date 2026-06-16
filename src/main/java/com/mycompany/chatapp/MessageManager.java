/*
 * PROG5121 – Part 3 POE
 * Manages all message arrays, JSON loading, sending, reports, and deletes.
 *
 * Author: Jorryn Panjasuran
 * Date: 2025
 */

package com.mycompany.chatapp;

// Title   : Arrays.fill() Array Reset
// Author  : Oracle Arrays API; W3Schools
// Date    : 17 Jun 2025
// Version : 1.0
// Sources :
//   https://docs.oracle.com/javase/8/docs/api/java/util/Arrays.html
//   https://www.w3schools.com/java/ref_arrays_fill.asp

// Title   : StringBuilder for Efficient Concatenation
// Author  : Oracle Docs
// Date    : 17 Jun 2025
// Version : 1.0
// Source  : https://docs.oracle.com/javase/8/docs/api/java/lang/StringBuilder.html

// Title   : Scanner Console Input
// Author  : Oracle Java SE Docs
// Date    : 17 Jun 2025
// Version : 1.0
// Source  : https://docs.oracle.com/javase/8/docs/api/java/util/Scanner.html

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * MessageManager – static utility class that owns the three message arrays
 * (sent, stored, disregarded) and all operations on them:
 * sending, disregarding, JSON load/save, and all reports.
 *
 * @author Jorryn Panjasuran 2025
 */
public class MessageManager {

    // ---------------------------------------------------------------
    // In-memory storage arrays (fixed-size, used as dynamic lists with counters)
    // ---------------------------------------------------------------

    // Title   : Arrays in Java
    // Author  : GeeksforGeeks
    // Date    : 17 Jun 2025
    // Source  : https://www.geeksforgeeks.org/arrays-in-java/

    /** Holds all messages that were sent in this session. */
    static Message[] sentMessages       = new Message[100];

    /** Holds messages the user chose to disregard/delete. */
    static Message[] disregardedMessages = new Message[100];

    /** Holds messages the user chose to store for later. */
    static Message[] storedMessages     = new Message[100];

    /** Parallel arrays for fast hash/ID lookups without scanning Message objects. */
    static String[] messageHashes = new String[100];
    static String[] messageIDs    = new String[100];

    // Counters track how many slots in each array are actually used
    private static int sentCount    = 0;
    private static int discardCount = 0;
    private static int storeCount   = 0;

    // ---------------------------------------------------------------
    // Public counter accessors (used by JUnit tests)
    // ---------------------------------------------------------------

    public static int getSentCount()    { return sentCount;    }
    public static int getStoreCount()   { return storeCount;   }
    public static int getDiscardCount() { return discardCount; }

    // ---------------------------------------------------------------
    // Sending a message
    // ---------------------------------------------------------------

    /**
     * Prompts the user for a recipient and message body (via Scanner),
     * validates input, then routes the message to sent/stored/disregarded
     * based on the user's choice.
     *
     * @param scanner shared Scanner instance
     * @param msgNum  zero-based index in the current send batch (used for hash)
     * @return true if the message was successfully placed in any array
     */
    public static boolean sendMessage(Scanner scanner, int msgNum) {

        // --- Step 1: Recipient ---
        System.out.print("Enter recipient phone number (e.g. +27834567890): ");
        String recipient = scanner.nextLine().trim();

        if (!Message.checkRecipientCell(recipient)) {
            System.out.println("Cell phone number is incorrectly formatted.");
            return false;
        }

        // --- Step 2: Message body ---
        System.out.print("Enter your message (max 250 characters): ");
        String content = scanner.nextLine();

        String feedback = Message.validateMessageLength(content);
        System.out.println(feedback);

        if (!"Message ready to send.".equals(feedback)) {
            return false;   // too long; caller will notify user
        }

        // --- Step 3: Build the Message object ---
        Message msg = new Message(recipient, content, msgNum);
        System.out.println("Message ID  : " + msg.getMessageID());
        System.out.println("Message Hash: " + msg.getMessageHash());

        // --- Step 4: Ask the user what to do with it ---
        System.out.println("What would you like to do?");
        System.out.println("  1) Send");
        System.out.println("  2) Discard");
        System.out.println("  3) Store");
        System.out.print("Choice: ");
        String action = scanner.nextLine().trim();

        // --- Step 5: Route to the correct array ---
        switch (action) {
            case "1" -> {    // SEND
                msg.setMessageType("sent");
                msg.storeMessageToJson();   // persist to file

                if (sentCount < sentMessages.length) {
                    sentMessages[sentCount]  = msg;
                    messageHashes[sentCount] = msg.getMessageHash();
                    messageIDs[sentCount]    = msg.getMessageID();
                    sentCount++;
                    System.out.println("Message successfully sent.");
                    System.out.println(msg.printDetails());
                    return true;
                }
                System.out.println("Sent message array is full.");
                return false;
            }
            case "2" -> {    // DISCARD
                msg.setMessageType("disregarded");
                msg.storeMessageToJson();

                if (discardCount < disregardedMessages.length) {
                    disregardedMessages[discardCount++] = msg;
                    System.out.println("Message discarded.");
                    return true;
                }
                System.out.println("Disregarded array is full.");
                return false;
            }
            case "3" -> {    // STORE
                msg.setMessageType("stored");
                msg.storeMessageToJson();

                if (storeCount < storedMessages.length) {
                    storedMessages[storeCount++] = msg;
                    System.out.println("Message successfully stored.");
                    return true;
                }
                System.out.println("Stored message array is full.");
                return false;
            }
            default -> {
                System.out.println("Invalid option — message not saved.");
                return false;
            }
        }
    }

    // ---------------------------------------------------------------
    // JSON hydration on startup
    // ---------------------------------------------------------------

    /**
     * Reads all messages from messages.json at application startup
     * and distributes them into the correct in-memory arrays.
     * Called before the user registers so arrays are ready.
     */
    public static void loadMessagesFromFile() {
        // Title   : File.exists() for Persistence Check
        // Author  : Stack Overflow Q/1816673
        // Source  : https://stackoverflow.com/questions/1816673/
        File file = new File("messages.json");
        if (!file.exists()) {
            System.out.println("No saved messages found. Starting fresh.");
            return;
        }

        List<Message> loaded = Message.readMessagesFromFile("messages.json");

        for (Message msg : loaded) {
            if (msg == null) continue;

            switch (msg.getMessageType().toLowerCase()) {
                case "sent" -> {
                    if (sentCount < sentMessages.length) {
                        sentMessages[sentCount]  = msg;
                        messageHashes[sentCount] = msg.getMessageHash();
                        messageIDs[sentCount]    = msg.getMessageID();
                        sentCount++;
                    }
                }
                case "stored"      -> { if (storeCount   < storedMessages.length)      storedMessages[storeCount++]       = msg; }
                case "disregarded" -> { if (discardCount < disregardedMessages.length) disregardedMessages[discardCount++] = msg; }
            }
        }
        System.out.println("Messages loaded from file successfully.");
    }

    /**
     * After login, loads only "stored" messages from file to avoid duplicating
     * entries that were already pulled in by loadMessagesFromFile().
     */
    public static void loadStoredMessagesFromJson() {
        List<Message> loaded = Message.readMessagesFromFile("messages.json");
        if (loaded == null) return;

        for (Message msg : loaded) {
            if (msg != null
                    && "stored".equalsIgnoreCase(msg.getMessageType())
                    && storeCount < storedMessages.length) {
                storedMessages[storeCount++] = msg;
            }
        }
    }

    // ---------------------------------------------------------------
    // Reports sub-menu
    // ---------------------------------------------------------------

    /**
     * Displays the reports menu and routes to the selected report.
     *
     * @param scanner shared Scanner instance
     */
    public static void showReports(Scanner scanner) {
        System.out.println("\n===========================================");
        System.out.println("              Reports Menu                 ");
        System.out.println("===========================================");
        System.out.println("1) Show Sender & Recipients");
        System.out.println("2) Longest Message");
        System.out.println("3) Search by Message ID");
        System.out.println("4) Search by Recipient");
        System.out.println("5) Delete by Message Hash");
        System.out.println("6) Full Sent Report");
        System.out.print("Choose a report: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> showSenderAndRecipients();
            case "2" -> showLongestMessage();
            case "3" -> {
                System.out.print("Enter Message ID to search: ");
                searchByMessageID(scanner.nextLine().trim());
            }
            case "4" -> {
                System.out.print("Enter recipient number to search: ");
                searchByRecipient(scanner.nextLine().trim());
            }
            case "5" -> {
                System.out.print("Enter Message Hash to delete: ");
                deleteByMessageHash(scanner.nextLine().trim());
            }
            case "6" -> displayReport();
            default  -> System.out.println("Invalid report option.");
        }
    }

    // ---------------------------------------------------------------
    // Individual report methods
    // ---------------------------------------------------------------

    /**
     * Prints a list of all sent messages showing ID, recipient, and body.
     */
    public static void showSenderAndRecipients() {
        // Title   : StringBuilder for Efficient Concatenation
        // Author  : Oracle Docs
        // Source  : https://docs.oracle.com/javase/8/docs/api/java/lang/StringBuilder.html
        if (sentCount == 0) {
            System.out.println("No sent messages to display.");
            return;
        }
        System.out.println("\n--- Sent Messages ---");
        for (int i = 0; i < sentCount; i++) {
            if (sentMessages[i] != null) {
                System.out.println("Message ID : " + sentMessages[i].getMessageID());
                System.out.println("Recipient  : " + sentMessages[i].getRecipient());
                System.out.println("Message    : " + sentMessages[i].getMessage());
                System.out.println("---------------------");
            }
        }
    }

    /**
     * Finds and prints the longest message in the sent array.
     */
    public static void showLongestMessage() {
        Message longestMsg = null;
        int maxLen = 0;

        for (int i = 0; i < sentCount; i++) {
            if (sentMessages[i] != null
                    && sentMessages[i].getMessage().length() > maxLen) {
                maxLen     = sentMessages[i].getMessage().length();
                longestMsg = sentMessages[i];
            }
        }

        if (longestMsg != null) {
            System.out.println("\n--- Longest Message ---");
            System.out.println(longestMsg.printDetails());
        } else {
            System.out.println("No sent messages found.");
        }
    }

    /**
     * Searches the sent array for a message matching the given ID.
     *
     * @param id the message ID to find
     */
    public static void searchByMessageID(String id) {
        for (int i = 0; i < sentCount; i++) {
            if (sentMessages[i] != null
                    && sentMessages[i].getMessageID().equals(id)) {
                System.out.println("Recipient : " + sentMessages[i].getRecipient());
                System.out.println("Message   : " + sentMessages[i].getMessage());
                return;
            }
        }
        System.out.println("Message ID not found.");
    }

    /**
     * Searches both sent and stored arrays for messages to the given recipient.
     *
     * @param recipient phone number to search for
     */
    public static void searchByRecipient(String recipient) {
        boolean found = false;
        System.out.println("\n--- Messages to " + recipient + " ---");

        for (int i = 0; i < sentCount; i++) {
            if (sentMessages[i] != null
                    && sentMessages[i].getRecipient().equals(recipient)) {
                System.out.println("[Sent]   " + sentMessages[i].getMessage());
                found = true;
            }
        }

        for (int i = 0; i < storeCount; i++) {
            if (storedMessages[i] != null
                    && storedMessages[i].getRecipient().equals(recipient)) {
                System.out.println("[Stored] " + storedMessages[i].getMessage());
                found = true;
            }
        }

        if (!found) {
            System.out.println("No messages found for that recipient.");
        }
    }

    /**
     * Deletes a sent message permanently by matching its hash.
     *
     * @param hash the message hash to delete
     */
    public static void deleteByMessageHash(String hash) {
        for (int i = 0; i < sentCount; i++) {
            if (sentMessages[i] != null
                    && sentMessages[i].getMessageHash().equals(hash)) {
                System.out.println("Message \"" + sentMessages[i].getMessage()
                        + "\" successfully deleted.");
                removeSentMessageAtIndex(i);
                return;
            }
        }
        System.out.println("Message hash not found.");
    }

    /**
     * Prints a full formatted report of every message in the sent array.
     */
    public static void displayReport() {
        if (sentCount == 0) {
            System.out.println("No messages have been sent yet.");
            return;
        }

        System.out.println("\n===========================================");
        System.out.println("         Full Sent Messages Report         ");
        System.out.println("Total Sent: " + sentCount);
        System.out.println("===========================================");

        for (int i = 0; i < sentCount; i++) {
            Message msg = sentMessages[i];
            if (msg != null) {
                System.out.println("Message #" + (i + 1));
                System.out.println(msg.printDetails());
                System.out.println("-------------------------------------------");
            }
        }
    }

    // ---------------------------------------------------------------
    // Disregard (move from sent to disregarded)
    // ---------------------------------------------------------------

    /**
     * Prompts the user for a message ID, then moves that message from the
     * sent array to the disregarded array.
     *
     * @param scanner shared Scanner instance
     */
    public static void disregardMessage(Scanner scanner) {
        if (sentCount == 0) {
            System.out.println("No sent messages to disregard.");
            return;
        }

        System.out.print("Enter the Message ID to disregard: ");
        String id = scanner.nextLine().trim();

        for (int i = 0; i < sentCount; i++) {
            if (sentMessages[i] != null
                    && sentMessages[i].getMessageID().equals(id)) {

                if (discardCount < disregardedMessages.length) {
                    disregardedMessages[discardCount++] = sentMessages[i];
                    System.out.println("Message " + id + " moved to disregarded.");
                    removeSentMessageAtIndex(i);
                } else {
                    System.out.println("Disregarded array is full.");
                }
                return;
            }
        }
        System.out.println("Message ID not found in sent messages.");
    }

    /**
     * Removes the message at the given index from sentMessages and compacts
     * the array so there are no gaps.
     *
     * @param index position to remove
     */
    public static void removeSentMessageAtIndex(int index) {
        // Shift every element left by one from the removal point
        for (int j = index; j < sentCount - 1; j++) {
            sentMessages[j]  = sentMessages[j + 1];
            messageHashes[j] = messageHashes[j + 1];
            messageIDs[j]    = messageIDs[j + 1];
        }
        // Null out the last slot that is now a duplicate
        sentMessages[--sentCount]  = null;
        messageHashes[sentCount]   = null;
        messageIDs[sentCount]      = null;
    }

    // ---------------------------------------------------------------
    // Demo data for markers
    // ---------------------------------------------------------------

    /**
     * Pre-loads 5 canned messages so markers can test all reports
     * without typing messages manually.
     * 2 sent, 1 disregarded, 2 stored.
     */
    public static void populateTestMessages() {
        Message msg1 = new Message("+27834557896", "Hi Mike, can you join us later?", sentCount);
        Message msg2 = new Message("+27831231234", "It is dinner time!", sentCount + 1);
        Message msg3 = new Message("+27831110000", "Yohoooo, I am at your gate.", sentCount + 2);
        Message msg4 = new Message("+27832221111", "Fine. I'll meet you there.", sentCount + 3);
        Message msg5 = new Message("+27839998888", "Ok, I am leaving without you.", sentCount + 4);

        // Add msg1 and msg2 to sent
        sentMessages[sentCount]  = msg1;
        messageHashes[sentCount] = msg1.getMessageHash();
        messageIDs[sentCount]    = msg1.getMessageID();
        sentCount++;

        sentMessages[sentCount]  = msg2;
        messageHashes[sentCount] = msg2.getMessageHash();
        messageIDs[sentCount]    = msg2.getMessageID();
        sentCount++;

        // msg3 goes to disregarded
        disregardedMessages[discardCount++] = msg3;

        // msg4 and msg5 go to stored
        storedMessages[storeCount++] = msg4;
        storedMessages[storeCount++] = msg5;

        System.out.println("5 test messages pre-loaded: 2 sent, 1 discarded, 2 stored.");
    }

    // ---------------------------------------------------------------
    // Test reset (used by JUnit only — NOT called in production)
    // ---------------------------------------------------------------

    /**
     * Resets all arrays and counters to a clean state for JUnit tests.
     * Do not call this in production code.
     */
    static void resetForUnitTests() {
        // Title   : Arrays.fill() Array Reset
        // Author  : Oracle Arrays API
        // Source  : https://docs.oracle.com/javase/8/docs/api/java/util/Arrays.html
        Arrays.fill(sentMessages,        null);
        Arrays.fill(storedMessages,      null);
        Arrays.fill(disregardedMessages, null);
        Arrays.fill(messageHashes,       null);
        Arrays.fill(messageIDs,          null);
        sentCount = storeCount = discardCount = 0;
    }
}

// ───────────────────────── CODE ATTRIBUTION ─────────────────────────
// Title   : Arrays.fill() Array Reset
// Author  : Oracle Arrays API; W3Schools
// Date    : 17 Jun 2025
// Version : 1.0
// Sources :
//   https://docs.oracle.com/javase/8/docs/api/java/util/Arrays.html
//   https://www.w3schools.com/java/ref_arrays_fill.asp
//
// Title   : StringBuilder for Efficient Concatenation
// Author  : Oracle Docs
// Date    : 17 Jun 2025
// Version : 1.0
// Source  : https://docs.oracle.com/javase/8/docs/api/java/lang/StringBuilder.html
//
// Title   : Scanner Console Input
// Author  : Oracle Java SE 8 API
// Date    : 17 Jun 2025
// Version : 1.0
// Source  : https://docs.oracle.com/javase/8/docs/api/java/util/Scanner.html
//
// Title   : File.exists() Persistence Check
// Author  : Stack Overflow Q/1816673; GeeksforGeeks
// Date    : 17 Jun 2025
// Version : 1.0
// Sources :
//   https://stackoverflow.com/questions/1816673/
//   https://www.geeksforgeeks.org/java/file-exists-method-in-java-with-examples/
//
// Title   : Arrays in Java
// Author  : GeeksforGeeks
// Date    : 17 Jun 2025
// Version : 1.0
// Source  : https://www.geeksforgeeks.org/arrays-in-java/
//
// Title   : PROG5121 Lecture Slides
// Author  : The IIE / Rochelle Moodley (internal, unpublished)
// Date    : 2025
// ────────────────────────────────────────────────────────────────────