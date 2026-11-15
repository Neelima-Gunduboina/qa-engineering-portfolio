package com.neelima.qa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * ParaBank login panel (left sidebar on index.htm).
 */
public class LoginPage extends BasePage {

    private static final By ERROR = By.cssSelector("#rightPanel p.error");

    @FindBy(name = "username")
    private WebElement username;

    @FindBy(name = "password")
    private WebElement password;

    @FindBy(css = "input[value='Log In']")
    private WebElement loginButton;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public AccountsOverviewPage login(String user, String pass) {
        submit(user, pass);
        return new AccountsOverviewPage(driver);
    }

    /** Submits credentials that are expected to be rejected. */
    public LoginPage attemptInvalidLogin(String user, String pass) {
        submit(user, pass);
        return this;
    }

    public String getErrorMessage() {
        return textOf(ERROR);
    }

    private void submit(String user, String pass) {
        type(username, user);
        type(password, pass);
        click(loginButton);
    }
}
