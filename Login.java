package com.mycompany.luxwynneapp;

public class Login {
    private String username;
    private String password;
    private String cellPhoneNumber;
    private String firstName;
    private String lastName;

    // Checks if username contains an underscore and is no more than 5 characters long
    public boolean checkUserName(String username) {
        if (username != null && username.contains("_") && username.length() <= 5) {
            return true;
        }
        return false;
    }

    // Checks password: at least 8 chars, 1 capital letter, 1 number, and 1 special character
    public boolean checkPasswordComplexity(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUppercase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (int i = 0; i < password.length(); i++) {
            char ch = password.charAt(i);
            if (Character.isUpperCase(ch)) {
                hasUppercase = true;
            } else if (Character.isDigit(ch)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(ch)) {
                hasSpecialChar = true;
            }
        }
        return hasUppercase && hasDigit && hasSpecialChar;
    }

    // Checks cell phone number format: starts with international code (+...) followed by up to 10 digits
    public boolean checkCellPhoneNumber(String cellPhoneNumber) {
        return cellPhoneNumber != null && cellPhoneNumber.matches("^\\+\\d{1,3}\\d{1,10}$");
    }

    // Registers the user after validating inputs
    public String registerUser(String username, String password, String cellPhoneNumber, String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;

        if (!checkUserName(username)) {
            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        }
        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }
        if (!checkCellPhoneNumber(cellPhoneNumber)) {
            return "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.";
        }

        this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
        return "Username successfully captured.\nPassword successfully captured.\nCell number successfully captured.";
    }

    // Verifies login credentials
    public boolean loginUser(String enteredUsername, String enteredPassword) {
        return enteredUsername != null && enteredUsername.equals(this.username) &&
               enteredPassword != null && enteredPassword.equals(this.password);
    }

    // Returns formatted login status message
    public String returnLoginStatus(boolean isLoggedIn) {
        if (isLoggedIn) {
            return "Welcome " + this.firstName + ", " + this.lastName + " it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }
}
