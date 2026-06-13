package com.javabasics.controlflow;

/**
 * Covers: if/else-if/else, nested if, switch statement, switch expression (Java 14+).
 */
public class ConditionalsDemo {

    public static void main(String[] args) {

        // ── if / else if / else ───────────────────────────────────────
        System.out.println("=== if / else if / else ===");
        int score = 78;

        if (score >= 90) {
            System.out.println("Grade: A");
        } else if (score >= 80) {
            System.out.println("Grade: B");
        } else if (score >= 70) {
            System.out.println("Grade: C");
        } else if (score >= 60) {
            System.out.println("Grade: D");
        } else {
            System.out.println("Grade: F");
        }

        // ── Nested if ────────────────────────────────────────────────
        System.out.println("\n=== Nested if ===");
        int age = 20;
        boolean hasLicense = true;

        if (age >= 18) {
            if (hasLicense) {
                System.out.println("Can drive legally.");
            } else {
                System.out.println("Old enough but no license.");
            }
        } else {
            System.out.println("Too young to drive.");
        }

        // ── switch statement ─────────────────────────────────────────
        System.out.println("\n=== switch statement ===");
        int dayNum = 3;
        String dayName;

        switch (dayNum) {
            case 1:
                dayName = "Monday";
                break;
            case 2:
                dayName = "Tuesday";
                break;
            case 3:
                dayName = "Wednesday";
                break;
            case 4:
                dayName = "Thursday";
                break;
            case 5:
                dayName = "Friday";
                break;
            case 6:
                dayName = "Saturday";
                break;
            case 7:
                dayName = "Sunday";
                break;
            default:
                dayName = "Invalid day";
        }
        System.out.println("Day " + dayNum + " is: " + dayName);

        // Fall-through example (no break)
        System.out.println("\n--- Fall-through switch ---");
        int month = 4;
        switch (month) {
            case 12:
            case 1:
            case 2:
                System.out.println("Winter");
                break;
            case 3:
            case 4:
            case 5:
                System.out.println("Spring");
                break;
            case 6:
            case 7:
            case 8:
                System.out.println("Summer");
                break;
            default:
                System.out.println("Autumn");
        }

        // ── switch expression (Java 14+) ─────────────────────────────
        System.out.println("\n=== switch expression (Java 14+) ===");
        String day = "MONDAY";
        String type = switch (day) {
            case "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY" -> "Weekday";
            case "SATURDAY", "SUNDAY" -> "Weekend";
            default -> "Unknown";
        };
        System.out.println(day + " is a: " + type);

        // switch expression with yield
        int numLetters = switch (day) {
            case "MONDAY", "FRIDAY", "SUNDAY" -> 6;
            case "TUESDAY"                    -> 7;
            case "THURSDAY", "SATURDAY"       -> 8;
            default -> {
                System.out.println("Calculating for: " + day);
                yield day.length();
            }
        };
        System.out.println("Letters in " + day + ": " + numLetters);

        // ── switch on String ─────────────────────────────────────────
        System.out.println("\n=== switch on String ===");
        String role = "ADMIN";
        switch (role) {
            case "ADMIN":
                System.out.println("Full access");
                break;
            case "EDITOR":
                System.out.println("Read/write access");
                break;
            case "VIEWER":
                System.out.println("Read-only access");
                break;
            default:
                System.out.println("No access");
        }
    }
}
