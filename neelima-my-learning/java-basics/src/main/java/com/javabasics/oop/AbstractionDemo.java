package com.javabasics.oop;

/**
 * Abstraction: expose WHAT an object does, hide HOW it does it.
 * Covers: abstract classes, abstract methods, interfaces with default/static methods.
 */
public class AbstractionDemo {

    public static void main(String[] args) {

        // ── Abstract class ───────────────────────────────────────────
        System.out.println("=== Abstract Class ===");
        // Cannot instantiate: new Payment() — compile error
        Payment creditCard = new CreditCardPayment("4111-1111-1111-1111");
        Payment upi        = new UPIPayment("neelima@upi");

        creditCard.processPayment(1500.0);
        upi.processPayment(200.0);

        // ── Interface ────────────────────────────────────────────────
        System.out.println("\n=== Interface ===");
        Notification emailNotif = new EmailNotification("neelima@email.com");
        Notification smsNotif   = new SMSNotification("+1-555-0100");

        emailNotif.send("Your order has shipped!");
        smsNotif.send("OTP: 482910");

        // Default method available on all implementations
        emailNotif.logNotification();
        smsNotif.logNotification();

        // Static method on interface
        System.out.println("Max retries: " + Notification.maxRetries());

        // ── Interface vs Abstract class summary ──────────────────────
        System.out.println("\n=== Key Differences ===");
        System.out.println("Abstract class: can have state (fields), constructors, concrete methods.");
        System.out.println("Interface      : no state (pre-Java 8), all methods implicitly public.");
        System.out.println("A class can implement many interfaces but extend only one class.");
    }
}

// ── Abstract class ────────────────────────────────────────────────────────────
abstract class Payment {
    protected String provider;

    Payment(String provider) {
        this.provider = provider;
    }

    // Concrete method — shared by all subclasses
    public void showReceipt(double amount) {
        System.out.println("Receipt: " + provider + " charged $" + amount);
    }

    // Abstract method — each subclass MUST implement this
    public abstract void processPayment(double amount);
}

class CreditCardPayment extends Payment {
    private String cardNumber;

    CreditCardPayment(String cardNumber) {
        super("CreditCard");
        this.cardNumber = cardNumber;
    }

    @Override
    public void processPayment(double amount) {
        String masked = "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
        System.out.println("Processing $" + amount + " via credit card " + masked);
        showReceipt(amount);
    }
}

class UPIPayment extends Payment {
    private String upiId;

    UPIPayment(String upiId) {
        super("UPI");
        this.upiId = upiId;
    }

    @Override
    public void processPayment(double amount) {
        System.out.println("Processing $" + amount + " via UPI ID: " + upiId);
        showReceipt(amount);
    }
}

// ── Interface ─────────────────────────────────────────────────────────────────
interface Notification {
    // Abstract method — implementors must define this
    void send(String message);

    // Default method — free implementation, can be overridden
    default void logNotification() {
        System.out.println("[LOG] Notification sent via " + getClass().getSimpleName());
    }

    // Static method — called on the interface, not the instance
    static int maxRetries() { return 3; }
}

class EmailNotification implements Notification {
    private String email;
    EmailNotification(String email) { this.email = email; }

    @Override
    public void send(String message) {
        System.out.println("Email to " + email + ": " + message);
    }
}

class SMSNotification implements Notification {
    private String phone;
    SMSNotification(String phone) { this.phone = phone; }

    @Override
    public void send(String message) {
        System.out.println("SMS to " + phone + ": " + message);
    }
}
