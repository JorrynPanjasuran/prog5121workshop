/*
 * PROG5121 – Part 3 POE
 * Main entry point for the QuickChat application.
 * Uses Scanner for all user input (console-based).
 *
 * Author: Jorryn Panjasuran
 * Date: 2025
 */

package com.mycompany.chatapp;

// Title   : Scanner Console Input
// Author  : Oracle Java SE Docs
// Date    : 17 Jun 2025
// Version : 1.0
// Source  : https://docs.oracle.com/javase/8/docs/api/java/util/Scanner.html

import java.util.Scanner;

/**
 * ChatApp – launches registration, login, and the main message menu.
 *
 * @author Jorryn Panjasuran 2025
 */
public class ChatApp {

    public static void main(String[] args) {

        // Single shared Scanner for the whole application lifetime
        Scanner scanner = new Scanner(System.in);

        System.out.println("===========================================");
        System.out.println("   Welcome to QuickChat – Registration     ");
        System.out.println("===========================================");

        // Load any messages saved from a previous session
        MessageManager.loadMessagesFromFile();

        // --- Registration ---
        System.out.print("Enter your first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter your last name: ");
        String lastName = scanner.nextLine();

        // Validate username, password, cellphone via loop until correct format
        String username = getValidUsername(scanner);
        String password = getValidPassword(scanner);
        String cellphone = getValidCellphone(scanner);

        // Build and register the user
        Login user = new Login(username, password, cellphone, firstName, lastName);
        System.out.println("\n" + user.register());

        // --- Login loop ---
        boolean loggedIn = false;
        while (!loggedIn) {
            System.out.print("\nEnter username to login: ");
            String inputUser = scanner.nextLine();
            System.out.print("Enter password: ");
            String inputPass = scanner.nextLine();

            boolean success = user.login(inputUser, inputPass);
            System.out.println(user.loginStatusMessage(success));

            if (success) {
                loggedIn = true;
                // Load stored messages from file into memory
                MessageManager.loadStoredMessagesFromJson();
                // Pre-load 5 demo messages for the marker
                MessageManager.populateTestMessages();
                // Start main menu
                runApp(scanner);
            }
        }

        scanner.close();
    }

    // ---------------------------------------------------------------
    // Main application menu loop
    // ---------------------------------------------------------------

    /**
     * Displays the menu and routes to the correct feature until user quits.
     *
     * @param scanner shared Scanner instance
     */
    public static void runApp(Scanner scanner) {
        boolean running = true;

        while (running) {
            System.out.println("\n===========================================");
            System.out.println("           QuickChat Main Menu             ");
            System.out.println("===========================================");
            System.out.println("1) Send Message");
            System.out.println("2) Show Recently Sent Messages");
            System.out.println("3) Disregard a Message");
            System.out.println("4) Quit");
            System.out.println("5) Reports");
            System.out.print("Choose an option: ");

            String option = scanner.nextLine().trim();

            // Title   : Modern switch-arrow syntax
            // Author  : Oracle JEP 361
            // Date    : 17 Jun 2025
            // Source  : https://docs.oracle.com/en/java/javase/13/language/switch-expressions.html
            switch (option) {
                case "1" -> {
                    System.out.print("How many messages would you like to send? ");
                    int total;
                    try {
                        total = Integer.parseInt(scanner.nextLine().trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number. Returning to menu.");
                        break;
                    }
                    for (int i = 0; i < total; i++) {
                        System.out.println("\n--- Message " + (i + 1) + " of " + total + " ---");
                        if (!MessageManager.sendMessage(scanner, i)) {
                            System.out.println("Message not sent. Moving to next.");
                        }
                    }
                    System.out.println("Total messages sent so far: " + MessageManager.getSentCount());
                }
                case "2" -> {
                    if (MessageManager.getSentCount() == 0) {
                        System.out.println("No messages sent yet.");
                    } else {
                        MessageManager.displayReport();
                    }
                }
                case "3" -> MessageManager.disregardMessage(scanner);
                case "4" -> {
                    System.out.println("Goodbye! Exiting QuickChat.");
                    running = false;
                }
                case "5" -> MessageManager.showReports(scanner);
                default -> System.out.println("Invalid option. Please choose 1-5.");
            }
        }
    }

    // ---------------------------------------------------------------
    // Validation helpers — loop until valid input is given
    // ---------------------------------------------------------------

    // Title   : Username regex (contains _ and <= 5 chars)
    // Author  : Stack Overflow Q/336210
    // Date    : 17 Jun 2025
    // Source  : https://stackoverflow.com/questions/336210/

    /**
     * Keeps prompting until the username contains '_' and is 5 chars or less.
     */
    private static String getValidUsername(Scanner scanner) {
        while (true) {
            System.out.print("Enter username (must contain _ and be 5 chars or less): ");
            String username = scanner.nextLine();
            if (Login.checkUserName(username)) {
                return username;
            }
            System.out.println("Invalid username. It must contain an underscore and be <= 5 characters.");
        }
    }

    // Title   : Password complexity regex with look-aheads
    // Author  : Stack Overflow Q/19605150
    // Date    : 17 Jun 2025
    // Source  : https://stackoverflow.com/questions/19605150/

    /**
     * Keeps prompting until the password meets complexity requirements.
     */
    private static String getValidPassword(Scanner scanner) {
        while (true) {
            System.out.print("Enter password (8+ chars, 1 uppercase, 1 number, 1 special): ");
            String password = scanner.nextLine();
            if (Login.checkPasswordComplexity(password)) {
                return password;
            }
            System.out.println("Invalid password. Must be 8+ chars with uppercase, number, and special character.");
        }
    }

    // Title   : SA +27 cell number regex
    // Author  : Stack Overflow Q/33477950
    // Date    : 17 Jun 2025
    // Source  : https://stackoverflow.com/questions/33477950/

    /**
     * Keeps prompting until the cellphone matches +27XXXXXXXXX format.
     */
    private static String getValidCellphone(Scanner scanner) {
        while (true) {
            System.out.print("Enter cellphone number (+27XXXXXXXXX): ");
            String phone = scanner.nextLine();
            if (Login.checkCellPhoneNumber(phone)) {
                return phone;
            }
            System.out.println("Invalid number. Must start with +27 followed by 9 digits.");
        }
    }
}

// ───────────────────────── CODE ATTRIBUTION ─────────────────────────
// Title   : Scanner Console Input
// Author  : Oracle Java SE 8 API
// Date    : 17 Jun 2025
// Version : 1.0
// Source  : https://docs.oracle.com/javase/8/docs/api/java/util/Scanner.html
//
// Title   : Modern switch-arrow syntax (JEP 361)
// Author  : Oracle
// Date    : 17 Jun 2025
// Version : 1.0
// Source  : https://docs.oracle.com/en/java/javase/13/language/switch-expressions.html
//
// Title   : PROG5121 Lecture Slides
// Author  : The IIE / Rochelle Moodley (internal, unpublished)
// Date    : 2025
// ────────────────────────────────────────────────────────────────────