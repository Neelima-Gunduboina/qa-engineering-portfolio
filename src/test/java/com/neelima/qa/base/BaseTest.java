package com.neelima.qa.base;

import com.neelima.qa.utils.ConfigLoader;
import com.neelima.qa.utils.UniqueNames;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * Starts a browser before each UI test and quits it afterwards.
 * ThreadLocal keeps drivers isolated when TestNG runs tests in parallel.
 * Selenium Manager downloads the matching ChromeDriver automatically.
 */
public abstract class BaseTest {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        String browser = ConfigLoader.get("browser", "chrome");
        if (!"chrome".equalsIgnoreCase(browser)) {
            throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
        ChromeOptions options = new ChromeOptions();
        if (Boolean.parseBoolean(ConfigLoader.get("headless", "true"))) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080");
        DRIVER.set(new ChromeDriver(options));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }

    public WebDriver getDriver() {
        return DRIVER.get();
    }

    protected void open(String path) {
        getDriver().get(ConfigLoader.get("base.url") + path);
    }

    /** A unique customer built from the JSON template, so tests never collide on username. */
    protected static Map<String, String> uniqueCustomer(Map<String, Object> template) {
        Map<String, String> customer = new HashMap<>();
        template.forEach((k, v) -> customer.put(k, String.valueOf(v)));
        customer.put("username", customer.get("username").replace("${unique}", UniqueNames.username("ui")));
        return customer;
    }
}
