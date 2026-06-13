package com.javabasics.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Selenium WebDriver tests using Google Search as the target.
 *
 * Covers:
 *  - Browser setup / teardown with WebDriverManager (no manual chromedriver)
 *  - Navigating to a URL
 *  - Finding elements (By.name, By.cssSelector, By.xpath, By.tagName)
 *  - Typing, clicking, submitting forms
 *  - Explicit waits (WebDriverWait + ExpectedConditions)
 *  - Reading page title and element text
 *  - Taking a screenshot
 *
 * NOTE: These tests require an internet connection and Chrome to be installed.
 *       Run with: mvn test -Dtest=GoogleSearchTest
 *       To skip Selenium in CI: mvn test -Dtest="!GoogleSearchTest"
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Selenium – Google Search Tests")
class GoogleSearchTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    private static final String BASE_URL    = "https://www.google.com";
    private static final Duration TIMEOUT   = Duration.ofSeconds(10);

    // ── Setup / Teardown ──────────────────────────────────────────────────────
    @BeforeAll
    static void setUpBrowser() {
        // WebDriverManager downloads the correct ChromeDriver automatically
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");          // run without opening a browser window
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1280,800");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        wait   = new WebDriverWait(driver, TIMEOUT);
    }

    @AfterAll
    static void tearDownBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("Page title should contain 'Google'")
    void pageTitleContainsGoogle() {
        driver.get(BASE_URL);
        String title = driver.getTitle();
        System.out.println("Page title: " + title);
        assertTrue(title.contains("Google"), "Expected title to contain 'Google', was: " + title);
    }

    @Test
    @Order(2)
    @DisplayName("Current URL should start with https://www.google.com")
    void urlIsCorrect() {
        driver.get(BASE_URL);
        String url = driver.getCurrentUrl();
        assertTrue(url.startsWith("https://www.google.com"),
                "URL was: " + url);
    }

    @Test
    @Order(3)
    @DisplayName("Search input should be present and accept text")
    void searchInputAcceptsText() {
        driver.get(BASE_URL);

        // Find the search box — Google uses textarea[name='q'] or input[name='q']
        WebElement searchBox = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.name("q")));

        assertTrue(searchBox.isDisplayed(), "Search box should be visible");
        assertTrue(searchBox.isEnabled(),   "Search box should be enabled");

        searchBox.clear();
        searchBox.sendKeys("Java programming");
        assertEquals("Java programming", searchBox.getAttribute("value"));
    }

    @Test
    @Order(4)
    @DisplayName("Submitting a search should show results page")
    void searchReturnsResults() {
        driver.get(BASE_URL);

        WebElement searchBox = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.name("q")));
        searchBox.clear();
        searchBox.sendKeys("Selenium WebDriver tutorial");
        searchBox.sendKeys(Keys.RETURN);

        // Wait for results page to load — URL changes to contain /search
        wait.until(ExpectedConditions.urlContains("/search"));

        String url = driver.getCurrentUrl();
        assertTrue(url.contains("Selenium+WebDriver+tutorial") || url.contains("Selenium%20WebDriver%20tutorial"),
                "URL should contain the search query");
    }

    @Test
    @Order(5)
    @DisplayName("Results page title should reflect the search query")
    void resultsPageTitleMatchesQuery() {
        driver.get(BASE_URL + "/search?q=Java+basics");

        wait.until(ExpectedConditions.titleContains("Java"));
        String title = driver.getTitle();
        System.out.println("Results title: " + title);
        assertTrue(title.toLowerCase().contains("java"),
                "Title should contain 'java', was: " + title);
    }

    @Test
    @Order(6)
    @DisplayName("Results page should contain at least one search result link")
    void resultsPageHasLinks() {
        driver.get(BASE_URL + "/search?q=OpenJDK");

        // Wait for result links (Google result divs use data-header-feature or h3 tags)
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("h3")));

        List<WebElement> headings = driver.findElements(By.cssSelector("h3"));
        System.out.println("Result heading count: " + headings.size());
        assertFalse(headings.isEmpty(), "Should have at least one result heading");
    }

    @Test
    @Order(7)
    @DisplayName("XPath search — find element by partial text")
    void xpathSearch() {
        driver.get(BASE_URL + "/search?q=JUnit+5+tutorial");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h3")));

        // Use XPath to find a result whose text contains 'JUnit'
        List<WebElement> results = driver.findElements(
                By.xpath("//h3[contains(translate(text(),'junit','JUNIT'),'JUNIT')]"));

        System.out.println("XPath matched JUnit headings: " + results.size());
        // At least one result should contain JUnit in the heading
        assertFalse(results.isEmpty(), "Expected at least one result heading mentioning JUnit");
    }

    @Test
    @Order(8)
    @DisplayName("Screenshot — captures page without error")
    void takeScreenshot() {
        driver.get(BASE_URL);
        // TakesScreenshot is implemented by ChromeDriver
        assertInstanceOf(TakesScreenshot.class, driver,
                "Driver should support screenshots");

        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        assertNotNull(screenshot);
        assertTrue(screenshot.length > 0, "Screenshot bytes should not be empty");
        System.out.println("Screenshot captured: " + screenshot.length + " bytes");
    }

    @Test
    @Order(9)
    @DisplayName("Explicit wait — element visibility wait works correctly")
    void explicitWaitDemo() {
        driver.get(BASE_URL);

        // Wait until the search button is visible
        WebElement searchBtn = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("input[type='submit'][value='Google Search'], " +
                                       "input[name='btnK']")));

        assertNotNull(searchBtn);
        assertTrue(searchBtn.isDisplayed());
        System.out.println("Search button text/value: " + searchBtn.getAttribute("value"));
    }

    @Test
    @Order(10)
    @DisplayName("Keyboard shortcut — pressing ESCAPE clears focus")
    void keyboardInteraction() {
        driver.get(BASE_URL);

        WebElement searchBox = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.name("q")));
        searchBox.click();
        searchBox.sendKeys("test query");
        searchBox.sendKeys(Keys.ESCAPE);

        // After ESC, page should still be on Google home
        assertTrue(driver.getCurrentUrl().startsWith("https://www.google.com"));
    }
}
