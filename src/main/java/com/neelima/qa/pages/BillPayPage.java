package com.neelima.qa.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Page object for Bill Pay page (basic fields).
 */
public class BillPayPage {
    private WebDriver driver;

    @FindBy(name = "payee.name")
    private WebElement payeeName;

    @FindBy(name = "payee.accountNumber")
    private WebElement payeeAccount;

    @FindBy(name = "amount")
    private WebElement amount;

    @FindBy(css = "input[value='Send Payment']")
    private WebElement sendPayment;

    public BillPayPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void payBill(String name, String acct, String amt) {
        payeeName.sendKeys(name);
        payeeAccount.sendKeys(acct);
        amount.sendKeys(amt);
        sendPayment.click();
    }
}
