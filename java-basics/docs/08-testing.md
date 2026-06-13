# 08 — Testing Guide

This project has two types of tests: **JUnit 5** (unit tests) and **Selenium** (browser tests).

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
# All unit tests (excludes Selenium)
mvn test -Dexclude=**/selenium/**

# One specific test class
mvn test -Dtest=StudentServiceTest

# One specific method
mvn test -Dtest=StudentServiceTest#averageGpa
```

---

## Selenium — Browser Tests

File: [`GoogleSearchTest.java`](../src/test/java/com/javabasics/selenium/GoogleSearchTest.java)

### Key Components

| Component | Purpose |
|-----------|---------|
| `WebDriverManager` | Auto-downloads ChromeDriver — no manual setup |
| `ChromeOptions` | Configure browser (headless mode, window size) |
| `WebDriver` | Controls the browser |
| `WebDriverWait` | Explicit wait — waits for a condition |
| `ExpectedConditions` | Pre-built conditions (element visible, URL contains, etc.) |
| `By` | Element locator strategies |

### Locator Strategies

```java
driver.findElement(By.id("search"));
driver.findElement(By.name("q"));
driver.findElement(By.cssSelector("input[type='submit']"));
driver.findElement(By.xpath("//h3[contains(text(),'Java')]"));
driver.findElement(By.tagName("h1"));
driver.findElement(By.className("result"));
driver.findElement(By.linkText("Click here"));
```

### Explicit Wait

```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

// Wait until element is visible
WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("q")));

// Wait until URL changes
wait.until(ExpectedConditions.urlContains("/search"));

// Wait until title contains text
wait.until(ExpectedConditions.titleContains("Java"));
```

### Common Actions

```java
// Navigate
driver.get("https://www.google.com");
driver.navigate().back();
driver.navigate().forward();
driver.navigate().refresh();

// Interact
element.click();
element.sendKeys("search query");
element.sendKeys(Keys.RETURN);       // press Enter
element.clear();
element.getAttribute("value");
element.getText();
element.isDisplayed();
element.isEnabled();

// Screenshot
byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

// Browser info
driver.getTitle();
driver.getCurrentUrl();
```

### How to run Selenium tests

```bash
# Make sure Chrome is installed, then:
mvn test -Dtest=GoogleSearchTest
```

Tests run in **headless mode** by default — no browser window opens.  
To see the browser, remove the `--headless` option from `ChromeOptions` in the test file.

---

## Test Coverage Summary

| Test Class | Tests | Covers |
|-----------|-------|--------|
| `StudentServiceTest` | 25+ | CRUD, validation, search, stats, grades |
| `GoogleSearchTest` | 10 | Navigation, forms, waits, XPath, screenshot |
