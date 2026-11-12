package com.neelima.practice.tests;

import com.neelima.practice.base.BaseTest;
import com.neelima.practice.pages.SauceLoginPage;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Data-driven negative tests: each row is an invalid input and the exact error the user must see.
 * Replaces negativetesterrormessage and formvalidation.
 */
public class NegativeLoginTest extends BaseTest {

    @DataProvider(name = "invalidLogins")
    public Object[][] invalidLogins() {
        return new Object[][]{
                {"wrong_user", "wrong_password",
                        "Epic sadface: Username and password do not match any user in this service"},
                {"standard_user", "wrong_password",
                        "Epic sadface: Username and password do not match any user in this service"},
                {"", "secret_sauce", "Epic sadface: Username is required"},
                {"standard_user", "", "Epic sadface: Password is required"},
                {"", "", "Epic sadface: Username is required"},
                {"locked_out_user", "secret_sauce", "Epic sadface: Sorry, this user has been locked out."},
                {"' OR '1'='1", "' OR '1'='1",
                        "Epic sadface: Username and password do not match any user in this service"},
        };
    }

    @Test(dataProvider = "invalidLogins", groups = {"regression", "negative"},
            description = "Invalid credentials are rejected with the correct error message")
    public void invalidLoginShowsCorrectError(String username, String password, String expectedError) {
        SauceLoginPage login = new SauceLoginPage(driver()).open().attemptLogin(username, password);

        Assert.assertEquals(login.getErrorMessage(), expectedError);
        Assert.assertFalse(login.getCurrentUrl().contains("inventory"), "User should not reach inventory page");
    }
}
