package com.acme.dbo.retrofit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Client {
    private int id;
    private String login;
    private String salt;
    private String secret;

    public Client() {
    }

    public Client(String login, String salt, String secret) {
        this.login = login;
        this.salt = salt;
        this.secret = secret;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return id == client.id &&
                Objects.equals(login, client.login) &&
                Objects.equals(salt, client.salt) &&
                Objects.equals(secret, client.secret);
    }
}
