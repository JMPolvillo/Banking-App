package com.hackathon.bankingapp.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PasswordValidator {
    public static void validatePassword(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.length() < 8) {
            errors.add("Password must be at least 8 characters long");
        }

        if (password != null && password.length() > 128) {
            errors.add("Password must be less than 128 characters long");
        }

        if (password != null && !Pattern.compile("[A-Z]").matcher(password).find()) {
            errors.add("Password must contain at least one uppercase letter");
        }

        if (password != null && !Pattern.compile("[0-9]").matcher(password).find()) {
            errors.add("Password must contain at least one digit");
        }

        if (password != null && !Pattern.compile("[!@#$%^&*(),.?\":{}|<>]").matcher(password).find()) {
            errors.add("Password must contain at least one special character");
        }

        if (password != null && password.contains(" ")) {
            errors.add("Password cannot contain whitespace");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", errors));
        }
    }
}
