# Java Basics — Complete Learning Project

A hands-on Java learning project covering every core concept from primitives to streams, with a full Student Management System and JUnit 5 unit tests.

> Selenium browser automation lives in the sibling project [`../selenium-testng-practice`](../selenium-testng-practice).

---

## Project Structure

```
java-basics/
├── pom.xml                              ← Maven build (JUnit 5)
├── src/
│   ├── main/java/com/javabasics/
│   │   ├── basics/                     ← Fibonacci, sum of digits, palindrome, prime, factorial
│   │   ├── fundamentals/               ← Data types, operators, type casting
│   │   ├── controlflow/                ← if/else, loops, switch
│   │   ├── oop/                        ← OOP pillars (4 concepts)
│   │   ├── collections/                ← List, Set, Map, Queue
│   │   ├── exceptions/                 ← Exception handling
│   │   ├── streams/                    ← Lambdas, Stream API, Optional
│   │   └── project/                    ← Student Management System
│   │       ├── model/Student.java
│   │       ├── repository/StudentRepository.java
│   │       ├── service/StudentService.java
│   │       └── StudentManagementApp.java
│   └── test/java/com/javabasics/
│       ├── basics/NumberProgramsTest.java     ← JUnit 5 parameterized tests
│       └── project/StudentServiceTest.java    ← JUnit 5 tests
└── docs/                               ← Detailed topic guides
```

---

## Topics Covered

| # | Topic | File |
|---|-------|------|
| 1 | Data Types & Variables | `fundamentals/DataTypesAndVariables.java` |
| 2 | Operators | `fundamentals/OperatorsDemo.java` |
| 3 | Type Casting | `fundamentals/TypeCastingDemo.java` |
| 4 | Conditionals | `controlflow/ConditionalsDemo.java` |
| 5 | Loops | `controlflow/LoopsDemo.java` |
| 6 | Encapsulation | `oop/EncapsulationDemo.java` |
| 7 | Inheritance | `oop/InheritanceDemo.java` |
| 8 | Polymorphism | `oop/PolymorphismDemo.java` |
| 9 | Abstraction | `oop/AbstractionDemo.java` |
| 10 | Interfaces | `oop/InterfacesDemo.java` |
| 11 | Collections | `collections/CollectionsDemo.java` |
| 12 | Exception Handling | `exceptions/ExceptionHandlingDemo.java` |
| 13 | Lambdas & Streams | `streams/LambdaAndStreamsDemo.java` |
| 14 | Classic number programs | `basics/NumberPrograms.java` |
| 15 | Mini Project | `project/StudentManagementApp.java` |

---

## Prerequisites

| Tool | Version |
|------|---------|
| Java | 17+ |
| Maven | 3.8+ |

---

## How to Run

### Run a specific demo class
```bash
mvn compile exec:java -Dexec.mainClass="com.javabasics.project.StudentManagementApp"
```

### Run all tests
```bash
mvn test
```

### Run one test class
```bash
mvn test -Dtest=NumberProgramsTest
```

---

## Mini Project — Student Management System

A layered application demonstrating real-world Java patterns:

- **Model** (`Student.java`) — encapsulated entity with validation and grade calculation
- **Repository** (`StudentRepository.java`) — in-memory CRUD store using `HashMap`
- **Service** (`StudentService.java`) — business logic, search, statistics using Streams
- **App** (`StudentManagementApp.java`) — entry point with full demo output

### Features
- Add / get / update / delete students
- Search by major, filter by min GPA
- Sort by name or GPA
- Statistics: average GPA, highest GPA, count by major
- Group students by letter grade (A/B/C/D/F)
- Duplicate email guard

---

## Tests Overview

### JUnit 5 (`StudentServiceTest.java`)
- 25+ test cases across 5 nested test classes
- Parameterized tests for GPA boundaries and grade mapping
- Covers CRUD, validation, search, and statistics

### JUnit 5 (`NumberProgramsTest.java`)
- Parameterized tests (`@CsvSource`, `@ValueSource`) for Fibonacci, sum of digits, reverse, palindrome, prime, factorial
- Boundary and invalid-input cases (0, negatives, overflow limits)

**70 tests in total, all passing.**

---

## Docs

Each concept has a detailed guide in [`docs/`](docs/):

- [01 — Fundamentals](docs/01-fundamentals.md)
- [02 — Control Flow](docs/02-control-flow.md)
- [03 — OOP](docs/03-oop.md)
- [04 — Collections](docs/04-collections.md)
- [05 — Exceptions](docs/05-exceptions.md)
- [06 — Streams & Lambdas](docs/06-streams-lambdas.md)
- [07 — Student Project](docs/07-project.md)
- [08 — Testing Guide](docs/08-testing.md)
