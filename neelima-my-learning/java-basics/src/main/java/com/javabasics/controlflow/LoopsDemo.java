package com.javabasics.controlflow;

import java.util.Arrays;
import java.util.List;

/**
 * Covers: for, while, do-while, enhanced for, break, continue, labels.
 */
public class LoopsDemo {

    public static void main(String[] args) {

        // ── for loop ─────────────────────────────────────────────────
        System.out.println("=== for loop ===");
        for (int i = 1; i <= 5; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        // Countdown
        for (int i = 5; i >= 1; i--) {
            System.out.print(i + " ");
        }
        System.out.println();

        // ── Nested for — multiplication table ────────────────────────
        System.out.println("\n=== Nested for (3x3 table) ===");
        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 3; j++) {
                System.out.printf("%4d", i * j);
            }
            System.out.println();
        }

        // ── while loop ───────────────────────────────────────────────
        System.out.println("\n=== while loop ===");
        int n = 1;
        while (n <= 5) {
            System.out.print(n + " ");
            n++;
        }
        System.out.println();

        // while — find first power of 2 greater than 100
        int power = 1;
        while (power <= 100) {
            power *= 2;
        }
        System.out.println("First power of 2 > 100: " + power);

        // ── do-while loop ────────────────────────────────────────────
        // Body executes at least once even if condition is false.
        System.out.println("\n=== do-while loop ===");
        int count = 1;
        do {
            System.out.print(count + " ");
            count++;
        } while (count <= 5);
        System.out.println();

        // do-while — condition false from the start, still runs once
        int x = 10;
        do {
            System.out.println("do-while runs once even though x=" + x + " > 5");
            x++;
        } while (x < 5);

        // ── Enhanced for (for-each) ───────────────────────────────────
        System.out.println("\n=== Enhanced for-each ===");
        int[] numbers = {10, 20, 30, 40, 50};
        int sum = 0;
        for (int num : numbers) {
            sum += num;
        }
        System.out.println("Array sum: " + sum);

        List<String> fruits = Arrays.asList("Apple", "Banana", "Cherry");
        for (String fruit : fruits) {
            System.out.println("Fruit: " + fruit);
        }

        // ── break ────────────────────────────────────────────────────
        System.out.println("\n=== break ===");
        for (int i = 1; i <= 10; i++) {
            if (i == 6) {
                System.out.println("Breaking at i=" + i);
                break;
            }
            System.out.print(i + " ");
        }
        System.out.println();

        // ── continue ─────────────────────────────────────────────────
        System.out.println("\n=== continue (skip even numbers) ===");
        for (int i = 1; i <= 10; i++) {
            if (i % 2 == 0) continue;   // skip even
            System.out.print(i + " ");
        }
        System.out.println();

        // ── Labeled break (exit outer loop) ──────────────────────────
        System.out.println("\n=== Labeled break ===");
        outer:
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i == 1 && j == 1) {
                    System.out.println("Breaking outer at i=" + i + " j=" + j);
                    break outer;
                }
                System.out.print("(" + i + "," + j + ") ");
            }
        }
        System.out.println();

        // ── Infinite loop with break ──────────────────────────────────
        System.out.println("\n=== Controlled infinite loop ===");
        int val = 0;
        while (true) {
            val++;
            if (val == 5) break;
        }
        System.out.println("Exited at val=" + val);
    }
}
