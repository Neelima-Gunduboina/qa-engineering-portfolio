# 08 — Testing Guide

This project uses **JUnit 5** unit tests. Selenium browser tests live in the sibling `selenium-testng-practice` project.

---

## JUnit 5 — Unit Tests

File: [`StudentServiceTest.java`](../src/test/java/com/javabasics/project/StudentServiceTest.java)

### Key Annotations

| Annotation | Purpose |
|-----------|---------|
| `@Test` | Marks a method as a test |
| `@BeforeEach` | Runs before every test — use for fresh setup |
| `@BeforeAll` | Runs once before all tests in the class |
| `@AfterEach` / `@AfterAll` | Cleanup after each / all tests |
| `@DisplayName` | Human-readable name in test reports |
| `@Nested` | Group related tests inside an inner class |
| `@ParameterizedTest` | Run the same test with multiple inputs |
| `@ValueSource` | Supply simple values to a parameterized test |
| `@CsvSource` | Supply multiple columns of data |

### Assertions

```java
assertEquals(expected, actual);
assertNotNull(value);
assertTrue(condition);
assertFalse(condition);
assertThrows(Exception.class, () -> { ... });
assertInstanceOf(Type.class, object);
```

### Test Structure

```java
@Test
@DisplayName("should return correct student for known id")
void getById() {
    // Arrange — set up test data
    Student added = service.addStudent("Bob", "bob@uni.edu", 21, 3.0, "Physics");

    // Act — call the method under test
    Student found = service.getStudentById(added.getId());

    // Assert — verify the result
    assertEquals("Bob", found.getName());
}
```

### Parameterized Test Example

```java
@ParameterizedTest(name = "GPA {0} → Grade {1}")
@CsvSource({ "4.0, A", "3.5, B", "2.5, C", "1.5, D", "0.5, F" })
void gradeMapping(double gpa, String expected) {
    Student s = service.addStudent("T", gpa + "@u.edu", 20, gpa, "CS");
    assertEquals(Student.Grade.valueOf(expected), s.getLetterGrade());
}
```

### How to run JUnit tests

```bash
# All unit tests
mvn test

# One specific test class
mvn test -Dtest=StudentServiceTest

# One specific method
mvn test -Dtest=StudentServiceTest#averageGpa
```

---

## Selenium — Browser Tests

Selenium tests moved to their own project: [`selenium-testng-practice`](../../selenium-testng-practice/README.md)
(TestNG, Page Object Model, locators, waits, negative tests, 21 tests).

---

## Test Coverage Summary

| Test Class | Tests | Covers |
|-----------|-------|--------|
| `StudentServiceTest` | 25+ | CRUD, validation, search, stats, grades |
| `NumberProgramsTest` | 33 | Fibonacci, digits, palindrome, prime, factorial |
