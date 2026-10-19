package com.mycompany.luxwynneapp;

import java.util.Scanner;

public class LuxwynneApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login userAuth = new Login();

        System.out.println("=== SYSTEM REGISTRATION ===");
        System.out.print("Enter your First Name: ");
        String fName = scanner.nextLine();

        System.out.print("Enter your Last Name: ");
        String lName = scanner.nextLine();

        System.out.print("Enter Username (must contain '_' and be <= 5 chars): ");
        String uName = scanner.nextLine();

        System.out.print("Enter Password (>= 8 chars, 1 uppercase, 1 digit, 1 special): ");
        String pWord = scanner.nextLine();

        System.out.print("Enter Cell Phone Number (with international code, e.g., +27...): ");
        String cellNum = scanner.nextLine();

        String registrationResult = userAuth.registerUser(uName, pWord, cellNum, fName, lName);
        System.out.println("\n" + registrationResult);

        // Proceed to login if registration was fully successful
        if (registrationResult.contains("Cell number successfully captured.")) {
            System.out.println("\n=== SYSTEM LOGIN ===");
            System.out.print("Enter Username: ");
            String loginUser = scanner.nextLine();

            System.out.print("Enter Password: ");
            String loginPass = scanner.nextLine();

            boolean loginSuccess = userAuth.loginUser(loginUser, loginPass);
            System.out.println(userAuth.returnLoginStatus(loginSuccess));
        }

        scanner.close();
    }
}
