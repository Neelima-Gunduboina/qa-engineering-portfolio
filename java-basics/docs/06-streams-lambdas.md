# 06 — Lambdas & Streams

Introduced in Java 8. Enables **functional-style** programming — concise, expressive data processing.

---

## Lambda Expressions

A lambda is a short anonymous function.

```java
// Before (anonymous class)
Runnable r = new Runnable() {
    @Override public void run() { System.out.println("Hello"); }
};

// After (lambda)
Runnable r = () -> System.out.println("Hello");

// With parameters
Comparator<String> byLen = (a, b) -> a.length() - b.length();
```

Syntax: `(parameters) -> expression` or `(parameters) -> { statements; }`

---

## Built-in Functional Interfaces

| Interface | Method | Use |
|-----------|--------|-----|
| `Predicate<T>` | `boolean test(T t)` | filter / check |
| `Function<T,R>` | `R apply(T t)` | transform |
| `Consumer<T>` | `void accept(T t)` | act on value |
| `Supplier<T>` | `T get()` | produce value |
| `BiFunction<T,U,R>` | `R apply(T t, U u)` | two inputs |

```java
Predicate<Integer> isEven   = n -> n % 2 == 0;
Function<String, Integer> len = String::length;
Consumer<String> print       = System.out::println;
Supplier<List<String>> make  = ArrayList::new;
```

---

## Method References

Shorter form of a lambda that just calls an existing method:

```java
// Static method
names.stream().map(String::toUpperCase)

// Instance method on a specific object
String prefix = "Hello, ";
Function<String, String> greet = prefix::concat;

// Instance method on the argument type
names.stream().map(String::length)

// Constructor
Function<String, StringBuilder> sb = StringBuilder::new;
```

---

## Stream API

A **Stream** processes a sequence of elements via a pipeline:  
`source → intermediate operations → terminal operation`

```
List.stream()
    .filter(...)        // intermediate — lazy
    .map(...)           // intermediate — lazy
    .collect(...)       // terminal — triggers execution
```

### Common Operations

```java
List<Integer> nums = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// filter — keep matching elements
nums.stream().filter(n -> n % 2 == 0).collect(toList());
// → [2, 4, 6, 8, 10]

// map — transform each element
nums.stream().map(n -> n * n).collect(toList());
// → [1, 4, 9, 16, 25, 36, 49, 64, 81, 100]

// reduce — aggregate to one value
int sum = nums.stream().reduce(0, Integer::sum);   // 55

// sorted, distinct, limit, skip
nums.stream().distinct().sorted().limit(5).collect(toList());

// anyMatch / allMatch / noneMatch
nums.stream().anyMatch(n -> n > 9);   // true
nums.stream().allMatch(n -> n > 0);   // true

// count, min, max
nums.stream().count();
nums.stream().min(Integer::compareTo);   // Optional<Integer>
nums.stream().max(Integer::compareTo);   // Optional<Integer>

// flatMap — flatten nested collections
List<List<Integer>> nested = ...;
nested.stream().flatMap(Collection::stream).collect(toList());
```

### Collectors

```java
// join strings
names.stream().collect(Collectors.joining(", ", "[", "]"));

// group by
Map<String, List<Student>> byMajor =
    students.stream().collect(Collectors.groupingBy(Student::getMajor));

// count per group
Map<String, Long> counts =
    students.stream().collect(Collectors.groupingBy(Student::getMajor, Collectors.counting()));

// partition (true/false split)
Map<Boolean, List<Integer>> evensOdds =
    nums.stream().collect(Collectors.partitioningBy(n -> n % 2 == 0));

// toMap
Map<String, Integer> nameLengths =
    names.stream().collect(Collectors.toMap(s -> s, String::length));
```

---

## Optional

Avoids `NullPointerException` by explicitly modelling "might be absent":

```java
Optional<String> opt = Optional.of("Hello");
Optional<String> empty = Optional.empty();

opt.isPresent();                        // true
opt.get();                              // "Hello"
empty.orElse("Default");               // "Default"
opt.map(String::toUpperCase).orElse(""); // "HELLO"
```

---

## File Reference
[LambdaAndStreamsDemo.java](../src/main/java/com/javabasics/streams/LambdaAndStreamsDemo.java)
