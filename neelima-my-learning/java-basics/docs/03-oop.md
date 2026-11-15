# 03 — Object-Oriented Programming (OOP)

Java is built around 4 OOP pillars.

---

## 1. Encapsulation

**Hide internal state. Expose only what's needed.**

```java
public class BankAccount {
    private double balance;   // private — nobody touches this directly

    public double getBalance() { return balance; }   // controlled read

    public void deposit(double amount) {
        if (amount > 0) balance += amount;           // validated write
    }
}
```

**Why:** Prevents invalid state. If `balance` were public, any code could set it to `-999`.

---

## 2. Inheritance

**A subclass reuses and extends the superclass.**

```java
class Vehicle {
    String brand;
    void start() { System.out.println("Engine started"); }
}

class Car extends Vehicle {
    int doors;

    @Override
    void start() {
        super.start();                            // call parent first
        System.out.println("Car ready with " + doors + " doors");
    }
}
```

- `extends` — single inheritance only (one parent class)
- `super()` — calls the parent constructor
- `@Override` — tells the compiler you intentionally override a method

---

## 3. Polymorphism

**One interface, many behaviours.**

### Runtime (overriding)
```java
List<Animal> animals = List.of(new Dog(), new Cat(), new Bird());
for (Animal a : animals) {
    a.speak();   // Dog: "Woof", Cat: "Meow", Bird: "Tweet" — decided at runtime
}
```

### Compile-time (overloading)
```java
class Calculator {
    int    add(int a, int b)     { return a + b; }
    double add(double a, double b) { return a + b; }   // same name, different params
}
```

---

## 4. Abstraction

**Expose WHAT, hide HOW.**

### Abstract class
```java
abstract class Payment {
    // Subclass must implement this — no default
    public abstract void process(double amount);

    // Shared helper — subclasses get this for free
    public void printReceipt(double amount) { ... }
}

class CreditCard extends Payment {
    @Override
    public void process(double amount) { /* credit card logic */ }
}
```

### Interface
```java
interface Flyable {
    void fly();                              // abstract (must implement)
    default void land() { System.out.println("Landing..."); }  // default (optional override)
    static int maxAltitude() { return 40000; }                 // static util method
}
```

| | Abstract Class | Interface |
|-|---------------|-----------|
| State (fields) | Yes | No (only constants) |
| Constructor | Yes | No |
| Multiple inheritance | No | Yes (many interfaces) |
| Access modifiers | Any | public only |

---

## Interfaces in depth

A class can implement **multiple interfaces**:

```java
class SmartPhone extends Device implements WiFi, Camera, Chargeable {
    ...
}
```

**Functional interface** — exactly one abstract method, usable as a lambda:
```java
@FunctionalInterface
interface MathOp {
    int operate(int a, int b);
}

MathOp add = (a, b) -> a + b;
```

---

## File Reference
[EncapsulationDemo.java](../src/main/java/com/javabasics/oop/EncapsulationDemo.java) | [InheritanceDemo.java](../src/main/java/com/javabasics/oop/InheritanceDemo.java) | [PolymorphismDemo.java](../src/main/java/com/javabasics/oop/PolymorphismDemo.java) | [AbstractionDemo.java](../src/main/java/com/javabasics/oop/AbstractionDemo.java) | [InterfacesDemo.java](../src/main/java/com/javabasics/oop/InterfacesDemo.java)
