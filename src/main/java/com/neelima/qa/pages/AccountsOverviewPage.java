package com.neelima.qa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

/**
 * Page object for the Accounts Overview page.
 */
public class AccountsOverviewPage {
    private WebDriver driver;

    public AccountsOverviewPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    /**
     * Returns the first account id found on the overview page.
     */
    public String getFirstAccountId() {
        // Parabank lists account links; try common selector
        List<WebElement> links = driver.findElements(By.cssSelector("#accountTable a, #rightPanel a"));
        if (!links.isEmpty()) return links.get(0).getText().trim();
        // fallback: try table rows
        List<WebElement> rows = driver.findElements(By.cssSelector("table#accountTable tbody tr td a"));
        if (!rows.isEmpty()) return rows.get(0).getText().trim();
        return null;
    }

    public String getPageText() {
        return driver.getPageSource();
    }
}
