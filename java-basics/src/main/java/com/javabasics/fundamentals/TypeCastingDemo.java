package com.javabasics.fundamentals;

/**
 * Covers: widening, narrowing, object casting, String conversions.
 */
public class TypeCastingDemo {

    public static void main(String[] args) {

        // ── Widening (implicit) ──────────────────────────────────────
        // Smaller type → larger type. Java does this automatically. No data loss.
        // byte → short → int → long → float → double
        System.out.println("=== Widening (Implicit) Casting ===");
        int    intVal    = 42;
        long   longVal   = intVal;       // int  → long  (automatic)
        float  floatVal  = longVal;      // long → float (automatic)
        double doubleVal = floatVal;     // float→ double(automatic)

        System.out.println("int    : " + intVal);
        System.out.println("long   : " + longVal);
        System.out.println("float  : " + floatVal);
        System.out.println("double : " + doubleVal);

        // ── Narrowing (explicit) ─────────────────────────────────────
        // Larger type → smaller type. Must be done manually. May lose data.
        System.out.println("\n=== Narrowing (Explicit) Casting ===");
        double pi       = 3.99;
        int    truncated = (int) pi;       // 3 — decimal part lost
        byte   byteVal   = (byte) 130;     // 130 overflows byte range → wraps to -126

        System.out.println("double 3.99  → int  : " + truncated);
        System.out.println("int    130   → byte : " + byteVal);

        long   bigNum    = 123456789L;
        int    smallNum  = (int) bigNum;   // safe here since value fits
        System.out.println("long 123456789 → int : " + smallNum);

        // ── char ↔ int ───────────────────────────────────────────────
        System.out.println("\n=== char <-> int ===");
        char ch   = 'A';
        int  code = ch;                   // widening: char → int (ASCII/Unicode value)
        char back = (char)(code + 1);     // narrowing: int → char
        System.out.println("'A' as int   : " + code);     // 65
        System.out.println("65+1 as char : " + back);     // B

        // ── Object / Reference Casting ───────────────────────────────
        System.out.println("\n=== Object Casting ===");

        // Upcasting (automatic) — subclass ref held in superclass variable
        Animal animal = new Dog("Buddy");   // Dog IS-A Animal
        animal.speak();                     // calls Dog's speak via polymorphism

        // Downcasting (manual) — superclass ref cast back to subclass
        if (animal instanceof Dog dog) {    // pattern match (Java 16+)
            dog.fetch();
        }

        // ClassCastException example (caught safely)
        Animal anotherAnimal = new Dog("Max");
        try {
            Cat cat = (Cat) anotherAnimal;  // Dog cannot be cast to Cat
        } catch (ClassCastException e) {
            System.out.println("ClassCastException caught: " + e.getMessage());
        }

        // ── String Conversions ───────────────────────────────────────
        System.out.println("\n=== String Conversions ===");

        // Primitive → String
        int    num = 100;
        String s1  = String.valueOf(num);          // "100"
        String s2  = Integer.toString(num);        // "100"
        String s3  = "" + num;                     // "100" (concatenation trick)
        System.out.println("int→String: " + s1 + ", " + s2 + ", " + s3);

        // String → Primitive
        String numStr = "250";
        int    parsed  = Integer.parseInt(numStr);
        double parsedD = Double.parseDouble("3.14");
        boolean parsedB = Boolean.parseBoolean("true");
        System.out.println("String→int     : " + parsed);
        System.out.println("String→double  : " + parsedD);
        System.out.println("String→boolean : " + parsedB);
    }

    static class Animal {
        String name;
        Animal(String name) { this.name = name; }
        void speak() { System.out.println(name + " makes a sound"); }
    }

    static class Dog extends Animal {
        Dog(String name) { super(name); }
        @Override void speak() { System.out.println(name + " says: Woof!"); }
        void fetch() { System.out.println(name + " fetches the ball!"); }
    }

    static class Cat extends Animal {
        Cat(String name) { super(name); }
        @Override void speak() { System.out.println(name + " says: Meow!"); }
    }
}
