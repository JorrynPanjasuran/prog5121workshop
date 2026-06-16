/*
 * PROG5121 – Part 3 POE
 * Domain object representing a single QuickChat message.
 *
 * Author: Jorryn Panjasuran
 * Date: 2025
 */

package com.mycompany.chatapp;

// Title   : Random ID & StringBuilder Pattern
// Author  : Oracle Java SE 8 API
// Date    : 17 Jun 2025
// Version : 1.0
// Sources :
//   https://docs.oracle.com/javase/8/docs/api/java/util/Random.html
//   https://docs.oracle.com/javase/8/docs/api/java/lang/StringBuilder.html

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
import java.util.*;

/**
 * Message – stores one QuickChat message, validates it, and handles JSON persistence.
 * <p>
 * Responsibilities:
 * - Generates a unique 10-digit message ID
 * - Builds a hash from the ID, message number, and first/last word
 * - Validates recipient number and message length
 * - Appends itself to messages.json (one JSON object per line)
 * - Reads messages.json back into a List on startup
 *
 * @author Jorryn Panjasuran 2025
 */
public class Message {

    // ---------------------------------------------------------------
    // Instance fields
    // ---------------------------------------------------------------

    private String messageID;      // 10-digit random string
    private String recipient;      // +27XXXXXXXXX format
    private String message;        // body text (max 250 chars)
    private String messageHash;    // first2ofID:msgNum:FirstWordLastWord (uppercase)
    private String messageType;    // "sent" | "stored" | "disregarded"

    // Status flags
    private boolean isSent;
    private boolean isReceived;
    private boolean isRead;

    // Counts how many messages have been sent this session across all instances
    private static int totalMessages = 0;

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    /**
     * Creates a new Message and generates its ID and hash automatically.
     *
     * @param recipient     recipient phone number in +27XXXXXXXXX format
     * @param message       body text
     * @param messageNumber zero-based position in the current send batch (used for hash)
     */
    public Message(String recipient, String message, int messageNumber) {
        this.messageID   = generateMessageID();
        this.recipient   = recipient;
        this.message     = message;
        this.messageHash = createMessageHash(this.messageID, messageNumber, message);
        this.messageType = "sent";   // default; caller can change with setMessageType()
        this.isSent      = true;
        this.isReceived  = true;
        this.isRead      = true;
    }

    // ---------------------------------------------------------------
    // Static validation methods (called before object is constructed)
    // ---------------------------------------------------------------

    /**
     * Generates a random 10-digit numeric string used as the message ID.
     *
     * @return 10-digit numeric string
     */
    public static String generateMessageID() {
        // Title   : Random-ID & StringBuilder Pattern
        // Author  : Oracle Java SE 8 API
        // Source  : https://docs.oracle.com/javase/8/docs/api/java/util/Random.html
        Random rand = new Random();
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            id.append(rand.nextInt(10));
        }
        return id.toString();
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
     * Validates that the recipient starts with '+' and is 11–13 characters long
     * (covers +27XXXXXXXXX and similar E.164 numbers).
     *
     * @param number phone number string
     * @return true if valid
     */
    public static boolean checkRecipientCell(String number) {
        // Title   : SA +27 Cell-Number Regex
        // Author  : Stack Overflow Q/33477950
        // Source  : https://stackoverflow.com/questions/33477950/
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
             + ", please reduce size.";
    }

    /**
     * Builds a simple hash: first 2 chars of ID + ":" + message number + ":"
     * + first word + last word — all uppercase.
     * Example: "AB:0:HIHELLO"
     *
     * @param id     the message ID
     * @param msgNum zero-based message index
     * @param msg    body text
     * @return uppercase hash string
     */
    public static String createMessageHash(String id, int msgNum, String msg) {
        // Split on whitespace to extract first and last word
        String[] words = msg.trim().split("\\s+");
        String first = words.length > 0
                ? words[0].replaceAll("[^a-zA-Z0-9]", "") : "NA";
        String last  = words.length > 1
                ? words[words.length - 1].replaceAll("[^a-zA-Z0-9]", "") : "NA";
        return (id.substring(0, 2) + ":" + msgNum + ":" + first + last).toUpperCase();
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
            case "send" -> {
                markAsSent();
                totalMessages++;
                return "Message successfully sent.";
            }
            case "discard" -> { return "Press 0 to delete message."; }
            case "store"   -> {
                storeMessageToJson();
                return "Message successfully stored.";
            }
            default -> { return "Invalid option."; }
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
    // JSON persistence
    // ---------------------------------------------------------------

    /**
     * Appends this message as a single-line JSON object to messages.json.
     * Each line is one complete JSON object (newline-delimited JSON).
     */
    public void storeMessageToJson() {
        // Title   : BufferedWriter + FileWriter Append Pattern
        // Author  : DigitalOcean Tutorial
        // Source  : https://www.digitalocean.com/community/tutorials/java-append-to-file
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter("messages.json", true))) {

            // Manually build JSON (no external library needed for POE)
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
     * as a list. Uses simple string parsing — no external JSON library.
     *
     * @param fileName path to the JSON file
     * @return list of reconstructed Message objects (may be empty)
     */
    public static List<Message> readMessagesFromFile(String fileName) {
        // Title   : BufferedReader Line-by-Line File Read
        // Author  : DigitalOcean Tutorial
        // Source  : https://www.digitalocean.com/community/tutorials/java-read-file-line-by-line
        List<Message> messages = new ArrayList<>();
        File file = new File(fileName);

        if (!file.exists()) {
            return messages;  // nothing saved yet — return empty list
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String hash      = extractJsonField(line, "messageHash");
                String recipient = extractJsonField(line, "recipient");
                String msgBody   = extractJsonField(line, "message");
                String type      = extractJsonField(line, "messageType");

                // Reconstruct Message object and restore original hash & type
                Message m = new Message(recipient, msgBody, messages.size());
                m.messageHash = hash;
                m.setMessageType(type);
                messages.add(m);
            }
        } catch (IOException e) {
            System.out.println("Error reading messages from file: " + e.getMessage());
        }

        return messages;
    }

    /**
     * Extracts the value of a named field from a simple JSON string.
     * Only works for flat (non-nested) JSON objects.
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
// Title   : Random-ID & StringBuilder Pattern
// Author  : Oracle Java SE 8 API
// Date    : 17 Jun 2025
// Version : 1.0
// Sources :
//   https://docs.oracle.com/javase/8/docs/api/java/util/Random.html
//   https://docs.oracle.com/javase/8/docs/api/java/lang/StringBuilder.html
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
// Title   : File.exists() for Persistence Check
// Author  : Stack Overflow Q/1816673; GeeksforGeeks
// Date    : 17 Jun 2025
// Version : 1.0
// Sources :
//   https://stackoverflow.com/questions/1816673/
//   https://www.geeksforgeeks.org/java/file-exists-method-in-java-with-examples/
//
// Title   : PROG5121 Lecture Slides
// Author  : The IIE / Rochelle Moodley (internal, unpublished)
// Date    : 2025
// ────────────────────────────────────────────────────────────────────