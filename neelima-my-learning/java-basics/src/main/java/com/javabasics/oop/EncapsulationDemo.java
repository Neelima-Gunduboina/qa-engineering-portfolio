package com.javabasics.oop;

/**
 * Encapsulation: hide internal state behind private fields + public getters/setters.
 * Rule: fields are private; access only through controlled methods.
 */
public class EncapsulationDemo {

    public static void main(String[] args) {
        BankAccount account = new BankAccount("ACC001", "Neelima", 1000.0);

        System.out.println("=== Encapsulation Demo ===");
        System.out.println("Owner  : " + account.getOwner());
        System.out.println("Balance: " + account.getBalance());

        account.deposit(500.0);
        System.out.println("After deposit 500: " + account.getBalance());

        account.withdraw(200.0);
        System.out.println("After withdraw 200: " + account.getBalance());

        // Trying to overdraw — rejected by internal validation
        account.withdraw(5000.0);
        System.out.println("After bad withdraw: " + account.getBalance());

        // Cannot set negative balance from outside — setter validates
        account.setBalance(-100);
        System.out.println("After setBalance(-100): " + account.getBalance());
    }
}

class BankAccount {
    // private — cannot be accessed directly from outside
    private final String accountId;
    private final String owner;
    private double balance;

    BankAccount(String accountId, String owner, double initialBalance) {
        this.accountId = accountId;
        this.owner = owner;
        this.balance = initialBalance;
    }

    // Getters — read-only access
    public String getAccountId() { return accountId; }
    public String getOwner()     { return owner; }
    public double getBalance()   { return balance; }

    // Controlled setter — validates before changing state
    public void setBalance(double balance) {
        if (balance < 0) {
            System.out.println("ERROR: Balance cannot be negative.");
            return;
        }
        this.balance = balance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            System.out.println("Deposit amount must be positive.");
            return;
        }
        balance += amount;
        System.out.println("Deposited: " + amount);
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Withdrawal amount must be positive.");
            return;
        }
        if (amount > balance) {
            System.out.println("Insufficient funds. Available: " + balance);
            return;
        }
        balance -= amount;
        System.out.println("Withdrawn: " + amount);
    }

    @Override
    public String toString() {
        return "BankAccount[" + accountId + ", owner=" + owner + ", balance=" + balance + "]";
    }
}
