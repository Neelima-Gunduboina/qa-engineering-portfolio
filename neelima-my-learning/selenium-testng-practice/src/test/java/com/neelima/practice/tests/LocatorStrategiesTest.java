package com.neelima.practice.tests;

import com.neelima.practice.base.BaseTest;
import com.neelima.practice.pages.PracticeLoginPage;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * The same login performed with four locator strategies: ID, Name/ID mix, XPath and CSS.
 * Replaces the old Locators / LoginUsingXPath / LoginUsingCSSSelector scripts.
 */
public class LocatorStrategiesTest extends BaseTest {

    private static final String USER = "student";
    private static final String PASS = "Password123";

    @DataProvider(name = "locatorStrategies")
    public Object[][] locatorStrategies() {
        return new Object[][]{
                {"ID", By.id("username"), By.id("password"), By.id("submit")},
                {"Name", By.name("username"), By.name("password"), By.id("submit")},
                {"XPath", By.xpath("//input[@id='username']"), By.xpath("//input[@id='password']"),
                        By.xpath("//button[@id='submit']")},
                {"CSS", By.cssSelector("input#username"), By.cssSelector("input#password"),
                        By.cssSelector("button#submit")},
        };
    }

    @Test(dataProvider = "locatorStrategies", groups = {"smoke", "locators"},
            description = "Valid login succeeds using each locator strategy")
    public void validLoginWithLocatorStrategy(String strategy, By username, By password, By submit) {
        PracticeLoginPage page = new PracticeLoginPage(driver()).open()
                .login(username, password, submit, USER, PASS);

        Assert.assertEquals(page.getSuccessHeading(), "Logged In Successfully",
                "Success heading mismatch using " + strategy + " locators");
        Assert.assertTrue(page.isLogoutVisible(), "Log out button not shown using " + strategy + " locators");
    }

    @Test(groups = {"regression", "negative"}, description = "Invalid username shows the username error")
    public void invalidUsernameShowsError() {
        PracticeLoginPage page = new PracticeLoginPage(driver()).open()
                .login(By.id("username"), By.id("password"), By.id("submit"), "incorrectUser", PASS);

        Assert.assertEquals(page.getErrorMessage(), "Your username is invalid!");
    }

    @Test(groups = {"regression", "negative"}, description = "Invalid password shows the password error")
    public void invalidPasswordShowsError() {
        PracticeLoginPage page = new PracticeLoginPage(driver()).open()
                .login(By.id("username"), By.id("password"), By.id("submit"), USER, "incorrectPassword");

        Assert.assertEquals(page.getErrorMessage(), "Your password is invalid!");
    }
}
