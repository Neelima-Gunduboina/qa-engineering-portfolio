package com.javabasics.oop;

/**
 * Inheritance: a subclass extends a superclass to reuse and specialise behaviour.
 * Covers: super keyword, method overriding, constructor chaining, final class.
 */
public class InheritanceDemo {

    public static void main(String[] args) {
        System.out.println("=== Inheritance Demo ===\n");

        Vehicle car = new Car("Toyota", "Camry", 4);
        Vehicle bike = new Motorcycle("Yamaha", "MT-07");
        ElectricCar tesla = new ElectricCar("Tesla", "Model 3", 4, 358);

        car.displayInfo();
        System.out.println();
        bike.displayInfo();
        System.out.println();
        tesla.displayInfo();
        System.out.println("Range: " + tesla.getRange() + " miles");

        System.out.println("\n=== Calling super methods ===");
        car.start();
        tesla.start();      // overridden in ElectricCar
        bike.start();
    }
}

// ── Superclass ───────────────────────────────────────────────────────────────
class Vehicle {
    protected String brand;
    protected String model;
    private static int vehicleCount = 0;

    Vehicle(String brand, String model) {
        this.brand = brand;
        this.model = model;
        vehicleCount++;
    }

    public void start() {
        System.out.println(brand + " " + model + " engine started.");
    }

    public void displayInfo() {
        System.out.println("Vehicle: " + brand + " " + model);
    }

    public static int getVehicleCount() { return vehicleCount; }
}

// ── Subclass: Car ─────────────────────────────────────────────────────────────
class Car extends Vehicle {
    private int doors;

    Car(String brand, String model, int doors) {
        super(brand, model);    // must be the first statement in constructor
        this.doors = doors;
    }

    @Override
    public void displayInfo() {
        super.displayInfo();    // call parent's version first
        System.out.println("Type: Car | Doors: " + doors);
    }
}

// ── Subclass: Motorcycle ──────────────────────────────────────────────────────
class Motorcycle extends Vehicle {

    Motorcycle(String brand, String model) {
        super(brand, model);
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Type: Motorcycle");
    }

    @Override
    public void start() {
        System.out.println(brand + " " + model + " revs up! Vroom!");
    }
}

// ── Multi-level: ElectricCar extends Car ──────────────────────────────────────
class ElectricCar extends Car {
    private int range; // miles on a full charge

    ElectricCar(String brand, String model, int doors, int range) {
        super(brand, model, doors);
        this.range = range;
    }

    public int getRange() { return range; }

    @Override
    public void start() {
        System.out.println(brand + " " + model + " silently powers on.");
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Powertrain: Electric | Range: " + range + " miles");
    }
}
