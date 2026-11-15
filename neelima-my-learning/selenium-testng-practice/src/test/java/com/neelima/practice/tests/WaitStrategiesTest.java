package com.neelima.practice.tests;

import com.neelima.practice.base.BaseTest;
import com.neelima.practice.config.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

/**
 * Side-by-side examples of implicit, explicit and fluent waits — none of them use Thread.sleep().
 * Replaces implicitwait, explicitwait, ImplicitWaitExample, ExplicitWaitExample and ExplicitWaitLoginTest.
 */
public class WaitStrategiesTest extends BaseTest {

    private static final By USERNAME = By.id("user-name");
    private static final By PASSWORD = By.id("password");
    private static final By LOGIN = By.id("login-button");
    private static final By PRODUCTS_TITLE = By.cssSelector("[data-test='title']");

    @Test(groups = {"regression", "waits"}, description = "Implicit wait: driver polls on every findElement")
    public void loginWithImplicitWait() {
        WebDriver driver = driver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get(Config.get("saucedemo.url"));

        driver.findElement(USERNAME).sendKeys(Config.get("saucedemo.user"));
        driver.findElement(PASSWORD).sendKeys(Config.get("saucedemo.password"));
        driver.findElement(LOGIN).click();

        Assert.assertEquals(driver.findElement(PRODUCTS_TITLE).getText(), "Products");
    }

    @Test(groups = {"regression", "waits"}, description = "Explicit wait: wait for a specific condition")
    public void loginWithExplicitWait() {
        WebDriver driver = driver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(Config.get("saucedemo.url"));

        wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME)).sendKeys(Config.get("saucedemo.user"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(PASSWORD)).sendKeys(Config.get("saucedemo.password"));
        wait.until(ExpectedConditions.elementToBeClickable(LOGIN)).click();

        Assert.assertTrue(wait.until(ExpectedConditions.urlContains("inventory")));
        Assert.assertEquals(wait.until(ExpectedConditions.visibilityOfElementLocated(PRODUCTS_TITLE)).getText(),
                "Products");
    }

    @Test(groups = {"regression", "waits"}, description = "Fluent wait: custom polling interval and ignored exceptions")
    public void loginWithFluentWait() {
        WebDriver driver = driver();
        driver.get(Config.get("saucedemo.url"));

        FluentWait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofMillis(250))
                .ignoring(NoSuchElementException.class);

        wait.until(d -> d.findElement(USERNAME)).sendKeys(Config.get("saucedemo.user"));
        wait.until(d -> d.findElement(PASSWORD)).sendKeys(Config.get("saucedemo.password"));
        wait.until(d -> d.findElement(LOGIN)).click();

        String title = wait.until(d -> d.findElement(PRODUCTS_TITLE)).getText();
        Assert.assertEquals(title, "Products");
    }
}
