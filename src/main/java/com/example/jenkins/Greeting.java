package com.example.jenkins;

public final class Greeting {
    private Greeting() { }

    public static String message(String name) {
        String cleaned = name == null ? "" : name.strip();
        if (cleaned.length() > 80) {
            throw new IllegalArgumentException("Please use a name with 80 characters or fewer.");
        }
        return "Hello, " + (cleaned.isEmpty() ? "World" : cleaned) + "!";
    }
}
