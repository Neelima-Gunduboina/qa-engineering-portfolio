package com.neelima.qa.tests.ui;

import com.neelima.qa.base.BaseTest;
import com.neelima.qa.dataproviders.JsonDataProvider;
import com.neelima.qa.listeners.TestListener;
import com.neelima.qa.pages.AccountsOverviewPage;
import com.neelima.qa.pages.BillPayPage;
import com.neelima.qa.pages.RegisterPage;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Map;

@Listeners(TestListener.class)
public class BillPayTest extends BaseTest {

    @Test(groups = {"regression", "e2e"}, dataProvider = "registerData", dataProviderClass = JsonDataProvider.class,
            description = "Customer pays a bill and the account is debited")
    public void payBillDebitsAccount(Map<String, Object> template) {
        Map<String, String> customer = uniqueCustomer(template);
        open("/register.htm");
        new RegisterPage(getDriver()).register(customer);

        open("/overview.htm");
        AccountsOverviewPage overview = new AccountsOverviewPage(getDriver());
        String account = overview.getAccountIds().get(0);
        BigDecimal before = overview.getBalance(account);

        open("/billpay.htm");
        BillPayPage billPay = new BillPayPage(getDriver()).payBill("British Gas", "12345678", "25.50", account);

        Assert.assertEquals(billPay.getResultHeading(), "Bill Payment Complete");
        Assert.assertEquals(billPay.getResultPayee(), "British Gas");
        Assert.assertEquals(billPay.getResultAmount(), "$25.50");

        open("/overview.htm");
        Assert.assertEquals(new AccountsOverviewPage(getDriver()).getBalance(account),
                before.subtract(new BigDecimal("25.50")));
    }
}
