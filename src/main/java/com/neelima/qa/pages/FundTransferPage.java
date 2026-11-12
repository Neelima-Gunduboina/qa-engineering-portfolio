package com.neelima.qa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * ParaBank Transfer Funds page (transfer.htm).
 */
public class FundTransferPage extends BasePage {

    private static final By RESULT_HEADING = By.cssSelector("#showResult h1.title");
    private static final By AMOUNT_RESULT = By.id("amountResult");
    private static final By FROM_RESULT = By.id("fromAccountIdResult");
    private static final By TO_RESULT = By.id("toAccountIdResult");

    @FindBy(id = "amount")
    private WebElement amount;

    @FindBy(css = "input[value='Transfer']")
    private WebElement transferButton;

    public FundTransferPage(WebDriver driver) {
        super(driver);
    }

    public FundTransferPage transfer(String fromAccountId, String toAccountId, String value) {
        type(amount, value);
        loadedSelect("#fromAccountId").selectByVisibleText(fromAccountId);
        loadedSelect("#toAccountId").selectByVisibleText(toAccountId);
        click(transferButton);
        return this;
    }

    public String getResultHeading() {
        return textOf(RESULT_HEADING);
    }

    public String getAmountResult() {
        return textOf(AMOUNT_RESULT);
    }

    public String getFromAccountResult() {
        return textOf(FROM_RESULT);
    }

    public String getToAccountResult() {
        return textOf(TO_RESULT);
    }
}
