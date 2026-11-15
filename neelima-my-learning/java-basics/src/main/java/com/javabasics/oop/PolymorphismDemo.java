package com.javabasics.oop;

import java.util.ArrayList;
import java.util.List;

/**
 * Polymorphism: one interface, many implementations.
 * Covers: compile-time (overloading) and runtime (overriding) polymorphism.
 */
public class PolymorphismDemo {

    public static void main(String[] args) {

        // ── Runtime Polymorphism (overriding) ────────────────────────
        // Parent reference holds child objects — method resolved at runtime
        System.out.println("=== Runtime Polymorphism ===");
        List<Shape> shapes = new ArrayList<>();
        shapes.add(new CircleShape(5.0));
        shapes.add(new RectangleShape(4.0, 6.0));
        shapes.add(new TriangleShape(3.0, 8.0));

        for (Shape shape : shapes) {
            // The correct overridden method is called based on actual object type
            System.out.printf("%-20s area=%.2f  perimeter=%.2f%n",
                shape.getClass().getSimpleName(),
                shape.area(),
                shape.perimeter());
        }

        // ── Compile-time Polymorphism (overloading) ──────────────────
        System.out.println("\n=== Compile-time Polymorphism (Overloading) ===");
        Calculator calc = new Calculator();
        System.out.println("add(2, 3)         = " + calc.add(2, 3));
        System.out.println("add(2, 3, 4)      = " + calc.add(2, 3, 4));
        System.out.println("add(2.5, 3.5)     = " + calc.add(2.5, 3.5));
        System.out.println("add(\"Hello\",\" World\") = " + calc.add("Hello", " World"));

        // ── Polymorphism through interfaces ──────────────────────────
        System.out.println("\n=== Polymorphism via Interface ===");
        List<Printable> items = new ArrayList<>();
        items.add(new Document("Report.pdf"));
        items.add(new Photo("Vacation.jpg"));
        items.add(new Email("Meeting reminder"));

        for (Printable item : items) {
            item.print();
        }
    }
}

// ── Abstract superclass for shapes ───────────────────────────────────────────
abstract class Shape {
    abstract double area();
    abstract double perimeter();
}

class CircleShape extends Shape {
    private double radius;
    CircleShape(double radius) { this.radius = radius; }

    @Override public double area()      { return Math.PI * radius * radius; }
    @Override public double perimeter() { return 2 * Math.PI * radius; }
}

class RectangleShape extends Shape {
    private double width, height;
    RectangleShape(double width, double height) { this.width = width; this.height = height; }

    @Override public double area()      { return width * height; }
    @Override public double perimeter() { return 2 * (width + height); }
}

class TriangleShape extends Shape {
    private double base, height;
    TriangleShape(double base, double height) { this.base = base; this.height = height; }

    @Override public double area()      { return 0.5 * base * height; }
    @Override public double perimeter() { return base + height + Math.sqrt(base * base + height * height); }
}

// ── Overloading example ───────────────────────────────────────────────────────
class Calculator {
    int    add(int a, int b)             { return a + b; }
    int    add(int a, int b, int c)      { return a + b + c; }
    double add(double a, double b)       { return a + b; }
    String add(String a, String b)       { return a + b; }
}

// ── Interface polymorphism ────────────────────────────────────────────────────
interface Printable {
    void print();
}

class Document implements Printable {
    private String name;
    Document(String name) { this.name = name; }
    @Override public void print() { System.out.println("Printing document: " + name); }
}

class Photo implements Printable {
    private String name;
    Photo(String name) { this.name = name; }
    @Override public void print() { System.out.println("Printing photo: " + name + " [Color calibrated]"); }
}

class Email implements Printable {
    private String subject;
    Email(String subject) { this.subject = subject; }
    @Override public void print() { System.out.println("Printing email: \"" + subject + "\""); }
}
