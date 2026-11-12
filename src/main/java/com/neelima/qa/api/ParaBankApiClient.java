package com.neelima.qa.api;

import com.neelima.qa.utils.ConfigLoader;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
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
}
