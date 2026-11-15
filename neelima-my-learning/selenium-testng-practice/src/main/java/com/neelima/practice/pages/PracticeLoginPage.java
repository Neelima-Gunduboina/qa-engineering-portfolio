package com.neelima.practice.pages;

import com.neelima.practice.config.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Page object for https://practicetestautomation.com/practice-test-login/.
 * Used to demonstrate the different locator strategies (ID, Name, XPath, CSS).
 */
public class PracticeLoginPage extends BasePage {

    private static final By SUCCESS_HEADING = By.cssSelector("h1.post-title");
    private static final By LOGOUT_BUTTON = By.linkText("Log out");
    private static final By ERROR = By.id("error");

    public PracticeLoginPage(WebDriver driver) {
        super(driver);
    }

    public PracticeLoginPage open() {
        driver.get(Config.get("practice.login.url"));
        return this;
    }

    /**
     * Logs in using whichever locators the caller supplies, so a single test can
     * prove that ID, XPath and CSS selectors all identify the same elements.
     */
    public PracticeLoginPage login(By username, By password, By submit, String user, String pass) {
        type(username, user);
        type(password, pass);
        click(submit);
        return this;
    }

    public String getSuccessHeading() {
        wait.until(ExpectedConditions.urlContains("logged-in-successfully"));
        return text(SUCCESS_HEADING);
    }

    public boolean isLogoutVisible() {
        return isDisplayed(LOGOUT_BUTTON);
    }

    public String getErrorMessage() {
        return text(ERROR);
    }
}
