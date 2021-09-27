package com.acme.dbo.retrofit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;

import static com.acme.dbo.retrofit.EndPoint.BASE_URL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RetrofitClientTest {

    @Test
    public void shouldGetClient() throws IOException {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .build();

        ClientService service = retrofit.create(ClientService.class);
        Response<List<Client>> response = service.getClients().execute();
        assertThat(response.isSuccessful(), equalTo(true));
    }

    @Test
    public void shouldPost() throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        ClientService service = retrofit.create(ClientService.class);
        Response<Client> response = service.createClient(new Client(Math.random() + "@gmail.com", "somesalt", "skj35k9kslfk25k2l3")).execute();
        assertThat(response.isSuccessful(), equalTo(true));
    }

    @Test
    public void shouldGetById() throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        ClientService service = retrofit.create(ClientService.class);
        Response<Client> response = service.getClient(1).execute();
        assertThat(response.isSuccessful(), equalTo(true));
        assertThat(response.body().getLogin(), equalTo("admin@acme.com"));
    }

    @Test
    public void shouldDeleteById() throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        ClientService service = retrofit.create(ClientService.class);
        int id = service.createClient(new Client(Math.random() + "@gmail.com", "testsalt", "235jw23jehr529ef")).execute().body().getId();
        service.deleteClientById(id).execute();
    }

    @Test
    public void shouldDeleteByLogin() throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        ClientService service = retrofit.create(ClientService.class);
        String clientLogin = service.createClient(new Client(Math.random() + "@gmail.com", "testsalt", "235jw23jehr529ef")).execute().body().getLogin();
        service.deleteClientByLogin(clientLogin).execute();
    }
}
