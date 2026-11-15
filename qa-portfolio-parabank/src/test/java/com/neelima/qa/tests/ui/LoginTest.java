package com.neelima.qa.tests.ui;

import com.neelima.qa.base.BaseTest;
import com.neelima.qa.dataproviders.JsonDataProvider;
import com.neelima.qa.listeners.TestListener;
import com.neelima.qa.pages.AccountsOverviewPage;
import com.neelima.qa.pages.LoginPage;
import com.neelima.qa.pages.RegisterPage;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.Map;

@Listeners(TestListener.class)
public class LoginTest extends BaseTest {

    @Test(groups = {"smoke", "regression"}, dataProvider = "registerData", dataProviderClass = JsonDataProvider.class,
            description = "Registered customer can log out and log back in")
    public void registeredCustomerCanLogin(Map<String, Object> template) {
        Map<String, String> customer = uniqueCustomer(template);
        open("/register.htm");
        new RegisterPage(getDriver()).register(customer);
        new AccountsOverviewPage(getDriver()).logOut();

        AccountsOverviewPage overview = new LoginPage(getDriver())
                .login(customer.get("username"), customer.get("password"));

        Assert.assertEquals(overview.getHeading(), "Accounts Overview");
        Assert.assertTrue(overview.isLoggedIn(), "Log Out link should be visible after login");
        Assert.assertEquals(overview.getAccountIds().size(), 1, "A new customer should have exactly one account");
    }

    /**
     * Regression guard for DEF-002 (docs/defects/DEF-002-login-accepts-unknown-credentials.md):
     * the web login accepted an unknown username/password and signed the visitor in as
     * another customer. Observed on 14 Nov 2026; not reproducible since the demo reset on
     * 15 Nov, so this runs in regression and will fail if the defect returns.
     */
    @Test(groups = {"regression", "negative"}, description = "DEF-002 guard: unknown credentials must be rejected")
    public void invalidCredentialsAreRejected() {
        open("/index.htm");
        LoginPage login = new LoginPage(getDriver())
                .attemptInvalidLogin("no_such_user_" + System.nanoTime(), "wrongPassword");

        Assert.assertEquals(login.getErrorMessage(), "The username and password could not be verified.");
    }

    @Test(groups = {"regression", "negative"}, description = "Empty credentials are rejected")
    public void emptyCredentialsAreRejected() {
        open("/index.htm");
        LoginPage login = new LoginPage(getDriver()).attemptInvalidLogin("", "");

        Assert.assertEquals(login.getErrorMessage(), "Please enter a username and password.");
    }
}
