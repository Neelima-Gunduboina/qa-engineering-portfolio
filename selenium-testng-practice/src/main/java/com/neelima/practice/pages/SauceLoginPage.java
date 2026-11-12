package com.neelima.practice.pages;

import com.neelima.practice.config.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page object for the SauceDemo login page (https://www.saucedemo.com).
 */
public class SauceLoginPage extends BasePage {

    private static final By USERNAME = By.id("user-name");
    private static final By PASSWORD = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']");

    public SauceLoginPage(WebDriver driver) {
        super(driver);
    }

    public SauceLoginPage open() {
        driver.get(Config.get("saucedemo.url"));
        visible(USERNAME);
        return this;
    }

    public SauceLoginPage enterUsername(String username) {
        type(USERNAME, username);
        return this;
    }

    public SauceLoginPage enterPassword(String password) {
        type(PASSWORD, password);
        return this;
    }

    public void clickLogin() {
        click(LOGIN_BUTTON);
    }

    /** Happy-path login that lands on the inventory page. */
    public InventoryPage loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        return new InventoryPage(driver);
    }

    /** Login attempt that is expected to fail; stays on this page. */
    public SauceLoginPage attemptLogin(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        return this;
    }

    public String getErrorMessage() {
        return text(ERROR_MESSAGE);
    }

    public boolean isErrorDisplayed() {
        return isDisplayed(ERROR_MESSAGE);
    }
}
