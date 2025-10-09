package com.RisingSun.client;


import okhttp3.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    private static final String BASE_URL = "http://95.154.89.8:8888/app";
    private final OkHttpClient client;
    private String authToken;

    public ApiClient() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    public String get(String endpoint) throws IOException {
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + endpoint)
                .get();

        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP " + response.code() + ": " + response.message());
            }
            return response.body().string();
        }
    }

    public String post(String endpoint, String jsonBody) throws IOException {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + endpoint)
                .post(body);

        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        Request request = builder.build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}
