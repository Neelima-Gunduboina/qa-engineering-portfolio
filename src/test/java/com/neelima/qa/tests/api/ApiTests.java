package com.neelima.qa.tests.api;

import com.neelima.qa.api.RestClient;
import com.neelima.qa.listeners.TestListener;
import com.neelima.qa.utils.ConfigLoader;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

@Listeners({TestListener.class})
public class ApiTests {

    @Test(groups = {"smoke", "regression"})
    public void healthCheck() {
        String base = ConfigLoader.get("api.base.url");
        Response r = RestClient.get("/ping");
        // Some demo APIs may not have /ping; fall back to /booking
        if (r.statusCode() >= 400) r = RestClient.get("/booking");
        Assert.assertTrue(r.statusCode() == 200 || r.statusCode() == 201, "API health check failed: " + r.statusCode());
    }

    @Test(groups = {"regression"})
    public void createBookingFlow() {
        // restful-booker sample create booking
        Map<String, Object> body = new HashMap<>();
        body.put("firstname", "Auto");
        body.put("lastname", "Tester");
        Map<String, Object> dates = new HashMap<>();
        dates.put("checkin", "2023-01-01");
        dates.put("checkout", "2023-01-02");
        body.put("totalprice", 123);
        body.put("depositpaid", false);
        body.put("bookingdates", dates);

        Response r = RestClient.post("/booking", body);
        Assert.assertTrue(r.statusCode() == 200 || r.statusCode() == 201, "Booking creation failed: " + r.statusCode());
        Assert.assertTrue(r.getBody().asString().contains("bookingid") || r.getBody().asString().contains("booking"));
    }
}
