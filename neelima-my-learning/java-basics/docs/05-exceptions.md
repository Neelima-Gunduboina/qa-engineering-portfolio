# 05 — Exception Handling

Exceptions are events that disrupt normal program flow. Java uses a structured `try/catch/finally` mechanism.

---

## Exception Hierarchy

```
Throwable
├── Error          → JVM errors (OutOfMemoryError) — don't catch these
└── Exception
    ├── IOException, SQLException, ...   ← Checked (must handle or declare)
    └── RuntimeException
        ├── NullPointerException
        ├── ArrayIndexOutOfBoundsException
        ├── NumberFormatException
        ├── ArithmeticException
        └── ...                          ← Unchecked (optional to handle)
```

**Checked** — compiler forces you to either `catch` or `throws`.  
**Unchecked** — subclass of `RuntimeException`, optional to handle.

---

## try / catch / finally

```java
try {
    int result = 10 / 0;             // throws ArithmeticException
} catch (ArithmeticException e) {
    System.out.println("Error: " + e.getMessage());
} finally {
    System.out.println("This always runs — cleanup here");
}
```

---

## Multi-catch

```java
try {
    String s = null;
    int n = Integer.parseInt(s);
} catch (NumberFormatException | NullPointerException e) {
    System.out.println("Caught: " + e.getClass().getSimpleName());
}
```

---

## Checked Exceptions — throws declaration

```java
// Declare the exception so callers know to handle it
void readFile(String path) throws IOException {
    throw new IOException("File not found: " + path);
}

// Caller must handle it
try {
    readFile("/missing.txt");
} catch (IOException e) {
    System.out.println(e.getMessage());
}
```

---

## try-with-resources

Auto-closes any `AutoCloseable` resource (files, DB connections, etc.).

```java
try (FileReader reader = new FileReader("data.txt")) {
    // reader is automatically closed when the block exits
} catch (IOException e) {
    System.out.println("Error: " + e.getMessage());
}
```

---

## Custom Exceptions

```java
// Custom checked exception
class InsufficientFundsException extends Exception {
    private double amount;

    InsufficientFundsException(double amount) {
        super("Insufficient funds: need $" + amount);
        this.amount = amount;
    }

    public double getAmount() { return amount; }
}

// Throwing it
void withdraw(double amount) throws InsufficientFundsException {
    if (amount > balance) {
        throw new InsufficientFundsException(amount);
    }
    balance -= amount;
}
```

---

## Exception Chaining

Wrap a low-level exception inside a higher-level one to preserve the root cause:

```java
try {
    // low-level operation
} catch (IOException e) {
    throw new RuntimeException("Config load failed", e);   // e is the cause
}

// Later, to inspect:
e.getCause();    // returns the original IOException
```

---

## Best Practices

| Do | Avoid |
|----|-------|
| Catch the most specific exception first | `catch (Exception e)` as the only catch |
| Always log or handle, never silently swallow | Empty `catch {}` blocks |
| Use custom exceptions for domain errors | Overusing checked exceptions |
| Close resources with try-with-resources | Manual `finally { resource.close() }` |

---

## File Reference
[ExceptionHandlingDemo.java](../src/main/java/com/javabasics/exceptions/ExceptionHandlingDemo.java)
