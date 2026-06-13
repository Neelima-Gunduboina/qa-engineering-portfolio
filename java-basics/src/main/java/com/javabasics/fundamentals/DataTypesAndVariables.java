package com.javabasics.fundamentals;

/**
 * Covers: primitive types, wrapper classes, var keyword, constants, scope.
 */
public class DataTypesAndVariables {

    // Constants (compile-time)
    static final double PI = 3.14159265358979;
    static final int MAX_STUDENTS = 100;

    public static void main(String[] args) {

        // ── Primitive Types ──────────────────────────────────────────
        byte   b  = 127;               // 8-bit  → -128 to 127
        short  s  = 32_000;            // 16-bit → -32,768 to 32,767
        int    i  = 2_000_000;         // 32-bit → ~±2 billion
        long   l  = 9_000_000_000L;    // 64-bit → ~±9 quintillion  (L suffix required)

        float  f  = 3.14f;             // 32-bit decimal (f suffix required)
        double d  = 3.141592653589793; // 64-bit decimal (default for decimals)

        char   c  = 'A';               // single Unicode character (16-bit)
        boolean flag = true;           // true / false only

        System.out.println("=== Primitive Types ===");
        System.out.println("byte    : " + b);
        System.out.println("short   : " + s);
        System.out.println("int     : " + i);
        System.out.println("long    : " + l);
        System.out.println("float   : " + f);
        System.out.println("double  : " + d);
        System.out.println("char    : " + c);
        System.out.println("boolean : " + flag);

        // ── Wrapper Classes ──────────────────────────────────────────
        // Each primitive has an object wrapper that lives in java.lang
        Integer  intObj    = 42;          // autoboxing (int → Integer)
        Double   doubleObj = 9.99;
        Boolean  boolObj   = false;
        Character charObj  = 'Z';

        int unboxed = intObj;             // unboxing (Integer → int)

        System.out.println("\n=== Wrapper Classes ===");
        System.out.println("Integer.MAX_VALUE : " + Integer.MAX_VALUE);
        System.out.println("Integer.MIN_VALUE : " + Integer.MIN_VALUE);
        System.out.println("Integer.toBinaryString(10) : " + Integer.toBinaryString(10));
        System.out.println("Double.parseDouble(\"3.5\") : " + Double.parseDouble("3.5"));

        // ── var (local-variable type inference, Java 10+) ───────────
        var name    = "Neelima";          // inferred as String
        var score   = 95.5;               // inferred as double
        var count   = 10;                 // inferred as int

        System.out.println("\n=== var keyword ===");
        System.out.println("name  : " + name  + "  (" + ((Object)name).getClass().getSimpleName()  + ")");
        System.out.println("score : " + score + "  (" + ((Object)score).getClass().getSimpleName() + ")");
        System.out.println("count : " + count + "  (" + ((Object)count).getClass().getSimpleName() + ")");

        // ── String ──────────────────────────────────────────────────
        String greeting = "Hello, " + name + "!";   // concatenation
        String multiLine = """
                This is a
                text block (Java 15+).
                """;

        System.out.println("\n=== String ===");
        System.out.println(greeting);
        System.out.println(multiLine);

        // ── Constants ───────────────────────────────────────────────
        System.out.println("=== Constants ===");
        System.out.println("PI           : " + PI);
        System.out.println("MAX_STUDENTS : " + MAX_STUDENTS);

        // ── Default Values (fields, not locals) ─────────────────────
        System.out.println("\n=== Default Field Values ===");
        DefaultValues dv = new DefaultValues();
        System.out.println("int    default : " + dv.number);
        System.out.println("double default : " + dv.decimal);
        System.out.println("bool   default : " + dv.active);
        System.out.println("String default : " + dv.text);
    }

    // Inner class to show default field values
    static class DefaultValues {
        int    number;   // 0
        double decimal;  // 0.0
        boolean active;  // false
        String  text;    // null
    }
}
