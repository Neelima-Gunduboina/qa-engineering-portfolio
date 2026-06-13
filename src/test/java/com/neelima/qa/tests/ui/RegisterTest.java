package com.neelima.qa.tests.ui;

import com.neelima.qa.base.BaseTest;
import com.neelima.qa.dataproviders.JsonDataProvider;
import com.neelima.qa.pages.RegisterPage;
import com.neelima.qa.utils.ConfigLoader;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.Map;

@Listeners({com.neelima.qa.listeners.TestListener.class})
public class RegisterTest extends BaseTest {

    @Test(groups = {"regression", "smoke"}, dataProvider = "registerData", dataProviderClass = JsonDataProvider.class)
    public void registerNewUser(Map<String, Object> data) {
        String base = ConfigLoader.get("base.url");
        getDriver().get(base + "/parabank/register.htm");
        RegisterPage rp = new RegisterPage(getDriver());

        long ts = System.currentTimeMillis();
        String usernameTemplate = (String) data.get("username");
        String username = usernameTemplate.replace("${timestamp}", String.valueOf(ts));
        rp.register((String) data.get("firstName"), (String) data.get("lastName"), (String) data.get("address"), (String) data.get("city"), (String) data.get("state"), (String) data.get("zipCode"), (String) data.get("phone"), (String) data.get("ssn"), username, (String) data.get("password"));

        // Basic assertion: registration success message present on page
        String page = getDriver().getPageSource();
        Assert.assertTrue(page.contains("Welcome") || page.contains("Your account was created"), "Registration confirmation not found in page source");
    }
}
