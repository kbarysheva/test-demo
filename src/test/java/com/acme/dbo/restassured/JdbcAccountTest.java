package com.acme.dbo.restassured;

import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.acme.dbo.restassured.EndPoint.BASE_URL;
import static com.acme.dbo.restassured.EndPoint.PORT;
import static com.acme.dbo.restassured.EndPoint.DBO_API;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.sql.*;
import java.time.Instant;

public class JdbcAccountTest {
    private RequestSpecification givenRequest;
    private Connection connection;

    @BeforeEach
    public void setUpDbConnection() throws SQLException {
        connection = DriverManager.getConnection("jdbc:derby://localhost/dbo-db");
    }

    @AfterEach
    public void closeDbConnection() throws SQLException {
        connection.close();
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

    // JDBC
    @Test
    public void shouldGetSomeClientsWhenPreparedDb() throws SQLException {

        int newClientId;
        try(final PreparedStatement newClient = connection.prepareStatement(
                "INSERT INTO CLIENT(LOGIN, SECRET, SALT, CREATED, ENABLED) VALUES(?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            newClient.setString(1, "test1234@mail.ru");
            newClient.setString(2, "kjd93mf9s29mf91mf9f");
            newClient.setString(3, "some-salt");
            newClient.setTimestamp(4, Timestamp.from(Instant.now()));
            newClient.setBoolean(5, true);

            assumeTrue(newClient.executeUpdate() == 1);

            try(final ResultSet generatedKeys = newClient.getGeneratedKeys()) {
                assumeTrue(generatedKeys.next());
                newClientId = generatedKeys.getInt(1);
            }
        }

        int clientsCount;
        try(final PreparedStatement countClients = connection.prepareStatement("SELECT COUNT(*) FROM CLIENT");
            final ResultSet resultSet = countClients.executeQuery()) {
            assumeTrue(resultSet.next());
            clientsCount = resultSet.getInt(1);
        }
        try {
            givenRequest.when().get("client").
                    then().statusCode(200).body(
                            "size()", is(clientsCount),
                            "id", hasItem(newClientId)
                    );
        } finally {
            try(final PreparedStatement deleteClient = connection.prepareStatement("DELETE FROM CLIENT WHERE ID=?")) {
                deleteClient.setInt(1, newClientId);
                assumeTrue(deleteClient.executeUpdate() == 1);
            }
        }
    }

    @Test
    public void shouldSelect() throws SQLException {
        try( final PreparedStatement selectClients = connection.prepareStatement("SELECT * FROM CLIENT");
             final ResultSet selectedClients = selectClients.executeQuery()) {

            while (selectedClients.next()) {
                System.out.println(selectedClients.getInt(1));
                System.out.println(selectedClients.getString(2));
                System.out.println(selectedClients.getString(3));
                System.out.println(selectedClients.getString(4));
                System.out.println(selectedClients.getString(5));
            }
        }
    }
}

