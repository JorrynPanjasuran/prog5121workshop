/*
 * PROG5121 – Part 3 POE
 * I wrote this class to manage all message arrays, JSON loading, sending,
 * reports, and deletes for the SwiftDeliver application.
 *
 * Author: ST10448822
 * Date: 2025
 */

package com.mycompany.chatapp;

// I use Scanner to read menu choices inside the stored-messages sub-menu.
// Title   : Scanner Console Input
// Author  : Oracle Java SE Docs
// Date    : 23 Jun 2025
// Version : 1.0
// Source  : https://docs.oracle.com/javase/8/docs/api/java/util/Scanner.html

// I chose fixed-size plain Java arrays to store messages without any extra libraries.
// Title   : Arrays in Java
// Author  : GeeksforGeeks
// Date    : 23 Jun 2025
// Source  : https://www.geeksforgeeks.org/arrays-in-java/

import java.io.File;
import java.util.Scanner;

/**
 * MessageManager – I own the five message arrays (sent, stored, disregarded,
 * messageHashes, messageIDs) and all operations: sending, storing, searching,
 * deleting, and reporting.
 *
 * @author ST10448822 2025
 */
public class MessageManager {

    // ---------------------------------------------------------------
    // In-memory storage arrays (fixed-size plain Java arrays)
    // ---------------------------------------------------------------

    // Title   : Arrays in Java
    // Author  : GeeksforGeeks
    // Source  : https://www.geeksforgeeks.org/arrays-in-java/

    /** I hold all messages that were sent in this session here. */
    static Message[] sentMessages        = new Message[100];

    /** I hold messages the user chose to disregard here. */
    static Message[] disregardedMessages = new Message[100];

    /** I hold messages the user chose to store for later here. */
    static Message[] storedMessages      = new Message[100];

    /** I keep parallel arrays for fast hash/ID lookups. */
    static String[]  messageHashes       = new String[100];
    static String[]  messageIDs          = new String[100];

    // I track how many slots in each array are actually used.
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
     * I prompt the user for a recipient and message body, validate input,
     * then route the message to sent/stored/disregarded based on the choice.
     *
     * @param scanner shared Scanner instance
     * @param msgNum  zero-based index in the current send batch (used for hash)
     * @return true if the message was successfully placed in any array
     */
    public static boolean sendMessage(Scanner scanner, int msgNum) {

        System.out.print("Enter recipient phone number (e.g. +27761234567): ");
        String recipient = scanner.nextLine().trim();

        if (!Message.checkRecipientCell(recipient)) {
            System.out.println("Cell phone number is incorrectly formatted or does not contain international code.");
            return false;
        }

        System.out.print("Enter your message (max 250 characters): ");
        String content = scanner.nextLine();

        String feedback = Message.validateMessageLength(content);
        System.out.println(feedback);

        if (!"Message ready to send.".equals(feedback)) {
            return false;
        }

        Message msg = new Message(recipient, content, msgNum);
        System.out.println("Message ID  : " + msg.getMessageID());
        System.out.println("Message Hash: " + msg.getMessageHash());

        System.out.println("What would you like to do with this message?");
        System.out.println("  1) Dispatch");
        System.out.println("  2) Cancel");
        System.out.println("  3) Save");
        System.out.print("Choice: ");
        String action = scanner.nextLine().trim();

        switch (action) {
            case "1":
                msg.setMessageType("sent");
                msg.storeMessageToJson();
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

            case "2":
                msg.setMessageType("disregarded");
                msg.storeMessageToJson();
                if (discardCount < disregardedMessages.length) {
                    disregardedMessages[discardCount] = msg;
                    discardCount++;
                    System.out.println("Press 0 to delete the message.");
                    return true;
                }
                System.out.println("Disregarded array is full.");
                return false;

            case "3":
                msg.setMessageType("stored");
                msg.storeMessageToJson();
                if (storeCount < storedMessages.length) {
                    storedMessages[storeCount] = msg;
                    storeCount++;
                    System.out.println("Message successfully stored.");
                    return true;
                }
                System.out.println("Stored message array is full.");
                return false;

            default:
                System.out.println("Invalid option — message not saved.");
                return false;
        }
    }

    // ---------------------------------------------------------------
    // JSON hydration on startup
    // ---------------------------------------------------------------

    /**
     * I read all messages from messages.json at application startup and
     * distribute them into the correct in-memory arrays.
     *
     * Title   : File.exists() for Persistence Check
     * Author  : Stack Overflow Q/1816673
     * Source  : https://stackoverflow.com/questions/1816673/
     */
    public static void loadMessagesFromFile() {
        File file = new File("messages.json");
        if (!file.exists()) {
            System.out.println("No saved messages found. Starting fresh.");
            return;
        }

        Message[] loaded = Message.readMessagesFromFile("messages.json");

        for (int i = 0; i < loaded.length; i++) {
            if (loaded[i] == null) {
                break;
            }
            String type = loaded[i].getMessageType().toLowerCase();
            if ("sent".equals(type)) {
                if (sentCount < sentMessages.length) {
                    sentMessages[sentCount]  = loaded[i];
                    messageHashes[sentCount] = loaded[i].getMessageHash();
                    messageIDs[sentCount]    = loaded[i].getMessageID();
                    sentCount++;
                }
            } else if ("stored".equals(type)) {
                if (storeCount < storedMessages.length) {
                    storedMessages[storeCount] = loaded[i];
                    storeCount++;
                }
            } else if ("disregarded".equals(type)) {
                if (discardCount < disregardedMessages.length) {
                    disregardedMessages[discardCount] = loaded[i];
                    discardCount++;
                }
            }
        }
        System.out.println("Messages loaded from file successfully.");
    }

    /**
     * I load only "stored" messages from file after login.
     */
    public static void loadStoredMessagesFromJson() {
        Message[] loaded = Message.readMessagesFromFile("messages.json");

        for (int i = 0; i < loaded.length; i++) {
            if (loaded[i] == null) {
                break;
            }
            if ("stored".equalsIgnoreCase(loaded[i].getMessageType())
                    && storeCount < storedMessages.length) {
                storedMessages[storeCount] = loaded[i];
                storeCount++;
            }
        }
    }

    // ---------------------------------------------------------------
    // Stored Messages sub-menu (Main Menu Option 4)
    // ---------------------------------------------------------------

    /**
     * I display the Stored Messages sub-menu and route to the selected feature.
     * All features in this menu operate on the stored messages array.
     *
     * @param scanner shared Scanner instance
     */
    public static void showStoredMessagesMenu(Scanner scanner) {
        System.out.println("\n===========================================");
        System.out.println("           Saved Messages Menu             ");
        System.out.println("===========================================");
        System.out.println("a) View Senders and Recipients");
        System.out.println("b) Longest Saved Message");
        System.out.println("c) Find by Message ID");
        System.out.println("d) Find by Recipient");
        System.out.println("e) Remove by Message Hash");
        System.out.println("f) Full Saved Messages Report");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine().trim().toLowerCase();

        switch (choice) {
            case "a":
                showSenderAndRecipients();
                break;
            case "b":
                showLongestMessage();
                break;
            case "c":
                System.out.print("Enter Message ID to search: ");
                String id = scanner.nextLine().trim();
                String found = searchByMessageID(id);
                System.out.println(found);
                break;
            case "d":
                System.out.print("Enter recipient number to search: ");
                String recip = scanner.nextLine().trim();
                String results = searchByRecipient(recip);
                System.out.println(results);
                break;
            case "e":
                System.out.print("Enter Message Hash to delete: ");
                String hash = scanner.nextLine().trim();
                String deleteResult = deleteByMessageHash(hash);
                System.out.println(deleteResult);
                break;
            case "f":
                displayStoredReport();
                break;
            default:
                System.out.println("Invalid option.");
                break;
        }
    }

    // ---------------------------------------------------------------
    // Individual report / search methods
    // ---------------------------------------------------------------

    /**
     * I print all stored messages showing their recipient and message body.
     * Requirement: a) Display the sender and recipient of all stored messages.
     */
    public static void showSenderAndRecipients() {
        if (storeCount == 0) {
            System.out.println("No stored messages to display.");
            return;
        }
        System.out.println("\n--- Stored Messages (Sender & Recipient) ---");
        for (int i = 0; i < storeCount; i++) {
            if (storedMessages[i] != null) {
                System.out.println("Recipient : " + storedMessages[i].getRecipient());
                System.out.println("Message   : " + storedMessages[i].getMessage());
                System.out.println("---");
            }
        }
    }

    /**
     * I find the body text of the longest message in the stored array.
     * I return the string so it can be used in unit tests.
     *
     * @return the body text of the longest stored message, or empty string if none
     */
    public static String getLongestStoredMessage() {
        String longest = "";
        for (int i = 0; i < storeCount; i++) {
            if (storedMessages[i] != null
                    && storedMessages[i].getMessage().length() > longest.length()) {
                longest = storedMessages[i].getMessage();
            }
        }
        return longest;
    }

    /**
     * I print the longest stored message.
     * Requirement: b) Display the longest stored message.
     */
    public static void showLongestMessage() {
        String longest = getLongestStoredMessage();
        if (longest.isEmpty()) {
            System.out.println("No stored messages found.");
        } else {
            System.out.println("\n--- Longest Stored Message ---");
            System.out.println(longest);
        }
    }

    /**
     * I search the sent and stored arrays for a message matching the given ID.
     * I return the message body for unit testing and also print to console.
     *
     * Requirement: c) Search for a message ID and display the corresponding recipient and message.
     *
     * @param id the message ID to find
     * @return the message body if found, otherwise a not-found message
     */
    public static String searchByMessageID(String id) {
        for (int i = 0; i < sentCount; i++) {
            if (sentMessages[i] != null && sentMessages[i].getMessageID().equals(id)) {
                System.out.println("Recipient : " + sentMessages[i].getRecipient());
                System.out.println("Message   : " + sentMessages[i].getMessage());
                return sentMessages[i].getMessage();
            }
        }
        for (int i = 0; i < storeCount; i++) {
            if (storedMessages[i] != null && storedMessages[i].getMessageID().equals(id)) {
                System.out.println("Recipient : " + storedMessages[i].getRecipient());
                System.out.println("Message   : " + storedMessages[i].getMessage());
                return storedMessages[i].getMessage();
            }
        }
        System.out.println("Message ID not found.");
        return "Message ID not found.";
    }

    /**
     * I search the sent and stored arrays for all messages to the given recipient.
     * I return all matching messages as a single string for unit testing.
     *
     * Requirement: d) Search for all the messages stored for a particular recipient.
     *
     * @param recipient phone number to search for
     * @return newline-separated message bodies, or a not-found message
     */
    public static String searchByRecipient(String recipient) {
        String result = "";
        System.out.println("\n--- Messages to " + recipient + " ---");

        for (int i = 0; i < sentCount; i++) {
            if (sentMessages[i] != null
                    && sentMessages[i].getRecipient().equals(recipient)) {
                System.out.println("[Sent]   " + sentMessages[i].getMessage());
                result = result + sentMessages[i].getMessage() + "\n";
            }
        }

        for (int i = 0; i < storeCount; i++) {
            if (storedMessages[i] != null
                    && storedMessages[i].getRecipient().equals(recipient)) {
                System.out.println("[Stored] " + storedMessages[i].getMessage());
                result = result + storedMessages[i].getMessage() + "\n";
            }
        }

        if (result.isEmpty()) {
            System.out.println("No messages found for that recipient.");
            return "No messages found for that recipient.";
        }
        return result.trim();
    }

    /**
     * I delete a stored message by matching its hash.
     * I return a confirmation string for unit testing.
     *
     * Requirement: e) Delete a message using the message hash.
     *
     * @param hash the message hash to delete
     * @return confirmation message or not-found message
     */
    public static String deleteByMessageHash(String hash) {
        for (int i = 0; i < storeCount; i++) {
            if (storedMessages[i] != null
                    && storedMessages[i].getMessageHash().equals(hash)) {
                String body = storedMessages[i].getMessage();
                removeStoredMessageAtIndex(i);
                return "Message: \"" + body + "\" successfully deleted.";
            }
        }
        return "Message hash not found.";
    }

    /**
     * I print a full report of all stored messages (hash, recipient, message).
     * Requirement: f) Display a report that lists the full details of all the stored messages.
     */
    public static void displayStoredReport() {
        if (storeCount == 0) {
            System.out.println("No stored messages.");
            return;
        }
        System.out.println("\n===========================================");
        System.out.println("        Stored Messages Full Report        ");
        System.out.println("Total Stored: " + storeCount);
        System.out.println("===========================================");

        for (int i = 0; i < storeCount; i++) {
            if (storedMessages[i] != null) {
                System.out.println("Message Hash: " + storedMessages[i].getMessageHash());
                System.out.println("Recipient   : " + storedMessages[i].getRecipient());
                System.out.println("Message     : " + storedMessages[i].getMessage());
                System.out.println("-------------------------------------------");
            }
        }
    }

    /**
     * I print a full report of all SENT messages.
     * Used for the Display Report unit test.
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
            if (sentMessages[i] != null) {
                System.out.println("Message Hash: " + sentMessages[i].getMessageHash());
                System.out.println("Recipient   : " + sentMessages[i].getRecipient());
                System.out.println("Message     : " + sentMessages[i].getMessage());
                System.out.println("-------------------------------------------");
            }
        }
    }

    // ---------------------------------------------------------------
    // Array removal helpers
    // ---------------------------------------------------------------

    /**
     * I remove the message at the given index from sentMessages and compact
     * the array so there are no gaps.
     *
     * @param index position to remove
     */
    public static void removeSentMessageAtIndex(int index) {
        for (int j = index; j < sentCount - 1; j++) {
            sentMessages[j]  = sentMessages[j + 1];
            messageHashes[j] = messageHashes[j + 1];
            messageIDs[j]    = messageIDs[j + 1];
        }
        sentCount--;
        sentMessages[sentCount]  = null;
        messageHashes[sentCount] = null;
        messageIDs[sentCount]    = null;
    }

    /**
     * I remove the message at the given index from storedMessages and compact
     * the array so there are no gaps.
     *
     * @param index position to remove
     */
    public static void removeStoredMessageAtIndex(int index) {
        for (int j = index; j < storeCount - 1; j++) {
            storedMessages[j] = storedMessages[j + 1];
        }
        storeCount--;
        storedMessages[storeCount] = null;
    }

    // ---------------------------------------------------------------
    // Demo data for markers – SwiftDeliver Part 3 test scenario
    // ---------------------------------------------------------------

    /**
     * I pre-load the 5 SwiftDeliver test messages specified in my Part 3 scenario.
     *
     * Message 1: Sent       – +27761234567  "Has the pizza left the kitchen yet?"
     * Message 2: Stored     – +27769876543  "Your order is stuck in traffic on the N1,
     *                                         estimated arrival is now 8:15 PM."
     * Message 3: Disregard  – +27761112233  "Oops, wrong order sent to table six."
     * Message 4: Sent       – 0769876543    "Rider is outside now!"
     * Message 5: Stored     – +27769876543  "Order confirmed and signed for, thanks!"
     */
    public static void populateTestMessages() {

        // Message 1 – Sent: kitchen manager asks if the pizza has left
        Message msg1 = new Message("+27761234567", "Has the pizza left the kitchen yet?", sentCount);
        msg1.setMessageType("sent");
        sentMessages[sentCount]  = msg1;
        messageHashes[sentCount] = msg1.getMessageHash();
        messageIDs[sentCount]    = msg1.getMessageID();
        sentCount++;

        // Message 2 – Stored: rider notifies customer about a traffic delay (longest message)
        Message msg2 = new Message("+27769876543",
                "Your order is stuck in traffic on the N1, estimated arrival is now 8:15 PM.", storeCount);
        msg2.setMessageType("stored");
        storedMessages[storeCount] = msg2;
        storeCount++;

        // Message 3 – Disregarded: wrong-order notification, no longer relevant
        Message msg3 = new Message("+27761112233", "Oops, wrong order sent to table six.", discardCount);
        msg3.setMessageType("disregarded");
        disregardedMessages[discardCount] = msg3;
        discardCount++;

        // Message 4 – Sent: rider confirms arrival (recipient uses local format as per scenario)
        Message msg4 = new Message("0769876543", "Rider is outside now!", sentCount);
        msg4.setMessageType("sent");
        sentMessages[sentCount]  = msg4;
        messageHashes[sentCount] = msg4.getMessageHash();
        messageIDs[sentCount]    = msg4.getMessageID();
        sentCount++;

        // Message 5 – Stored: customer confirms delivery was successful
        Message msg5 = new Message("+27769876543", "Order confirmed and signed for, thanks!", storeCount);
        msg5.setMessageType("stored");
        storedMessages[storeCount] = msg5;
        storeCount++;

        System.out.println("5 SwiftDeliver test messages pre-loaded: 2 sent, 1 discarded, 2 stored.");
    }

    // ---------------------------------------------------------------
    // Test reset (used by JUnit only — NOT called in production)
    // ---------------------------------------------------------------

    /**
     * I reset all arrays and counters to a clean state for JUnit tests.
     * I use manual loops so no external utility classes are needed.
     */
    static void resetForUnitTests() {
        for (int i = 0; i < sentMessages.length; i++) {
            sentMessages[i]        = null;
            messageHashes[i]       = null;
            messageIDs[i]          = null;
        }
        for (int i = 0; i < storedMessages.length; i++) {
            storedMessages[i]      = null;
        }
        for (int i = 0; i < disregardedMessages.length; i++) {
            disregardedMessages[i] = null;
        }
        sentCount    = 0;
        storeCount   = 0;
        discardCount = 0;
    }
}

// ───────────────────────── CODE ATTRIBUTION ─────────────────────────
// Title   : Scanner Console Input
// Author  : Oracle Java SE 8 API
// Date    : 23 Jun 2025
// Version : 1.0
// Source  : https://docs.oracle.com/javase/8/docs/api/java/util/Scanner.html
//
// Title   : Arrays in Java
// Author  : GeeksforGeeks
// Date    : 23 Jun 2025
// Version : 1.0
// Source  : https://www.geeksforgeeks.org/arrays-in-java/
//
// Title   : File.exists() Persistence Check
// Author  : Stack Overflow Q/1816673
// Date    : 23 Jun 2025
// Version : 1.0
// Source  : https://stackoverflow.com/questions/1816673/
//
// Title   : PROG5121 Lecture Slides
// Author  : The IIE / Rochelle Moodley (internal, unpublished)
// Date    : 2025
// ────────────────────────────────────────────────────────────────────
