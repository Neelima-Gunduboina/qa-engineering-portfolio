package com.neelima.practice.tests;

import com.neelima.practice.base.BaseTest;
import com.neelima.practice.config.Config;
import com.neelima.practice.pages.InventoryPage;
import com.neelima.practice.pages.SauceLoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Positive login scenarios using the Page Object Model.
 * Replaces BasicLoginTest, SauceDemoLogin, loginexp, TestngAssert, TextBoxActions and pompage.
 */
public class SauceLoginTest extends BaseTest {

    @Test(groups = {"smoke", "login"}, description = "Standard user logs in and lands on Products page")
    public void standardUserCanLogin() {
        InventoryPage inventory = new SauceLoginPage(driver()).open()
                .loginAs(Config.get("saucedemo.user"), Config.get("saucedemo.password"));

        Assert.assertTrue(inventory.isLoaded(), "Products page did not load after login");
        Assert.assertTrue(inventory.getCurrentUrl().endsWith("/inventory.html"));
        Assert.assertEquals(inventory.getProductNames().size(), 6, "Unexpected number of products");
    }

    @Test(groups = {"regression", "login"}, description = "Text box can be cleared and re-typed before login")
    public void clearAndRetypeUsernameBeforeLogin() {
        SauceLoginPage login = new SauceLoginPage(driver()).open();
        login.enterUsername("wrong_user");
        login.enterUsername(Config.get("saucedemo.user")); // type() clears the field first

        InventoryPage inventory = login.loginAs(Config.get("saucedemo.user"), Config.get("saucedemo.password"));

        Assert.assertTrue(inventory.isLoaded(), "Login failed after clearing and re-typing username");
    }
}
