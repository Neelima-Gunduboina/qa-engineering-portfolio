package com.neelima.qa.tests.ui;

import com.neelima.qa.base.BaseTest;
import com.neelima.qa.pages.AccountsOverviewPage;
import com.neelima.qa.pages.LoginPage;
import com.neelima.qa.pages.RegisterPage;
import com.neelima.qa.utils.ConfigLoader;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({com.neelima.qa.listeners.TestListener.class})
public class LoginTest extends BaseTest {

    @Test(groups = {"smoke", "regression"})
    public void registerAndLogin() {
        String base = ConfigLoader.get("base.url");
        getDriver().get(base + "/parabank/register.htm");
        RegisterPage rp = new RegisterPage(getDriver());
        String username = "autouser_" + System.currentTimeMillis();
        String password = "Pwd@12345";
        rp.register("Auto", "User", "1 Test St", "City", "ST", "00000", "555-0000", "111-22-3333", username, password);

        // After registration, attempt login
        getDriver().get(base + "/parabank/login.htm");
        LoginPage lp = new LoginPage(getDriver());
        AccountsOverviewPage ap = lp.login(username, password);

        String page = getDriver().getPageSource();
        Assert.assertTrue(page.contains("Accounts Overview") || page.contains("Log Out"), "Login appears to have failed or accounts overview not found");
    }
}
