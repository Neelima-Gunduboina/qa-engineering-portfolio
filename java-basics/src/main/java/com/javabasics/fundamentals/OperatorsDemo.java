package com.javabasics.fundamentals;

/**
 * Covers: arithmetic, assignment, comparison, logical, bitwise, ternary, instanceof.
 */
public class OperatorsDemo {

    public static void main(String[] args) {

        // ── Arithmetic ───────────────────────────────────────────────
        int a = 10, b = 3;
        System.out.println("=== Arithmetic Operators ===");
        System.out.println("a + b  = " + (a + b));   // 13
        System.out.println("a - b  = " + (a - b));   // 7
        System.out.println("a * b  = " + (a * b));   // 30
        System.out.println("a / b  = " + (a / b));   // 3  (integer division, truncates)
        System.out.println("a % b  = " + (a % b));   // 1  (remainder/modulo)

        // Pre / post increment & decrement
        int x = 5;
        System.out.println("x++  = " + x++);  // prints 5, then x becomes 6
        System.out.println("++x  = " + ++x);  // x becomes 7, then prints 7
        System.out.println("x--  = " + x--);  // prints 7, then x becomes 6
        System.out.println("--x  = " + --x);  // x becomes 5, then prints 5

        // ── Assignment ───────────────────────────────────────────────
        System.out.println("\n=== Assignment Operators ===");
        int n = 20;
        n += 5;  System.out.println("n += 5  → " + n);  // 25
        n -= 3;  System.out.println("n -= 3  → " + n);  // 22
        n *= 2;  System.out.println("n *= 2  → " + n);  // 44
        n /= 4;  System.out.println("n /= 4  → " + n);  // 11
        n %= 4;  System.out.println("n %= 4  → " + n);  // 3

        // ── Comparison ───────────────────────────────────────────────
        System.out.println("\n=== Comparison Operators ===");
        int p = 10, q = 20;
        System.out.println("p == q : " + (p == q));   // false
        System.out.println("p != q : " + (p != q));   // true
        System.out.println("p >  q : " + (p >  q));   // false
        System.out.println("p <  q : " + (p <  q));   // true
        System.out.println("p >= q : " + (p >= q));   // false
        System.out.println("p <= q : " + (p <= q));   // true

        // ── Logical ──────────────────────────────────────────────────
        System.out.println("\n=== Logical Operators ===");
        boolean t = true, f = false;
        System.out.println("t && f : " + (t && f));   // false (AND)
        System.out.println("t || f : " + (t || f));   // true  (OR)
        System.out.println("!t     : " + (!t));        // false (NOT)

        // Short-circuit: right side NOT evaluated if result already known
        int counter = 0;
        boolean result = false && (++counter > 0);   // counter stays 0
        System.out.println("Short-circuit &&, counter=" + counter);
        result = true || (++counter > 0);            // counter stays 0
        System.out.println("Short-circuit ||, counter=" + counter);

        // ── Bitwise ──────────────────────────────────────────────────
        System.out.println("\n=== Bitwise Operators ===");
        int m = 0b1010;  // 10 in binary
        int k = 0b1100;  // 12 in binary
        System.out.println("m & k  = " + (m & k));    // 1000 = 8  (AND)
        System.out.println("m | k  = " + (m | k));    // 1110 = 14 (OR)
        System.out.println("m ^ k  = " + (m ^ k));    // 0110 = 6  (XOR)
        System.out.println("~m     = " + (~m));        // -11        (NOT)
        System.out.println("m << 1 = " + (m << 1));   // 10100 = 20 (left shift)
        System.out.println("m >> 1 = " + (m >> 1));   // 0101  = 5  (right shift)

        // ── Ternary ──────────────────────────────────────────────────
        System.out.println("\n=== Ternary Operator ===");
        int age = 20;
        String status = (age >= 18) ? "Adult" : "Minor";
        System.out.println("age=" + age + " → " + status);

        int max = (a > b) ? a : b;
        System.out.println("max(" + a + "," + b + ") = " + max);

        // ── instanceof ───────────────────────────────────────────────
        System.out.println("\n=== instanceof Operator ===");
        Object obj = "Hello";
        System.out.println("obj instanceof String  : " + (obj instanceof String));   // true
        System.out.println("obj instanceof Integer : " + (obj instanceof Integer));  // false

        // Pattern matching instanceof (Java 16+)
        if (obj instanceof String s) {
            System.out.println("Pattern match — length: " + s.length());
        }
    }
}
