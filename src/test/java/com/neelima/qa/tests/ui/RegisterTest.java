package com.neelima.qa.tests.ui;

import com.neelima.qa.base.BaseTest;
import com.neelima.qa.dataproviders.JsonDataProvider;
import com.neelima.qa.listeners.TestListener;
import com.neelima.qa.pages.RegisterPage;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.Map;

@Listeners(TestListener.class)
public class RegisterTest extends BaseTest {

    @Test(groups = {"smoke", "regression"}, dataProvider = "registerData", dataProviderClass = JsonDataProvider.class,
            description = "New customer can register and is logged in automatically")
    public void registerNewCustomer(Map<String, Object> template) {
        Map<String, String> customer = uniqueCustomer(template);

        open("/register.htm");
        RegisterPage page = new RegisterPage(getDriver()).register(customer);

        Assert.assertEquals(page.getWelcomeHeading(), "Welcome " + customer.get("username"));
        Assert.assertEquals(page.getWelcomeText(),
                "Your account was created successfully. You are now logged in.");
    }

    @Test(groups = {"regression", "negative"}, dataProvider = "registerData", dataProviderClass = JsonDataProvider.class,
            description = "Registering an existing username is rejected")
    public void duplicateUsernameIsRejected(Map<String, Object> template) {
        Map<String, String> customer = uniqueCustomer(template);

        open("/register.htm");
        new RegisterPage(getDriver()).register(customer);
        getDriver().manage().deleteAllCookies();

        open("/register.htm");
        RegisterPage second = new RegisterPage(getDriver()).register(customer);

        Assert.assertEquals(second.getUsernameError(), "This username already exists.");
    }
}
