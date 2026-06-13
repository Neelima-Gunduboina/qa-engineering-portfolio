package com.neelima.qa.tests.ui;

import com.neelima.qa.base.BaseTest;
import com.neelima.qa.pages.AccountsOverviewPage;
import com.neelima.qa.pages.FundTransferPage;
import com.neelima.qa.pages.LoginPage;
import com.neelima.qa.pages.RegisterPage;
import com.neelima.qa.utils.ConfigLoader;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({com.neelima.qa.listeners.TestListener.class})
public class FundTransferTest extends BaseTest {

    @Test(groups = {"regression"})
    public void endToEndTransfer() throws Exception {
        String base = ConfigLoader.get("base.url");
        getDriver().get(base + "/parabank/register.htm");
        RegisterPage rp = new RegisterPage(getDriver());
        String username = "autofund_" + System.currentTimeMillis();
        String password = "Pwd@12345";
        rp.register("Auto", "Fund", "1 Test St", "City", "ST", "00000", "555-0000", "111-22-3333", username, password);

        // Login
        getDriver().get(base + "/parabank/login.htm");
        LoginPage lp = new LoginPage(getDriver());
        lp.login(username, password);

        AccountsOverviewPage accounts = new AccountsOverviewPage(getDriver());
        String acct1 = accounts.getFirstAccountId();
        // For simplicity try transfer to same account if only one exists (UI will show error); otherwise use acct1 twice
        getDriver().get(base + "/parabank/transfer.htm");
        FundTransferPage fp = new FundTransferPage(getDriver());
        String page = fp.transfer(acct1 == null ? "1001" : acct1, acct1 == null ? "1002" : acct1, "10.00");

        Assert.assertTrue(page.contains("completed") || page.contains("Transfer Complete") || page.contains("was submitted"), "Transfer confirmation not detected in page source");
    }
}
