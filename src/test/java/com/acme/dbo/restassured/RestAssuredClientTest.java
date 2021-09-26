package com.acme.dbo.restassured;

import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.acme.dbo.restassured.EndPoint.*;
import static com.acme.dbo.restassured.EndPoint.BASE_URL;
import static com.acme.dbo.restassured.EndPoint.PORT;
import static com.acme.dbo.restassured.EndPoint.DBO_API;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class RestAssuredClientTest {
    private RequestSpecification givenRequest;

    @BeforeEach
    public void setUpRestAssured() {
        givenRequest = given().
                baseUri(BASE_URL).
                basePath(DBO_API).
                port(PORT).
                header("X-API-VERSION", "1").
                contentType(ContentType.JSON).
                accept(ContentType.JSON).
                filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    public void shouldGetClientByIdWhenExists() {
        givenRequest
            .when()
            .get(CLIENT_ID, 1)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .body("size()", is(6),
                "id", is(1),
                            "login", is("admin@acme.com"));
    }

    @Test
    public void shouldErrorWhenClientNotExists() {
        givenRequest
                .when()
                .get(CLIENT_ID, -1)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void shouldDeleteById() {
        int clientId = givenRequest
                .when()
                    .body("{\n" +
                        "   \"login\":\"" +
                        Math.random() + "@gmail.com\", \n" +
                        "   \"salt\": \"test-salt\", \n" +
                        "   \"secret\": \"24lk1s3x5j2\" \n" +
                        "}")
                .post("client")
                .then().statusCode(HttpStatus.SC_CREATED)
                .extract()
                .path("id");

        givenRequest
                .when()
                .delete(CLIENT_ID, clientId)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }
}
