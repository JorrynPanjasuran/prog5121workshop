/*
 * PROG5121 – Part 2 POE
 * I wrote this class to manage all message arrays, JSON loading, sending,
 * and reports for the SwiftDeliver application.
 *
 * Author: ST10448822
 * Date: 2025
 */

package com.mycompany.chatapp;

// I use Scanner to read menu choices from the console.
// Title   : Scanner Console Input
// Author  : Oracle Java SE Docs
// Date    : 2025
// Version : 1.0
// Source  : https://docs.oracle.com/javase/8/docs/api/java/util/Scanner.html

// I chose fixed-size plain Java arrays to store messages without any extra libraries.
// Title   : Arrays in Java
// Author  : GeeksforGeeks
// Date    : 2025
// Source  : https://www.geeksforgeeks.org/arrays-in-java/

import java.io.File;
import java.util.Scanner;

/**
 * MessageManager – I own the five message arrays (sentMessages, storedMessages,
 * disregardedMessages, messageHashes, messageIDs) and all Part 2 operations:
 * sending, storing, disregarding, and reporting.
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

        System.out.println("What would you like to do?");
        System.out.println("  1) Send");
        System.out.println("  2) Discard");
        System.out.println("  3) Store");
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

    // ---------------------------------------------------------------
    // Report and array removal helpers
    // ---------------------------------------------------------------

    /**
     * I print a full report of all sent messages (hash, recipient, message).
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

    // ---------------------------------------------------------------
    // Test reset (used by JUnit only – NOT called in production)
    // ---------------------------------------------------------------

    /**
     * I reset all arrays and counters to a clean state for JUnit tests.
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
// Date    : 2025
// Version : 1.0
// Source  : https://docs.oracle.com/javase/8/docs/api/java/util/Scanner.html
//
// Title   : Arrays in Java
// Author  : GeeksforGeeks
// Date    : 2025
// Version : 1.0
// Source  : https://www.geeksforgeeks.org/arrays-in-java/
//
// Title   : File.exists() Persistence Check
// Author  : Stack Overflow Q/1816673
// Date    : 2025
// Version : 1.0
// Source  : https://stackoverflow.com/questions/1816673/
//
// Title   : PROG5121 Lecture Slides
// Author  : The IIE / Rochelle Moodley (internal, unpublished)
// Date    : 2025
// ────────────────────────────────────────────────────────────────────
