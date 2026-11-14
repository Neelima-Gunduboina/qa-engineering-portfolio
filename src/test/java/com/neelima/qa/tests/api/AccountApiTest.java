package com.neelima.qa.tests.api;

import com.neelima.qa.api.ParaBankApiClient;
import com.neelima.qa.listeners.TestListener;
import com.neelima.qa.utils.ConfigLoader;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * API tests against the ParaBank REST service: status codes, response fields,
 * error handling with invalid input, and balance reconciliation after a transfer.
 */
@Listeners(TestListener.class)
public class AccountApiTest {

    private final ParaBankApiClient api = new ParaBankApiClient();
    private String username;
    private String password;
    private long customerId;
    private long primaryAccountId;

    /**
     * Uses -Dapi.username / -Dapi.password when given; otherwise registers a fresh customer
     * so the suite never depends on shared demo data that the site may reset.
     */
    @BeforeClass(alwaysRun = true)
    public void setUpCustomer() {
        username = ConfigLoader.get("api.username", "");
        password = ConfigLoader.get("api.password", "");
        if (username.isEmpty()) {
            username = "api" + System.currentTimeMillis();
            password = "Test@1234";
            api.registerCustomer(username, password);
        }
        Response login = api.login(username, password);
        Assert.assertEquals(login.statusCode(), 200, "Customer login failed: " + login.asString());
        customerId = login.jsonPath().getLong("id");
        primaryAccountId = api.getCustomerAccounts(customerId).jsonPath().getLong("[0].id");
    }

    @Test(groups = {"smoke", "regression", "api"}, description = "Valid login returns the customer profile")
    public void validLoginReturnsCustomer() {
        Response r = api.login(username, password);

        Assert.assertEquals(r.statusCode(), 200);
        Assert.assertEquals(r.contentType(), "application/json");
        Assert.assertEquals(r.jsonPath().getLong("id"), customerId);
        Assert.assertNotNull(r.jsonPath().getString("firstName"));
        Assert.assertNotNull(r.jsonPath().getString("address.street"));
    }

    @Test(groups = {"regression", "api", "negative"}, description = "Invalid login returns 400 with an error message")
    public void invalidLoginIsRejected() {
        Response r = api.login("no_such_user", "wrong");

        Assert.assertEquals(r.statusCode(), 400);
        Assert.assertEquals(r.asString(), "Invalid username and/or password");
    }

    @Test(groups = {"smoke", "regression", "api"}, description = "Customer accounts list has valid fields")
    public void customerAccountsHaveValidFields() {
        Response r = api.getCustomerAccounts(customerId);

        Assert.assertEquals(r.statusCode(), 200);
        List<Long> owners = r.jsonPath().getList("customerId", Long.class);
        List<String> types = r.jsonPath().getList("type", String.class);
        Assert.assertFalse(owners.isEmpty(), "Customer should have at least one account");
        owners.forEach(owner -> Assert.assertEquals(owner.longValue(), customerId, "Account owned by another customer"));
        types.forEach(type -> Assert.assertTrue(List.of("CHECKING", "SAVINGS", "LOAN").contains(type),
                "Unexpected account type: " + type));
    }

    @Test(groups = {"regression", "api", "negative"}, description = "Unknown account id returns 400")
    public void unknownAccountReturns400() {
        Response r = api.getAccount(0);

        Assert.assertEquals(r.statusCode(), 400);
        Assert.assertEquals(r.asString(), "Could not find account #0");
    }

    @Test(groups = {"regression", "api", "negative"}, description = "Non-numeric amount is rejected")
    public void nonNumericAmountIsRejected() {
        Response r = api.transfer(primaryAccountId, primaryAccountId, "abc");

        Assert.assertTrue(r.statusCode() >= 400, "Expected a 4xx for amount=abc but got " + r.statusCode());
    }

    @Test(groups = {"regression", "api", "e2e"},
            description = "Transfer between two new accounts debits one, credits the other, and is logged")
    public void transferReconcilesBalancesAndHistory() {
        long from = openAccount(0);
        long to = openAccount(1);
        BigDecimal fromBefore = balanceOf(from);
        BigDecimal toBefore = balanceOf(to);
        BigDecimal amount = new BigDecimal("10.00");

        Response r = api.transfer(from, to, amount.toPlainString());

        Assert.assertEquals(r.statusCode(), 200);
        Assert.assertEquals(r.asString(),
                "Successfully transferred $" + amount.toPlainString() + " from account #" + from + " to account #" + to);
        Assert.assertEquals(balanceOf(from), fromBefore.subtract(amount), "Source account not debited");
        Assert.assertEquals(balanceOf(to), toBefore.add(amount), "Target account not credited");

        List<String> descriptions = api.getTransactions(to).jsonPath().getList("description", String.class);
        Assert.assertTrue(descriptions.contains("Funds Transfer Received"),
                "Transfer missing from target account history: " + descriptions);
    }

    @Test(groups = {"regression", "api", "e2e"},
            description = "Bill payment via the API debits the account and is logged")
    public void billPayDebitsAccount() {
        long account = openAccount(0);
        BigDecimal before = balanceOf(account);
        BigDecimal amount = new BigDecimal("25.50");

        Response r = api.payBill(account, amount.toPlainString(), "British Gas");

        Assert.assertEquals(r.statusCode(), 200);
        Assert.assertEquals(r.jsonPath().getString("payeeName"), "British Gas");
        Assert.assertEquals(balanceOf(account), before.subtract(amount), "Account not debited");
        List<String> descriptions = api.getTransactions(account).jsonPath().getList("description", String.class);
        Assert.assertTrue(descriptions.contains("Bill Payment to British Gas"),
                "Bill payment missing from history: " + descriptions);
    }

    /**
     * Known defect DEF-001 (docs/defects/DEF-001-negative-transfer-amount.md):
     * the API accepts a negative amount and moves money in reverse.
     * Kept in the "known-defect" group, which the suites exclude, so CI stays green
     * while the defect stays documented and reproducible.
     */
    @Test(groups = {"known-defect"}, description = "DEF-001: negative transfer amount must be rejected")
    public void negativeAmountShouldBeRejected() {
        long from = openAccount(0);
        long to = openAccount(1);

        Response r = api.transfer(from, to, "-5");

        Assert.assertTrue(r.statusCode() >= 400,
                "DEF-001 reproduced: negative transfer accepted with HTTP " + r.statusCode() + " — " + r.asString());
    }

    private long openAccount(int type) {
        Response r = api.createAccount(customerId, type, primaryAccountId);
        Assert.assertEquals(r.statusCode(), 200, "Could not open account: " + r.asString());
        return r.jsonPath().getLong("id");
    }

    private BigDecimal balanceOf(long accountId) {
        // Normalise scale so 80.0 and 80.00 compare equal (BigDecimal.equals is scale-sensitive)
        return new BigDecimal(api.getAccount(accountId).jsonPath().getString("balance"))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
