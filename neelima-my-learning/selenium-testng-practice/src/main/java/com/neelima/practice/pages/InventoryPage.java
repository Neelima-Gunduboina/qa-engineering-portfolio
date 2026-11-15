package com.neelima.practice.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page object for the SauceDemo products (inventory) page.
 */
public class InventoryPage extends BasePage {

    private static final By PAGE_TITLE = By.cssSelector("[data-test='title']");
    private static final By SORT_DROPDOWN = By.className("product_sort_container");
    private static final By ITEM_PRICES = By.className("inventory_item_price");
    private static final By ITEM_NAMES = By.className("inventory_item_name");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        wait.until(ExpectedConditions.urlContains("inventory"));
        return "Products".equals(text(PAGE_TITLE));
    }

    public InventoryPage sortBy(String visibleText) {
        new Select(visible(SORT_DROPDOWN)).selectByVisibleText(visibleText);
        return this;
    }

    public String getSelectedSortOption() {
        return new Select(visible(SORT_DROPDOWN)).getFirstSelectedOption().getText();
    }

    public List<Double> getPrices() {
        visible(ITEM_PRICES);
        return driver.findElements(ITEM_PRICES).stream()
                .map(WebElement::getText)
                .map(price -> Double.parseDouble(price.replace("$", "")))
                .collect(Collectors.toList());
    }

    public List<String> getProductNames() {
        visible(ITEM_NAMES);
        return driver.findElements(ITEM_NAMES).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }
}
