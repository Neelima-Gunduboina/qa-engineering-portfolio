package com.neelima.qa.api;

import com.neelima.qa.utils.ConfigLoader;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.Map;

/**
 * Simple REST client wrapper around REST Assured.
 */
public class RestClient {
    private static final String BASE = ConfigLoader.get("api.base.url", "https://restful-booker.herokuapp.com");

    static {
        RestAssured.baseURI = BASE;
    }

    public static Response get(String path) {
        return RestAssured.given().when().get(path).then().extract().response();
    }

    public static Response post(String path, Object body) {
        return RestAssured.given().contentType("application/json").body(body).when().post(path).then().extract().response();
    }

    public static Response post(String path, Map<String, Object> body) {
        return RestAssured.given().contentType("application/json").body(body).when().post(path).then().extract().response();
    }
}
