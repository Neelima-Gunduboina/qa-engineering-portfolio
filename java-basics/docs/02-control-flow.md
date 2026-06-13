# 02 — Control Flow

Control flow decides **which code runs** and **how many times**.

---

## if / else if / else

```java
int score = 85;

if (score >= 90) {
    System.out.println("Grade: A");
} else if (score >= 80) {
    System.out.println("Grade: B");   // ← this runs
} else {
    System.out.println("Grade: F");
}
```

---

## switch Statement

Use when comparing **one variable** against many constant values.

```java
String day = "MONDAY";
switch (day) {
    case "MONDAY":
    case "FRIDAY":
        System.out.println("Short meeting day");
        break;
    case "WEDNESDAY":
        System.out.println("Long meeting day");
        break;
    default:
        System.out.println("Normal day");
}
```

> **Always add `break`** unless you intentionally want fall-through.

### Switch Expression (Java 14+) — preferred

```java
String type = switch (day) {
    case "SATURDAY", "SUNDAY" -> "Weekend";
    default                   -> "Weekday";
};
```

---

## Loops

### for — when you know the count
```java
for (int i = 0; i < 5; i++) {
    System.out.println(i);
}
```

### while — when you don't know the count
```java
int n = 1;
while (n <= 100) {
    n *= 2;
}
```

### do-while — runs **at least once**
```java
int input;
do {
    input = getInput();    // always asks at least once
} while (input < 0);
```

### Enhanced for-each — iterating collections/arrays
```java
int[] nums = {10, 20, 30};
for (int n : nums) {
    System.out.println(n);
}
```

---

## break and continue

```java
// break — exits the loop entirely
for (int i = 0; i < 10; i++) {
    if (i == 5) break;    // stops at 5
    System.out.print(i);  // prints 0 1 2 3 4
}

// continue — skips the current iteration
for (int i = 0; i < 10; i++) {
    if (i % 2 == 0) continue;  // skip even
    System.out.print(i);        // prints 1 3 5 7 9
}
```

---

## Labeled break (exit nested loops)

```java
outer:
for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
        if (i == 1 && j == 1) break outer;  // exits BOTH loops
    }
}
```

---

## File Reference
See: [ConditionalsDemo.java](../src/main/java/com/javabasics/controlflow/ConditionalsDemo.java) | [LoopsDemo.java](../src/main/java/com/javabasics/controlflow/LoopsDemo.java)
