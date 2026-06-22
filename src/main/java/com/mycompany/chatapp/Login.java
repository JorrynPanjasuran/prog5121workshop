/*
 * PROG5121 – Part 3 POE
 * I wrote this class to handle user registration and login validation
 * for the SwiftDeliver application.
 *
 * Author: ST10448822
 * Date: 2025
 */

package com.mycompany.chatapp;

// I used a positive look-ahead regex to enforce the underscore and 5-char limit.
// Title   : Username regex (contains _ and <= 5 chars)
// Author  : Stack Overflow Q/336210; GeeksforGeeks
// Date    : 23 Jun 2025
// Sources :
//   https://stackoverflow.com/questions/336210/
//   https://www.geeksforgeeks.org/how-to-validate-a-username-using-regular-expressions-in-java/

// I used chained look-aheads to enforce uppercase, digit, and special-char requirements.
// Title   : Password complexity regex with look-aheads
// Author  : Stack Overflow Q/19605150
// Date    : 23 Jun 2025
// Source  : https://stackoverflow.com/questions/19605150/

// I matched the South African +27XXXXXXXXX format using a regex.
// Title   : SA +27 cell number regex
// Author  : Stack Overflow Q/33477950
// Date    : 23 Jun 2025
// Source  : https://stackoverflow.com/questions/33477950/

/**
 * Login – I store user credentials and provide registration/login logic for SwiftDeliver.
 *
 * @author ST10448822 2025
 */
public class Login {

    // I store all user fields after validation passes.
    private final String username;
    private final String password;
    private final String cellphone;
    private final String firstName;
    private final String lastName;

    /**
     * I create a Login object.  Validation must be done before calling this.
     *
     * @param username  must contain _ and be <= 5 chars
     * @param password  must be 8+ chars, 1 uppercase, 1 digit, 1 special char
     * @param cellphone must match +27XXXXXXXXX
     * @param firstName user's first name
     * @param lastName  user's last name
     */
    public Login(String username, String password,
                 String cellphone, String firstName, String lastName) {
        this.username  = username;
        this.password  = password;
        this.cellphone = cellphone;
        this.firstName = firstName;
        this.lastName  = lastName;
    }

    // ---------------------------------------------------------------
    // Static validation methods (I call these from ChatApp before building the object)
    // ---------------------------------------------------------------

    /**
     * I check that the username contains at least one underscore and is 5 characters
     * or less, using a positive look-ahead regex.
     *
     * @param username the input to check
     * @return true if valid
     */
    public static boolean checkUserName(String username) {
        // (?=.*_) ensures underscore is present; .{1,5} limits length to 5
        return username != null && username.matches("^(?=.*_).{1,5}$");
    }

    /**
     * I check that the password is at least 8 characters, contains one uppercase letter,
     * one digit, and one special character.
     *
     * @param password the input to check
     * @return true if valid
     */
    public static boolean checkPasswordComplexity(String password) {
        // I use look-aheads to enforce uppercase, digit, and special character requirements.
        return password != null
                && password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$");
    }

    /**
     * I check that the cell number follows South African E.164 format: +27 then 9 digits.
     *
     * @param cellphone the input to check
     * @return true if valid
     */
    public static boolean checkCellPhoneNumber(String cellphone) {
        return cellphone != null && cellphone.matches("^\\+27\\d{9}$");
    }

    // ---------------------------------------------------------------
    // Registration and login
    // ---------------------------------------------------------------

    /**
     * I validate all stored fields and return a human-readable result message.
     * I call this after the Login object has been constructed.
     *
     * @return success or specific error message
     */
    public String register() {

        if (!checkUserName(username)) {
            return "Username is not correctly formatted.\n"
                 + "It must contain an underscore and be no more than 5 characters.";
        }

        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted.\n"
                 + "It must be 8+ characters and include:\n"
                 + "  - A capital letter\n  - A number\n  - A special character";
        }

        if (!checkCellPhoneNumber(cellphone)) {
            return "Cell phone number is not correctly formatted.\n"
                 + "It must start with +27 followed by exactly 9 digits.";
        }

        // All fields valid
        return "Username and password successfully captured.\n"
             + "Cell phone number successfully added.";
    }

    /**
     * I check whether the supplied credentials match the registered user.
     *
     * @param inputUsername username entered at login
     * @param inputPassword password entered at login
     * @return true if both match
     */
    public boolean login(String inputUsername, String inputPassword) {
        return username.equals(inputUsername) && password.equals(inputPassword);
    }

    /**
     * I return a personalised welcome or error string based on login outcome.
     *
     * @param loginStatus result of login()
     * @return message string for display
     */
    public String loginStatusMessage(boolean loginStatus) {
        if (loginStatus) {
            return "Welcome " + firstName + " " + lastName
                 + ", it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }

    // ---------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------

    public String getUsername()  { return username;  }
    public String getPassword()  { return password;  }
    public String getCellphone() { return cellphone; }
    public String getFirstName() { return firstName; }
    public String getLastName()  { return lastName;  }
}

// ───────────────────────── CODE ATTRIBUTION ─────────────────────────
// Title   : Username Regex (contains _ and <= 5 chars)
// Author  : Stack Overflow Q/336210; GeeksforGeeks
// Date    : 23 Jun 2025
// Version : 1.0
// Sources :
//   https://stackoverflow.com/questions/336210/
//   https://www.geeksforgeeks.org/how-to-validate-a-username-using-regular-expressions-in-java/
//
// Title   : Password Complexity Regex with Look-aheads
// Author  : Stack Overflow Q/19605150; Q/12090077
// Date    : 23 Jun 2025
// Version : 1.0
// Sources :
//   https://stackoverflow.com/questions/19605150/
//   https://stackoverflow.com/questions/12090077/
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
