package com.neelima.qa.base;

import com.neelima.qa.utils.ConfigLoader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * BaseTest sets up and tears down WebDriver instances in a thread-safe manner.
 */
public class BaseTest {
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        String browser = ConfigLoader.get("browser", "chrome");
        boolean headless = Boolean.parseBoolean(ConfigLoader.get("headless", "true"));
        if ("chrome".equalsIgnoreCase(browser)) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            if (headless) options.addArguments("--headless=new");
            options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--disable-extensions", "--window-size=1920,1080");
            DRIVER.set(new ChromeDriver(options));
        } else {
            throw new RuntimeException("Unsupported browser: " + browser);
        }
    }

    public WebDriver getDriver() {
        return DRIVER.get();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }
}
