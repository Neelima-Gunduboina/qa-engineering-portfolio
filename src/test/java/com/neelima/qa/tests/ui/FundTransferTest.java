package com.neelima.qa.tests.ui;

import com.neelima.qa.base.BaseTest;
import com.neelima.qa.dataproviders.JsonDataProvider;
import com.neelima.qa.listeners.TestListener;
import com.neelima.qa.pages.AccountsOverviewPage;
import com.neelima.qa.pages.FundTransferPage;
import com.neelima.qa.pages.OpenAccountPage;
import com.neelima.qa.pages.RegisterPage;
import com.neelima.qa.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Map;

/**
 * End-to-end: register -> open a second account -> transfer -> verify confirmation and both balances.
 */
@Listeners(TestListener.class)
public class FundTransferTest extends BaseTest {

    @Test(groups = {"regression", "e2e"}, dataProvider = "transferData", dataProviderClass = JsonDataProvider.class,
            description = "Funds move between a customer's own accounts and balances reconcile")
    public void transferBetweenOwnAccounts(Map<String, Object> data) {
        String amount = String.valueOf(data.get("amount"));
        Map<String, String> customer =
                uniqueCustomer(JsonUtils.readListFromResource("testdata/registerData.json").get(0));

        open("/register.htm");
        new RegisterPage(getDriver()).register(customer);

        open("/overview.htm");
        String fromAccount = new AccountsOverviewPage(getDriver()).getAccountIds().get(0);

        open("/openaccount.htm");
        String toAccount = new OpenAccountPage(getDriver()).openAccount("SAVINGS", fromAccount);

        open("/overview.htm");
        AccountsOverviewPage before = new AccountsOverviewPage(getDriver());
        BigDecimal fromBefore = before.getBalance(fromAccount);
        BigDecimal toBefore = before.getBalance(toAccount);

        open("/transfer.htm");
        FundTransferPage transfer = new FundTransferPage(getDriver()).transfer(fromAccount, toAccount, amount);

        Assert.assertEquals(transfer.getResultHeading(), "Transfer Complete!");
        Assert.assertEquals(transfer.getAmountResult(), "$" + new BigDecimal(amount).setScale(2));
        Assert.assertEquals(transfer.getFromAccountResult(), fromAccount);
        Assert.assertEquals(transfer.getToAccountResult(), toAccount);

        open("/overview.htm");
        AccountsOverviewPage after = new AccountsOverviewPage(getDriver());
        BigDecimal value = new BigDecimal(amount);
        Assert.assertEquals(after.getBalance(fromAccount), fromBefore.subtract(value), "Source not debited correctly");
        Assert.assertEquals(after.getBalance(toAccount), toBefore.add(value), "Target not credited correctly");
    }
}
