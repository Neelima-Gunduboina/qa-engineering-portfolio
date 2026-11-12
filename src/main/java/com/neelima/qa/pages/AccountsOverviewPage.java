package com.neelima.qa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ParaBank Accounts Overview page (overview.htm).
 */
public class AccountsOverviewPage extends BasePage {

    private static final By HEADING = By.cssSelector("#showOverview h1.title");
    private static final By ACCOUNT_LINKS = By.cssSelector("#accountTable tbody tr td a");
    private static final By LOG_OUT = By.linkText("Log Out");

    public AccountsOverviewPage(WebDriver driver) {
        super(driver);
    }

    public String getHeading() {
        return textOf(HEADING);
    }

    public boolean isLoggedIn() {
        return !driver.findElements(LOG_OUT).isEmpty();
    }

    public List<String> getAccountIds() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(ACCOUNT_LINKS));
        return driver.findElements(ACCOUNT_LINKS).stream()
                .map(WebElement::getText)
                .map(String::trim)
                .collect(Collectors.toList());
    }

    /** Balance shown in the row for the given account, e.g. "$515.50" -> 515.50. */
    public BigDecimal getBalance(String accountId) {
        By balanceCell = By.xpath("//table[@id='accountTable']//a[normalize-space()='" + accountId
                + "']/ancestor::tr/td[2]");
        String raw = textOf(balanceCell).replace("$", "").replace(",", "");
        return new BigDecimal(raw);
    }

    public void logOut() {
        driver.findElement(LOG_OUT).click();
    }
}
