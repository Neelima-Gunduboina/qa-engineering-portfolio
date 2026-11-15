package com.javabasics.oop;

/**
 * Interfaces in depth: multiple implementation, functional interfaces,
 * interface segregation, and marker interfaces.
 */
public class InterfacesDemo {

    public static void main(String[] args) {

        // ── A class implementing multiple interfaces ───────────────────
        System.out.println("=== Multiple Interfaces ===");
        SmartPhone phone = new SmartPhone("iPhone 15");
        phone.turnOn();
        phone.connect("HomeWiFi");
        phone.takePhoto();
        phone.charge(80);
        System.out.println("Battery: " + phone.getBatteryLevel() + "%");

        // ── Functional interface + lambda ─────────────────────────────
        System.out.println("\n=== Functional Interface ===");
        MathOperation add      = (a, b) -> a + b;
        MathOperation multiply = (a, b) -> a * b;
        MathOperation power    = (a, b) -> (int) Math.pow(a, b);

        System.out.println("add(3,4)      = " + add.operate(3, 4));
        System.out.println("multiply(3,4) = " + multiply.operate(3, 4));
        System.out.println("power(2,8)    = " + power.operate(2, 8));

        // ── Marker interface ──────────────────────────────────────────
        System.out.println("\n=== Marker Interface ===");
        User regularUser = new User("Alice");
        AdminUser admin  = new AdminUser("Bob");

        printUserInfo(regularUser);
        printUserInfo(admin);
    }

    static void printUserInfo(User user) {
        System.out.print("User: " + user.getName());
        if (user instanceof AdminPrivilege) {
            System.out.print(" [ADMIN — full access]");
        }
        System.out.println();
    }
}

// ── Interfaces ────────────────────────────────────────────────────────────────
interface Switchable {
    void turnOn();
    void turnOff();
}

interface WiFiConnectable {
    void connect(String ssid);
    void disconnect();
}

interface Camera {
    void takePhoto();
    void recordVideo();
}

interface Chargeable {
    void charge(int percent);
    int getBatteryLevel();
}

// ── Class implementing 4 interfaces ──────────────────────────────────────────
abstract class SmartDevice implements Switchable, WiFiConnectable, Chargeable {
    protected String name;
    protected int batteryLevel = 50;
    protected boolean on = false;

    SmartDevice(String name) { this.name = name; }

    @Override public void turnOn()  { on = true;  System.out.println(name + " turned ON"); }
    @Override public void turnOff() { on = false; System.out.println(name + " turned OFF"); }
    @Override public void connect(String ssid) { System.out.println(name + " connected to " + ssid); }
    @Override public void disconnect()         { System.out.println(name + " disconnected"); }
    @Override public void charge(int percent)  { batteryLevel = percent; System.out.println(name + " charged to " + percent + "%"); }
    @Override public int getBatteryLevel()     { return batteryLevel; }
}

class SmartPhone extends SmartDevice implements Camera {
    SmartPhone(String name) { super(name); }

    @Override public void takePhoto()    { System.out.println(name + " 📷 photo taken"); }
    @Override public void recordVideo()  { System.out.println(name + " 🎥 video recording started"); }
}

// ── Functional interface (exactly one abstract method) ────────────────────────
@FunctionalInterface
interface MathOperation {
    int operate(int a, int b);
}

// ── Marker interface (no methods, just tags a class) ─────────────────────────
interface AdminPrivilege { }

class User {
    private String name;
    User(String name) { this.name = name; }
    public String getName() { return name; }
}

class AdminUser extends User implements AdminPrivilege {
    AdminUser(String name) { super(name); }
}
