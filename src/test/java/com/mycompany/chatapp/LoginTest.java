/*
 * PROG5121 – Part 3 POE
 * JUnit 5 tests for the Login class.
 * Tests: username validation, password validation, cellphone validation,
 *        register(), login(), and loginStatusMessage().
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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * LoginTest – unit tests for username, password, cellphone validation,
 * registration outcome messages, and login authentication.
 *
 * @author Jorryn Panjasuran 2025
 */
public class LoginTest {

    // ===========================================================
    // checkUserName
    // ===========================================================

    /**
     * "kyl_1" contains an underscore and is exactly 5 chars → valid.
     */
    @Test
    public void testCheckUserName_Valid() {
        assertTrue(Login.checkUserName("kyl_1"),
                "Username with underscore and 5 chars should be valid.");
    }

    /**
     * "kyle_1" is 6 characters long → invalid (exceeds 5-char limit).
     */
    @Test
    public void testCheckUserName_TooLong() {
        assertFalse(Login.checkUserName("kyle_1"),
                "Username longer than 5 chars should be invalid.");
    }

    /**
     * "kyle1" has no underscore → invalid.
     */
    @Test
    public void testCheckUserName_NoUnderscore() {
        assertFalse(Login.checkUserName("kyle1"),
                "Username without an underscore should be invalid.");
    }

    /**
     * null input must return false — no NullPointerException allowed.
     */
    @Test
    public void testCheckUserName_Null() {
        assertFalse(Login.checkUserName(null),
                "Null username should return false, not throw.");
    }

    // ===========================================================
    // checkPasswordComplexity
    // ===========================================================

    /**
     * "Password1!" meets all rules: 8+ chars, uppercase, digit, special → valid.
     */
    @Test
    public void testCheckPasswordComplexity_Valid() {
        assertTrue(Login.checkPasswordComplexity("Password1!"),
                "Password meeting all rules should be valid.");
    }

    /**
     * "password1!" has no uppercase letter → invalid.
     */
    @Test
    public void testCheckPasswordComplexity_NoUppercase() {
        assertFalse(Login.checkPasswordComplexity("password1!"),
                "Password with no uppercase should be invalid.");
    }

    /**
     * "Pass1!" is only 6 characters → invalid (needs 8+).
     */
    @Test
    public void testCheckPasswordComplexity_TooShort() {
        assertFalse(Login.checkPasswordComplexity("Pass1!"),
                "Password shorter than 8 chars should be invalid.");
    }

    /**
     * "Password!" has no digit → invalid.
     */
    @Test
    public void testCheckPasswordComplexity_NoDigit() {
        assertFalse(Login.checkPasswordComplexity("Password!"),
                "Password with no digit should be invalid.");
    }

    /**
     * "Password1" has no special character → invalid.
     */
    @Test
    public void testCheckPasswordComplexity_NoSpecialChar() {
        assertFalse(Login.checkPasswordComplexity("Password1"),
                "Password with no special character should be invalid.");
    }

    // ===========================================================
    // checkCellPhoneNumber
    // ===========================================================

    /**
     * "+27838968976" is +27 followed by exactly 9 digits → valid.
     */
    @Test
    public void testCheckCellPhoneNumber_Valid() {
        assertTrue(Login.checkCellPhoneNumber("+27838968976"),
                "Valid South African +27 number should pass.");
    }

    /**
     * "0838968976" does not start with +27 → invalid.
     */
    @Test
    public void testCheckCellPhoneNumber_NoCountryCode() {
        assertFalse(Login.checkCellPhoneNumber("0838968976"),
                "Number without +27 prefix should be invalid.");
    }

    /**
     * "+2783896897" has only 8 digits after +27 → invalid (needs exactly 9).
     */
    @Test
    public void testCheckCellPhoneNumber_TooShort() {
        assertFalse(Login.checkCellPhoneNumber("+2783896897"),
                "Number with fewer than 9 digits after +27 should be invalid.");
    }

    /**
     * "+278389689761" has 10 digits after +27 → invalid (needs exactly 9).
     */
    @Test
    public void testCheckCellPhoneNumber_TooLong() {
        assertFalse(Login.checkCellPhoneNumber("+278389689761"),
                "Number with more than 9 digits after +27 should be invalid.");
    }

    // ===========================================================
    // register()
    // ===========================================================

    /**
     * All valid fields → register() returns a message containing "successfully captured".
     */
    @Test
    public void testRegister_Success() {
        Login user = new Login("kyl_1", "Password1!", "+27838968976", "Kyle", "Smith");
        assertTrue(user.register().contains("successfully captured"),
                "Successful registration should return a success message.");
    }

    /**
     * Invalid username → register() message mentions "username".
     */
    @Test
    public void testRegister_BadUsername() {
        Login user = new Login("kyle1", "Password1!", "+27838968976", "Kyle", "Smith");
        assertTrue(user.register().toLowerCase().contains("username"),
                "Bad username should produce a username error message.");
    }

    /**
     * Invalid password → register() message mentions "password".
     */
    @Test
    public void testRegister_BadPassword() {
        Login user = new Login("kyl_1", "pass", "+27838968976", "Kyle", "Smith");
        assertTrue(user.register().toLowerCase().contains("password"),
                "Bad password should produce a password error message.");
    }

    /**
     * Invalid cellphone → register() message mentions "cell".
     */
    @Test
    public void testRegister_BadCellphone() {
        Login user = new Login("kyl_1", "Password1!", "0838968976", "Kyle", "Smith");
        assertTrue(user.register().toLowerCase().contains("cell"),
                "Bad cellphone should produce a cellphone error message.");
    }

    // ===========================================================
    // login()
    // ===========================================================

    /**
     * Correct username and password → login() returns true.
     */
    @Test
    public void testLogin_CorrectCredentials() {
        Login user = new Login("kyl_1", "Password1!", "+27838968976", "Kyle", "Smith");
        assertTrue(user.login("kyl_1", "Password1!"),
                "Correct credentials should return true.");
    }

    /**
     * Wrong password → login() returns false.
     */
    @Test
    public void testLogin_WrongPassword() {
        Login user = new Login("kyl_1", "Password1!", "+27838968976", "Kyle", "Smith");
        assertFalse(user.login("kyl_1", "WrongPass1!"),
                "Wrong password should return false.");
    }

    /**
     * Wrong username → login() returns false.
     */
    @Test
    public void testLogin_WrongUsername() {
        Login user = new Login("kyl_1", "Password1!", "+27838968976", "Kyle", "Smith");
        assertFalse(user.login("ab_cd", "Password1!"),
                "Wrong username should return false.");
    }

    // ===========================================================
    // loginStatusMessage()
    // ===========================================================

    /**
     * loginStatusMessage(true) must include the user's first and last name.
     */
    @Test
    public void testLoginStatusMessage_Success() {
        Login user = new Login("kyl_1", "Password1!", "+27838968976", "Kyle", "Smith");
        String msg = user.loginStatusMessage(true);
        assertTrue(msg.contains("Kyle") && msg.contains("Smith"),
                "Successful login message should contain the user's full name.");
    }

    /**
     * loginStatusMessage(false) must say credentials are incorrect.
     */
    @Test
    public void testLoginStatusMessage_Failure() {
        Login user = new Login("kyl_1", "Password1!", "+27838968976", "Kyle", "Smith");
        String msg = user.loginStatusMessage(false);
        assertTrue(msg.toLowerCase().contains("incorrect"),
                "Failed login message should indicate incorrect credentials.");
    }
}

// ───────────────────────── CODE ATTRIBUTION ─────────────────────────
// Title   : JUnit 5 Assertion Methods
// Author  : JUnit Team – Official API
// Date    : 17 Jun 2025
// Version : 1.0
// Source  : https://junit.org/junit5/docs/current/api/
//
// Title   : Password Complexity Regex with Look-aheads
// Author  : Stack Overflow Q/19605150
// Date    : 17 Jun 2025
// Version : 1.0
// Source  : https://stackoverflow.com/questions/19605150/
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
