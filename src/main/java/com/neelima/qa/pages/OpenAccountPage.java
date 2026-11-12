package com.neelima.qa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * ParaBank Open New Account page (openaccount.htm).
 */
public class OpenAccountPage extends BasePage {

    private static final By RESULT_HEADING = By.cssSelector("#openAccountResult h1.title");
    private static final By NEW_ACCOUNT_ID = By.id("newAccountId");

    @FindBy(css = "input[value='Open New Account']")
    private WebElement openButton;

    public OpenAccountPage(WebDriver driver) {
        super(driver);
    }

    /**
     * @param type "CHECKING" or "SAVINGS"
     * @return the id of the newly opened account
     */
    public String openAccount(String type, String fundingAccountId) {
        loadedSelect("#type").selectByVisibleText(type);
        loadedSelect("#fromAccountId").selectByVisibleText(fundingAccountId);
        click(openButton);
        wait.until(ExpectedConditions.textToBe(RESULT_HEADING, "Account Opened!"));
        return textOf(NEW_ACCOUNT_ID);
    }
}
