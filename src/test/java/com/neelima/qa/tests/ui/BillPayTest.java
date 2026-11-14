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
     * Known defect DEF-003 (docs/defects/DEF-003-ui-bill-pay-does-not-debit.md):
     * the confirmation is shown but the account is never debited and no transaction is recorded.
     * The same payment through the REST API works (AccountApiTest.billPayDebitsAccount).
     */
    @Test(groups = {"known-defect", "e2e"}, dataProvider = "registerData", dataProviderClass = JsonDataProvider.class,
            description = "DEF-003: a UI bill payment must debit the account")
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
