package com.acme.dbo.restassured;

import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Random;

import static com.acme.dbo.restassured.EndPoint.*;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class JpaAccountTest {
    private RequestSpecification givenRequest;
    private static EntityManagerFactory entityManagerFactory;

    @BeforeAll
    public static void setUpJpa() {
        entityManagerFactory = Persistence.createEntityManagerFactory("dbo");
    }

    @AfterAll
    public static void tearDownJpa() {
        entityManagerFactory.close();
    }

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
    public void shouldGetAccountByIdWhenExists() throws SQLException {
        final String newLogin = "login" + new Random().nextInt();
        final Client client = new Client(newLogin, "secret", "salt", LocalDateTime.now(), true);

        final EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        em.persist(client);
        em.getTransaction().commit();

        givenRequest.
                when().
                get(CLIENT_ID, client.getId()).
                then().
                statusCode(is(SC_OK)).
                body("id", equalTo(client.getId()),
                        "login", equalTo(client.getLogin()));

        em.getTransaction().begin();
        final Client clientSaved = em.find(Client.class, client.getId());
        em.remove(clientSaved);
        em.getTransaction().commit();

        em.close();
    }
}
