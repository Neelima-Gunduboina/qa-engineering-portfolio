package com.javabasics.exceptions;

import java.io.*;

/**
 * Covers: checked vs unchecked exceptions, try/catch/finally,
 *         multi-catch, try-with-resources, custom exceptions, throws.
 */
public class ExceptionHandlingDemo {

    public static void main(String[] args) {
        tryCatchFinally();
        multiCatch();
        checkedVsUnchecked();
        tryWithResources();
        customExceptionDemo();
        exceptionChaining();
    }

    // ── Basic try / catch / finally ───────────────────────────────────────────
    static void tryCatchFinally() {
        System.out.println("=== try / catch / finally ===");
        int[] arr = {10, 20, 30};

        try {
            System.out.println("arr[5] = " + arr[5]);     // throws ArrayIndexOutOfBoundsException
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Caught: " + e.getMessage());
        } finally {
            // always runs — even if exception occurred or not
            System.out.println("finally block always runs");
        }

        // NullPointerException
        try {
            String s = null;
            System.out.println(s.length());
        } catch (NullPointerException e) {
            System.out.println("Caught NullPointerException: variable was null");
        }

        // ArithmeticException
        try {
            int result = 10 / 0;
        } catch (ArithmeticException e) {
            System.out.println("Caught ArithmeticException: " + e.getMessage());
        }
    }

    // ── Multi-catch ───────────────────────────────────────────────────────────
    static void multiCatch() {
        System.out.println("\n=== Multi-catch ===");
        String[] data = {"42", "hello", null};

        for (String item : data) {
            try {
                int value = Integer.parseInt(item);    // may throw NumberFormatException
                System.out.println("Parsed: " + value);
                System.out.println("Length: " + item.length()); // may throw NPE
            } catch (NumberFormatException | NullPointerException e) {
                System.out.println("Caught (" + e.getClass().getSimpleName() + "): " + e.getMessage());
            }
        }
    }

    // ── Checked vs Unchecked ──────────────────────────────────────────────────
    static void checkedVsUnchecked() {
        System.out.println("\n=== Checked vs Unchecked ===");

        // Unchecked (RuntimeException) — not forced by compiler
        try {
            String s = "abc";
            int n = Integer.parseInt(s);    // NumberFormatException
        } catch (RuntimeException e) {
            System.out.println("Unchecked: " + e.getClass().getSimpleName());
        }

        // Checked — compiler forces you to handle or declare
        try {
            readFile("/nonexistent/file.txt");
        } catch (IOException e) {
            System.out.println("Checked IOException: " + e.getMessage());
        }
    }

    static void readFile(String path) throws IOException {
        // Checked exception propagated via 'throws' declaration
        throw new IOException("File not found: " + path);
    }

    // ── try-with-resources ────────────────────────────────────────────────────
    static void tryWithResources() {
        System.out.println("\n=== try-with-resources ===");

        // Resource is automatically closed when block exits (even on exception)
        try (MockResource resource = new MockResource("DB Connection")) {
            resource.use();
            // resource.close() called automatically here
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println("Resource closed automatically.");
    }

    // ── Custom Exceptions ─────────────────────────────────────────────────────
    static void customExceptionDemo() {
        System.out.println("\n=== Custom Exceptions ===");

        try {
            registerStudent("", 25);
        } catch (InvalidStudentException e) {
            System.out.println("Caught custom exception: " + e.getMessage());
            System.out.println("Error code: " + e.getErrorCode());
        }

        try {
            registerStudent("Neelima", 150);   // invalid age
        } catch (InvalidStudentException e) {
            System.out.println("Caught: " + e.getMessage());
        }
    }

    static void registerStudent(String name, int age) throws InvalidStudentException {
        if (name == null || name.isBlank()) {
            throw new InvalidStudentException("Name cannot be blank", "ERR_001");
        }
        if (age < 5 || age > 100) {
            throw new InvalidStudentException("Age " + age + " is out of range (5-100)", "ERR_002");
        }
        System.out.println("Registered: " + name + ", age " + age);
    }

    // ── Exception Chaining ────────────────────────────────────────────────────
    static void exceptionChaining() {
        System.out.println("\n=== Exception Chaining ===");
        try {
            loadConfig();
        } catch (RuntimeException e) {
            System.out.println("High-level error: " + e.getMessage());
            System.out.println("Root cause     : " + e.getCause().getMessage());
        }
    }

    static void loadConfig() {
        try {
            throw new IOException("config.properties not found");
        } catch (IOException e) {
            // Wrap low-level checked exception in high-level runtime exception
            throw new RuntimeException("Failed to load application config", e);
        }
    }
}

// ── Custom checked exception ──────────────────────────────────────────────────
class InvalidStudentException extends Exception {
    private final String errorCode;

    InvalidStudentException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() { return errorCode; }
}

// ── AutoCloseable resource for try-with-resources demo ───────────────────────
class MockResource implements AutoCloseable {
    private final String name;

    MockResource(String name) {
        this.name = name;
        System.out.println("Opened: " + name);
    }

    public void use() {
        System.out.println("Using: " + name);
    }

    @Override
    public void close() {
        System.out.println("Closing: " + name);
    }
}
