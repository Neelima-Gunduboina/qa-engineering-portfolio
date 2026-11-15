package com.neelima.practice.base;

import com.neelima.practice.driver.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Opens a fresh browser before each test and always closes it afterwards.
 * ThreadLocal keeps each test's driver separate when tests run in parallel.
 */
public abstract class BaseTest {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void startBrowser() {
        DRIVER.set(DriverFactory.create());
    }

    @AfterMethod(alwaysRun = true)
    public void closeBrowser() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }

    protected WebDriver driver() {
        return DRIVER.get();
    }
}
