package com.neelima.qa.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Page object for the ParaBank login page.
 */
public class LoginPage {
    private WebDriver driver;

    @FindBy(name = "username")
    private WebElement username;

    @FindBy(name = "password")
    private WebElement password;

    // login button locator - fallbacks may be required if site changes
    @FindBy(css = "input[value='Log In']")
    private WebElement loginButton;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    /**
     * Performs login and returns the AccountsOverviewPage.
     */
    public AccountsOverviewPage login(String user, String pass) {
        username.clear();
        username.sendKeys(user);
        password.clear();
        password.sendKeys(pass);
        loginButton.click();
        return new AccountsOverviewPage(driver);
    }
}
