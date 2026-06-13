package com.neelima.qa.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Page object for the Transfer Funds page.
 */
public class FundTransferPage {
    private WebDriver driver;

    @FindBy(id = "amount")
    private WebElement amountField;

    @FindBy(id = "fromAccountId")
    private WebElement fromAccount;

    @FindBy(id = "toAccountId")
    private WebElement toAccount;

    @FindBy(css = "input[value='Transfer']")
    private WebElement transferButton;

    public FundTransferPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    /**
     * Performs a fund transfer and returns the confirmation text.
     */
    public String transfer(String fromAcct, String toAcct, String amount) {
        try {
            fromAccount.sendKeys(fromAcct);
        } catch (Exception ignore) {}
        try {
            toAccount.sendKeys(toAcct);
        } catch (Exception ignore) {}
        amountField.clear();
        amountField.sendKeys(amount);
        transferButton.click();
        return driver.getPageSource();
    }
}
