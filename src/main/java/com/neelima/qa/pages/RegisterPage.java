package com.neelima.qa.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Page object for the ParaBank registration page.
 */
public class RegisterPage {
    private WebDriver driver;

    @FindBy(id = "customer.firstName")
    private WebElement firstName;

    @FindBy(id = "customer.lastName")
    private WebElement lastName;

    @FindBy(id = "customer.address.street")
    private WebElement address;

    @FindBy(id = "customer.address.city")
    private WebElement city;

    @FindBy(id = "customer.address.state")
    private WebElement state;

    @FindBy(id = "customer.address.zipCode")
    private WebElement zip;

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
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    /**
     * Registers a user with the provided details.
     */
    public void register(String fName, String lName, String addr, String cty, String st, String zipcode, String ph, String ssnVal, String user, String pass) {
        firstName.sendKeys(fName);
        lastName.sendKeys(lName);
        address.sendKeys(addr);
        city.sendKeys(cty);
        state.sendKeys(st);
        zip.sendKeys(zipcode);
        phone.sendKeys(ph);
        ssn.sendKeys(ssnVal);
        username.sendKeys(user);
        password.sendKeys(pass);
        repeatedPassword.sendKeys(pass);
        registerButton.click();
    }
}
