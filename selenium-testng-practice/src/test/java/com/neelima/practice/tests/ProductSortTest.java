package com.neelima.practice.tests;

import com.neelima.practice.base.BaseTest;
import com.neelima.practice.config.Config;
import com.neelima.practice.pages.InventoryPage;
import com.neelima.practice.pages.SauceLoginPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Dropdown handling with Select, plus verifying the sort actually worked.
 * Replaces dropdownexample / DropdownExample, which selected an option but never checked the result.
 */
public class ProductSortTest extends BaseTest {

    private InventoryPage inventory;

    @BeforeMethod(alwaysRun = true, dependsOnMethods = "startBrowser")
    public void login() {
        inventory = new SauceLoginPage(driver()).open()
                .loginAs(Config.get("saucedemo.user"), Config.get("saucedemo.password"));
        Assert.assertTrue(inventory.isLoaded());
    }

    @Test(groups = {"regression", "dropdown"}, description = "Price (low to high) sorts prices ascending")
    public void sortByPriceLowToHigh() {
        inventory.sortBy("Price (low to high)");

        List<Double> actual = inventory.getPrices();
        List<Double> expected = new ArrayList<>(actual);
        Collections.sort(expected);

        Assert.assertEquals(inventory.getSelectedSortOption(), "Price (low to high)");
        Assert.assertEquals(actual, expected, "Prices are not sorted low to high");
    }

    @Test(groups = {"regression", "dropdown"}, description = "Price (high to low) sorts prices descending")
    public void sortByPriceHighToLow() {
        inventory.sortBy("Price (high to low)");

        List<Double> actual = inventory.getPrices();
        List<Double> expected = new ArrayList<>(actual);
        expected.sort(Comparator.reverseOrder());

        Assert.assertEquals(actual, expected, "Prices are not sorted high to low");
    }

    @Test(groups = {"regression", "dropdown"}, description = "Name (Z to A) sorts names descending")
    public void sortByNameZtoA() {
        inventory.sortBy("Name (Z to A)");

        List<String> actual = inventory.getProductNames();
        List<String> expected = new ArrayList<>(actual);
        expected.sort(Comparator.reverseOrder());

        Assert.assertEquals(actual, expected, "Names are not sorted Z to A");
    }
}
