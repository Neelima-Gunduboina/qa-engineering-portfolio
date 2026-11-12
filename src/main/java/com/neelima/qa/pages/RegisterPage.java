package com.neelima.qa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.Map;

/**
 * ParaBank registration page (register.htm).
 */
public class RegisterPage extends BasePage {

    private static final By WELCOME_HEADING = By.cssSelector("#rightPanel h1.title");
    private static final By WELCOME_TEXT = By.cssSelector("#rightPanel p");
    private static final By USERNAME_ERROR = By.id("customer.username.errors");

    @FindBy(id = "customer.firstName")
    private WebElement firstName;
    @FindBy(id = "customer.lastName")
    private WebElement lastName;
    @FindBy(id = "customer.address.street")
    private WebElement street;
    @FindBy(id = "customer.address.city")
    private WebElement city;
    @FindBy(id = "customer.address.state")
    private WebElement state;
    @FindBy(id = "customer.address.zipCode")
    private WebElement zipCode;
    @FindBy(id = "customer.phoneNumber")
    private WebElement phone;
    @FindBy(id = "customer.ssn")
    private WebElement ssn;
    @FindBy(id = "customer.username")
    private WebElement username;
    @FindBy(id = "customer.password")
    private WebElement password;
    @FindBy(id = "repeatedPassword")
    private WebElement repeatedPassword;
    @FindBy(css = "input[value='Register']")
    private WebElement registerButton;

    public RegisterPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Fills and submits the form. Keys match the fields in testdata/registerData.json.
     */
    public RegisterPage register(Map<String, String> customer) {
        type(firstName, customer.get("firstName"));
        type(lastName, customer.get("lastName"));
        type(street, customer.get("address"));
        type(city, customer.get("city"));
        type(state, customer.get("state"));
        type(zipCode, customer.get("zipCode"));
        type(phone, customer.get("phone"));
        type(ssn, customer.get("ssn"));
        type(username, customer.get("username"));
        type(password, customer.get("password"));
        type(repeatedPassword, customer.get("password"));
        click(registerButton);
        return this;
    }

    public String getWelcomeHeading() {
        return textOf(WELCOME_HEADING);
    }

    public String getWelcomeText() {
        return textOf(WELCOME_TEXT);
    }

    public String getUsernameError() {
        return textOf(USERNAME_ERROR);
    }
}
