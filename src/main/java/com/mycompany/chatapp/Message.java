/*
 * PROG5121 – Part 3 POE
 * Domain object representing a single QuickChat message.
 *
 * Author: Jorryn Panjasuran
 * Date: 2025
 */

package com.mycompany.chatapp;

// Title   : Random class for ID generation
// Author  : Oracle Java SE 8 API
// Date    : 17 Jun 2025
// Version : 1.0
// Source  : https://docs.oracle.com/javase/8/docs/api/java/util/Random.html

// Title   : BufferedWriter + FileWriter Append Pattern
// Author  : DigitalOcean Tutorial "Java append to file"
// Date    : 17 Jun 2025
// Version : 1.0
// Source  : https://www.digitalocean.com/community/tutorials/java-append-to-file

// Title   : BufferedReader Line-by-Line File Read
// Author  : DigitalOcean Tutorial "Java Read File"
// Date    : 17 Jun 2025
// Version : 1.0
// Source  : https://www.digitalocean.com/community/tutorials/java-read-file-line-by-line

import java.io.*;
import java.util.Random;

/**
 * Message – stores one QuickChat message, validates it, and handles JSON persistence.
 *
 * Responsibilities:
 * - Generates a unique 10-digit message ID
 * - Builds a hash from the ID, message number, and first/last word
 * - Validates recipient number and message length
 * - Appends itself to messages.json (one JSON object per line)
 * - Reads messages.json back into a Message array on startup
 *
 * @author Jorryn Panjasuran 2025
 */
public class Message {

    // ---------------------------------------------------------------
    // Instance fields
    // ---------------------------------------------------------------

    private String messageID;      // 10-digit random string
    private String recipient;      // e.g. +27XXXXXXXXX format
    private String message;        // body text (max 250 chars)
    private String messageHash;    // first2ofID:msgNum:FirstWordLastWord (uppercase)
    private String messageType;    // "sent" | "stored" | "disregarded"

    // Status flags as required by the chat app payload specification
    private boolean isSent;
    private boolean isReceived;
    private boolean isRead;

    // Tracks how many messages have been sent this session across all instances
    private static int totalMessages = 0;

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /**
     * Creates a new Message and generates its ID and hash automatically.
     *
     * @param recipient     recipient phone number
     * @param message       body text
     * @param messageNumber zero-based position in the current send batch (used for hash)
     */
    public Message(String recipient, String message, int messageNumber) {
        this.messageID   = generateMessageID();
        this.recipient   = recipient;
        this.message     = message;
        this.messageHash = createMessageHash(this.messageID, messageNumber, message);
        this.messageType = "sent";
        this.isSent      = true;
        this.isReceived  = true;
        this.isRead      = true;
    }

    // ---------------------------------------------------------------
    // Static validation methods
    // ---------------------------------------------------------------

    /**
     * Generates a random 10-digit numeric string used as the message ID.
     * Uses String concatenation in a loop (no StringBuilder needed).
     *
     * @return 10-digit numeric string
     */
    public static String generateMessageID() {
        // Title   : Random class for ID generation
        // Author  : Oracle Java SE 8 API
        // Source  : https://docs.oracle.com/javase/8/docs/api/java/util/Random.html
        Random rand = new Random();
        String id = "";
        for (int i = 0; i < 10; i++) {
            id = id + rand.nextInt(10);
        }
        return id;
    }

    /**
     * Checks that a message ID is non-empty and not longer than 10 characters.
     *
     * @param id the ID to check
     * @return true if valid
     */
    public static boolean checkMessageID(String id) {
        return id != null && !id.isEmpty() && id.length() <= 10;
    }

    /**
     * Validates that the recipient starts with '+' and is 11-13 characters long
     * (covers international E.164 numbers such as +27XXXXXXXXX).
     *
     * Title   : SA +27 Cell-Number Regex
     * Author  : Stack Overflow Q/33477950
     * Source  : https://stackoverflow.com/questions/33477950/
     *
     * @param number phone number string
     * @return true if valid
     */
    public static boolean checkRecipientCell(String number) {
        return number != null
                && number.startsWith("+")
                && number.length() >= 11
                && number.length() <= 13;
    }

    /**
     * Checks message length against the 250-character limit.
     *
     * @param msg body text to check
     * @return "Message ready to send." or an error stating how many chars over the limit
     */
    public static String validateMessageLength(String msg) {
        if (msg.length() <= 250) {
            return "Message ready to send.";
        }
        return "Message exceeds 250 characters by " + (msg.length() - 250)
             + "; please reduce the size.";
    }

    /**
     * Builds a hash: first 2 chars of ID + ":" + message number + ":"
     * + first word + last word — all uppercase.
     * Example: ID="1234567890", msgNum=0, msg="Hi Thanks" → "12:0:HITHANKS"
     *
     * @param id     the message ID
     * @param msgNum zero-based message index
     * @param msg    body text
     * @return uppercase hash string
     */
    public static String createMessageHash(String id, int msgNum, String msg) {
        String[] words = msg.trim().split("\\s+");
        String first = words.length > 0
                ? words[0].replaceAll("[^a-zA-Z0-9]", "") : "NA";
        String last  = words.length > 1
                ? words[words.length - 1].replaceAll("[^a-zA-Z0-9]", "") : "NA";
        String hash = id.substring(0, 2) + ":" + msgNum + ":" + first + last;
        return hash.toUpperCase();
    }

    // ---------------------------------------------------------------
    // Send / route the message
    // ---------------------------------------------------------------

    /**
     * Routes the message based on the user's choice and updates counters.
     *
     * @param choice "send", "discard", or "store"
     * @return status message for display
     */
    public String sendOptions(String choice) {
        switch (choice.toLowerCase()) {
            case "send":
                markAsSent();
                totalMessages++;
                return "Message successfully sent.";
            case "discard":
                return "Press 0 to delete the message.";
            case "store":
                storeMessageToJson();
                return "Message successfully stored.";
            default:
                return "Invalid option.";
        }
    }

    /**
     * Returns a formatted summary of all message fields (used in reports).
     *
     * @return multi-line string with all message details
     */
    public String printDetails() {
        return "Message ID  : " + messageID
             + "\nHash        : " + messageHash
             + "\nRecipient   : " + recipient
             + "\nMessage     : " + message
             + "\nSent        : " + isSent
             + "\nReceived    : " + isReceived
             + "\nRead        : " + isRead;
    }

    /**
     * Returns how many messages have been sent in this session.
     *
     * @return count of sent messages
     */
    public static int returnTotalMessages() {
        return totalMessages;
    }

    // ---------------------------------------------------------------
    // JSON persistence (research feature – attributed below)
    // ---------------------------------------------------------------

    /**
     * Appends this message as a single-line JSON object to messages.json.
     * Each line is one complete JSON object (newline-delimited JSON).
     *
     * Title   : BufferedWriter + FileWriter Append Pattern
     * Author  : DigitalOcean Tutorial
     * Source  : https://www.digitalocean.com/community/tutorials/java-append-to-file
     */
    public void storeMessageToJson() {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter("messages.json", true))) {

            String json = "{"
                    + "\"messageHash\":\""  + messageHash.replace("\"", "\\\"") + "\","
                    + "\"recipient\":\""    + recipient.replace("\"", "\\\"")   + "\","
                    + "\"message\":\""      + message.replace("\"", "\\\"")     + "\","
                    + "\"messageType\":\""  + messageType                       + "\""
                    + "}";
            writer.write(json);
            writer.newLine();

        } catch (IOException e) {
            System.out.println("Error saving message: " + e.getMessage());
        }
    }

    /**
     * Reads all messages from a newline-delimited JSON file and returns them
     * as a plain array (max 100 entries). Uses simple string parsing.
     *
     * Title   : BufferedReader Line-by-Line File Read
     * Author  : DigitalOcean Tutorial
     * Source  : https://www.digitalocean.com/community/tutorials/java-read-file-line-by-line
     *
     * @param fileName path to the JSON file
     * @return Message array; slots beyond the loaded count are null
     */
    public static Message[] readMessagesFromFile(String fileName) {
        Message[] messages = new Message[100];
        int count = 0;
        File file = new File(fileName);

        if (!file.exists()) {
            return messages;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null && count < messages.length) {
                String hash      = extractJsonField(line, "messageHash");
                String recipient = extractJsonField(line, "recipient");
                String msgBody   = extractJsonField(line, "message");
                String type      = extractJsonField(line, "messageType");

                Message m = new Message(recipient, msgBody, count);
                m.messageHash = hash;
                m.setMessageType(type);
                messages[count] = m;
                count++;
            }
        } catch (IOException e) {
            System.out.println("Error reading messages from file: " + e.getMessage());
        }

        return messages;
    }

    /**
     * Extracts the value of a named field from a simple flat JSON string.
     *
     * @param json  the JSON line to parse
     * @param field the field name to find
     * @return the string value, or empty string if not found
     */
    private static String extractJsonField(String json, String field) {
        String search = "\"" + field + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) return "";
        start += search.length();
        int end = json.indexOf("\"", start);
        return end > start ? json.substring(start, end) : "";
    }

    // ---------------------------------------------------------------
    // Getters, setters, and flag methods
    // ---------------------------------------------------------------

    public String  getMessageType() { return messageType; }
    public void    setMessageType(String type) { this.messageType = type; }

    public String  getMessageID()   { return messageID;   }
    public String  getRecipient()   { return recipient;   }
    public String  getMessage()     { return message;     }
    public String  getMessageHash() { return messageHash; }

    public boolean isSent()         { return isSent;      }
    public boolean isReceived()     { return isReceived;  }
    public boolean isRead()         { return isRead;      }

    /** Marks the message as sent. */
    public void markAsSent()     { this.isSent     = true; }

    /** Marks the message as received. */
    public void markAsReceived() { this.isReceived = true; }

    /** Marks the message as read. */
    public void markAsRead()     { this.isRead     = true; }
}

// ───────────────────────── CODE ATTRIBUTION ─────────────────────────
// Title   : Random class for ID generation
// Author  : Oracle Java SE 8 API
// Date    : 17 Jun 2025
// Version : 1.0
// Source  : https://docs.oracle.com/javase/8/docs/api/java/util/Random.html
//
// Title   : BufferedWriter + FileWriter Append Pattern
// Author  : DigitalOcean Tutorial "Java append to file"
// Date    : 17 Jun 2025
// Version : 1.0
// Source  : https://www.digitalocean.com/community/tutorials/java-append-to-file
//
// Title   : BufferedReader Line-by-Line File Read
// Author  : DigitalOcean Tutorial "Java Read File"
// Date    : 17 Jun 2025
// Version : 1.0
// Source  : https://www.digitalocean.com/community/tutorials/java-read-file-line-by-line
//
// Title   : SA (+27) Cell-Number Regex
// Author  : Stack Overflow Q/33477950
// Date    : 17 Jun 2025
// Version : 1.0
// Source  : https://stackoverflow.com/questions/33477950/
//
// Title   : PROG5121 Lecture Slides
// Author  : The IIE / Rochelle Moodley (internal, unpublished)
// Date    : 2025
// ────────────────────────────────────────────────────────────────────
