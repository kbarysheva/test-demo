package com.acme.dbo.retrofit;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ClientService {
    @GET("client/{id}")
    @Headers("X-API-VERSION:1")
    Call<Client> getClient(@Path("id") Integer id);

    @GET("client")
    @Headers("X-API-VERSION:1")
    Call<List<Client>> getClients();

    @POST("client")
    @Headers("X-API-VERSION:1")
    Call<Client> createClient(@Body Client client);

    @DELETE("client/{id}")
    @Headers("X-API-VERSION:1")
    Call<Void> deleteClientById(@Path("id") Integer id);

    @DELETE("client/login/{clientLogin}")
    @Headers("X-API-VERSION:1")
    Call<Void> deleteClientByLogin(@Path("clientLogin") String login);
}
