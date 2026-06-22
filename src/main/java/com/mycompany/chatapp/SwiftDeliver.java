/*
 * PROG5121 – Part 3 POE
 * I wrote this as the main entry point for SwiftDeliver, a food-delivery
 * coordination app.  I use Scanner for all console input – no GUI, no JOptionPane.
 *
 * Author: ST10448822
 * Date: 2025
 */

package com.mycompany.chatapp;

// I rely on Scanner to collect every input from the kitchen manager or rider.
// Title   : Scanner Console Input
// Author  : Oracle Java SE Docs
// Date    : 23 Jun 2025
// Version : 1.0
// Source  : https://docs.oracle.com/javase/8/docs/api/java/util/Scanner.html

import java.util.Scanner;

/**
 * SwiftDeliver – I launch registration, login, and the main message menu for
 * SwiftDeliver.
 *
 * Main menu (Part 2 + Part 3):
 *   1) Send Messages
 *   2) Show Recently Sent Messages  (Coming Soon)
 *   3) Quit
 *   4) Stored Messages              (Part 3 addition)
 *
 * @author ST10448822 2025
 */
public class SwiftDeliver {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("===========================================");
        System.out.println("   Welcome to SwiftDeliver – Registration  ");
        System.out.println("===========================================");

        // I load any messages saved from a previous session before registration starts.
        MessageManager.loadMessagesFromFile();

        // --- Registration ---
        System.out.print("Enter your first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter your last name: ");
        String lastName = scanner.nextLine();

        String username  = getValidUsername(scanner);
        String password  = getValidPassword(scanner);
        String cellphone = getValidCellphone(scanner);

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
                MessageManager.loadStoredMessagesFromJson();
                // I pre-load Part 3 test messages so the marker can see all features.
                MessageManager.populateTestMessages();
                runApp(scanner);
            }
        }

        scanner.close();
    }

    // ---------------------------------------------------------------
    // I defined the main application menu loop below.
    // ---------------------------------------------------------------

    /**
     * I display the four-option menu and route to the correct feature until the
     * user quits SwiftDeliver.
     *
     * @param scanner shared Scanner instance
     */
    public static void runApp(Scanner scanner) {
        boolean running = true;

        while (running) {
            System.out.println("\n===========================================");
            System.out.println("        SwiftDeliver Main Menu             ");
            System.out.println("===========================================");
            System.out.println("1) Send Messages");
            System.out.println("2) Show Recently Sent Messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            System.out.print("Choose an option: ");

            String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
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
                    break;

                case "2":
                    System.out.println("Coming Soon.");
                    break;

                case "3":
                    System.out.println("Goodbye! Exiting SwiftDeliver.");
                    running = false;
                    break;

                case "4":
                    MessageManager.showStoredMessagesMenu(scanner);
                    break;

                default:
                    System.out.println("Invalid option. Please choose 1-4.");
                    break;
            }
        }
    }

    // ---------------------------------------------------------------
    // I wrote these validation helpers – each loops until valid input is given.
    // ---------------------------------------------------------------

    // I used a regex look-ahead to check that the username contains _ and is <= 5 chars.
    // Title   : Username regex (contains _ and <= 5 chars)
    // Author  : Stack Overflow Q/336210
    // Source  : https://stackoverflow.com/questions/336210/

    /**
     * I keep prompting until the username contains '_' and is 5 chars or less.
     */
    private static String getValidUsername(Scanner scanner) {
        while (true) {
            System.out.print("Enter username (must contain _ and be 5 chars or less): ");
            String username = scanner.nextLine();
            if (Login.checkUserName(username)) {
                return username;
            }
            System.out.println("Username is not correctly formatted; please ensure that "
                    + "your username contains an underscore and is no more than five characters in length.");
        }
    }

    // I used look-ahead regex to enforce password complexity.
    // Title   : Password complexity regex with look-aheads
    // Author  : Stack Overflow Q/19605150
    // Source  : https://stackoverflow.com/questions/19605150/

    /**
     * I keep prompting until the password meets all complexity requirements.
     */
    private static String getValidPassword(Scanner scanner) {
        while (true) {
            System.out.print("Enter password (8+ chars, 1 uppercase, 1 number, 1 special): ");
            String password = scanner.nextLine();
            if (Login.checkPasswordComplexity(password)) {
                return password;
            }
            System.out.println("Password is not correctly formatted; please ensure that the password "
                    + "contains at least eight characters, a capital letter, a number, and a special character.");
        }
    }

    // I matched the South African +27 format using a regex found on Stack Overflow.
    // Title   : SA +27 cell number regex
    // Author  : Stack Overflow Q/33477950
    // Source  : https://stackoverflow.com/questions/33477950/

    /**
     * I keep prompting until the cellphone matches +27XXXXXXXXX format.
     */
    private static String getValidCellphone(Scanner scanner) {
        while (true) {
            System.out.print("Enter cellphone number (+27XXXXXXXXX): ");
            String phone = scanner.nextLine();
            if (Login.checkCellPhoneNumber(phone)) {
                return phone;
            }
            System.out.println("Cell phone number incorrectly formatted or does not contain international code.");
        }
    }
}

// ───────────────────────── CODE ATTRIBUTION ─────────────────────────
// Title   : Scanner Console Input
// Author  : Oracle Java SE 8 API
// Date    : 23 Jun 2025
// Version : 1.0
// Source  : https://docs.oracle.com/javase/8/docs/api/java/util/Scanner.html
//
// Title   : Username Regex (contains _ and <= 5 chars)
// Author  : Stack Overflow Q/336210
// Date    : 23 Jun 2025
// Version : 1.0
// Source  : https://stackoverflow.com/questions/336210/
//
// Title   : Password Complexity Regex with Look-aheads
// Author  : Stack Overflow Q/19605150
// Date    : 23 Jun 2025
// Version : 1.0
// Source  : https://stackoverflow.com/questions/19605150/
//
// Title   : SA (+27) Cell-Number Regex
// Author  : Stack Overflow Q/33477950
// Date    : 23 Jun 2025
// Version : 1.0
// Source  : https://stackoverflow.com/questions/33477950/
//
// Title   : PROG5121 Lecture Slides
// Author  : The IIE / Rochelle Moodley (internal, unpublished)
// Date    : 2025
// ────────────────────────────────────────────────────────────────────
