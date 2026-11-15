package com.neelima.qa.tests.ui;

import com.neelima.qa.base.BaseTest;
import com.neelima.qa.dataproviders.JsonDataProvider;
import com.neelima.qa.listeners.TestListener;
import com.neelima.qa.pages.AccountsOverviewPage;
import com.neelima.qa.pages.BillPayPage;
import com.neelima.qa.pages.RegisterPage;
import com.neelima.qa.retry.RetryOnce;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Map;

@Listeners(TestListener.class)
public class BillPayTest extends BaseTest {

    private static final String PAYEE = "British Gas";
    private static final String AMOUNT = "25.50";

    @Test(groups = {"regression", "e2e"}, retryAnalyzer = RetryOnce.class,
            dataProvider = "registerData", dataProviderClass = JsonDataProvider.class,
            description = "Customer pays a bill and sees the payment confirmation")
    public void payBillShowsConfirmation(Map<String, Object> template) {
        String account = registerAndGetFirstAccount(template);

        open("/billpay.htm");
        BillPayPage billPay = new BillPayPage(getDriver()).payBill(PAYEE, "12345678", AMOUNT, account);

        Assert.assertEquals(billPay.getResultHeading(), "Bill Payment Complete");
        Assert.assertEquals(billPay.getResultPayee(), PAYEE);
        Assert.assertEquals(billPay.getResultAmount(), "$" + AMOUNT);
    }

    /**
     * Regression guard for DEF-003 (docs/defects/DEF-003-ui-bill-pay-does-not-debit.md):
     * on 14 Nov 2026 the confirmation was shown but the account was never debited. Not
     * reproducible since the demo reset on 15 Nov; this check fails if the defect returns.
     */
    @Test(groups = {"regression", "e2e"}, retryAnalyzer = RetryOnce.class,
            dataProvider = "registerData", dataProviderClass = JsonDataProvider.class,
            description = "DEF-003 guard: a UI bill payment debits the account")
    public void payBillDebitsAccount(Map<String, Object> template) {
        String account = registerAndGetFirstAccount(template);
        BigDecimal before = new AccountsOverviewPage(getDriver()).getBalance(account);

        open("/billpay.htm");
        new BillPayPage(getDriver()).payBill(PAYEE, "12345678", AMOUNT, account);

        open("/overview.htm");
        Assert.assertEquals(new AccountsOverviewPage(getDriver()).getBalance(account),
                before.subtract(new BigDecimal(AMOUNT)), "DEF-003 reproduced: account not debited");
    }

    private String registerAndGetFirstAccount(Map<String, Object> template) {
        open("/register.htm");
        new RegisterPage(getDriver()).register(uniqueCustomer(template));
        open("/overview.htm");
        return new AccountsOverviewPage(getDriver()).getAccountIds().get(0);
    }
}
