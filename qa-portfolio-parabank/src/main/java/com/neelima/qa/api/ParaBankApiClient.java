package com.neelima.qa.api;

import com.neelima.qa.utils.ConfigLoader;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.session.SessionFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/**
 * Thin client for the ParaBank REST API (/parabank/services/bank).
 * Each method returns the raw Response so tests decide what to assert.
 */
public class ParaBankApiClient {

    private final RequestSpecification spec;

    public ParaBankApiClient() {
        this.spec = new RequestSpecBuilder()
                .setBaseUri(ConfigLoader.get("api.base.url"))
                .setAccept(ContentType.JSON)
                .log(LogDetail.URI)
                .build();
    }

    /**
     * Registers a brand-new customer through the public sign-up form.
     * ParaBank has no REST endpoint for registration, so this posts the same form a user
     * submits in the browser (a session cookie from the GET is required first).
     * The demo site is reset regularly, so tests create their own customer instead of
     * relying on a shared account such as john/demo.
     */
    public void registerCustomer(String username, String password) {
        Response r = submitRegistration(username, password);
        if (r.statusCode() != 200 || !r.asString().contains("Your account was created successfully")) {
            throw new IllegalStateException("Customer registration failed (HTTP " + r.statusCode() + ")");
        }
    }

    /** Posts the sign-up form and returns the resulting page, successful or not. */
    public Response submitRegistration(String username, String password) {
        SessionFilter session = new SessionFilter();
        String webUrl = ConfigLoader.get("base.url");
        given().filter(session).get(webUrl + "/register.htm").then().statusCode(200);
        return given().filter(session)
                .contentType(ContentType.URLENC)
                .formParam("customer.firstName", "Api")
                .formParam("customer.lastName", "Tester")
                .formParam("customer.address.street", "1 Test Street")
                .formParam("customer.address.city", "London")
                .formParam("customer.address.state", "LDN")
                .formParam("customer.address.zipCode", "E1 6AN")
                .formParam("customer.phoneNumber", "07000000000")
                .formParam("customer.ssn", "123-45-6789")
                .formParam("customer.username", username)
                .formParam("customer.password", password)
                .formParam("repeatedPassword", password)
                .post(webUrl + "/register.htm");
    }

    public Response login(String username, String password) {
        return given(spec).pathParam("u", username).pathParam("p", password)
                .get("/login/{u}/{p}");
    }

    public Response getCustomerAccounts(long customerId) {
        return given(spec).pathParam("id", customerId).get("/customers/{id}/accounts");
    }

    public Response getAccount(long accountId) {
        return given(spec).pathParam("id", accountId).get("/accounts/{id}");
    }

    public Response getTransactions(long accountId) {
        return given(spec).pathParam("id", accountId).get("/accounts/{id}/transactions");
    }

    /**
     * @param accountType 0 = CHECKING, 1 = SAVINGS
     */
    public Response createAccount(long customerId, int accountType, long fundingAccountId) {
        return given(spec)
                .queryParam("customerId", customerId)
                .queryParam("newAccountType", accountType)
                .queryParam("fromAccountId", fundingAccountId)
                .post("/createAccount");
    }

    public Response transfer(long fromAccountId, long toAccountId, String amount) {
        return given(spec)
                .queryParam("fromAccountId", fromAccountId)
                .queryParam("toAccountId", toAccountId)
                .queryParam("amount", amount)
                .post("/transfer");
    }

    public Response payBill(long accountId, String amount, String payeeName) {
        String payee = """
                {"name":"%s","address":{"street":"1 Payee Road","city":"London","state":"LDN","zipCode":"E1 6AN"},
                 "phoneNumber":"02000000000","accountNumber":12345678}""".formatted(payeeName);
        return given(spec)
                .contentType(ContentType.JSON)
                .queryParam("accountId", accountId)
                .queryParam("amount", amount)
                .body(payee)
                .post("/billpay");
    }
}
