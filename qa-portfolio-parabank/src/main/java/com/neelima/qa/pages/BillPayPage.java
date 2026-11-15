package com.neelima.qa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * ParaBank Bill Pay page (billpay.htm).
 */
public class BillPayPage extends BasePage {

    private static final By RESULT_HEADING = By.cssSelector("#billpayResult h1.title");
    private static final By RESULT_PAYEE = By.cssSelector("#billpayResult #payeeName");
    private static final By RESULT_AMOUNT = By.cssSelector("#billpayResult #amount");

    @FindBy(name = "payee.name")
    private WebElement payeeName;
    @FindBy(name = "payee.address.street")
    private WebElement street;
    @FindBy(name = "payee.address.city")
    private WebElement city;
    @FindBy(name = "payee.address.state")
    private WebElement state;
    @FindBy(name = "payee.address.zipCode")
    private WebElement zipCode;
    @FindBy(name = "payee.phoneNumber")
    private WebElement phone;
    @FindBy(name = "payee.accountNumber")
    private WebElement accountNumber;
    @FindBy(name = "verifyAccount")
    private WebElement verifyAccount;
    @FindBy(name = "amount")
    private WebElement amount;
    @FindBy(css = "input[value='Send Payment']")
    private WebElement sendPayment;

    public BillPayPage(WebDriver driver) {
        super(driver);
    }

    public BillPayPage payBill(String payee, String payeeAccount, String value, String fromAccountId) {
        type(payeeName, payee);
        type(street, "10 Downing Street");
        type(city, "London");
        type(state, "LDN");
        type(zipCode, "SW1A2AA");
        type(phone, "02070000000");
        type(accountNumber, payeeAccount);
        type(verifyAccount, payeeAccount);
        type(amount, value);
        loadedSelect("select[name='fromAccountId']").selectByVisibleText(fromAccountId);
        click(sendPayment);
        return this;
    }

    public String getResultHeading() {
        return textOf(RESULT_HEADING);
    }

    public String getResultPayee() {
        return textOf(RESULT_PAYEE);
    }

    public String getResultAmount() {
        return textOf(RESULT_AMOUNT);
    }
}
