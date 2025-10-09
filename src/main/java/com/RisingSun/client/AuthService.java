package com.RisingSun.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.RisingSun.models.User;
import com.RisingSun.models.ApiResponse;

import java.io.IOException;

public class AuthService {
    private final ApiClient apiClient;
    private final Gson gson;

    public AuthService() {
        this.apiClient = new ApiClient();
        this.gson = new Gson();
    }

    public ApiResponse register(String username, String password) {
        try {
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("username", username);
            requestBody.addProperty("password", password);

            String response = apiClient.post("/auth/register", gson.toJson(requestBody));

            if (response.contains("User registered successfully")) {
                return new ApiResponse(true, "Регистрация успешна!");
            } else {
                JsonObject errorResponse = gson.fromJson(response, JsonObject.class);
                if (errorResponse.has("errors")) {
                    JsonObject errors = errorResponse.getAsJsonObject("errors");
                    String errorMsg = errors.entrySet().iterator().next().getValue().getAsString();
                    return new ApiResponse(false, errorMsg);
                }
                return new ApiResponse(false, "Ошибка регистрации");
            }
        } catch (IOException e) {
            return new ApiResponse(false, "Ошибка сети: " + e.getMessage());
        } catch (Exception e) {
            return new ApiResponse(false, "Ошибка: " + e.getMessage());
        }
    }

    public User login(String username, String password) {
        try {
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("username", username);
            requestBody.addProperty("password", password);

            String response = apiClient.post("/auth/login", gson.toJson(requestBody));

            if (response != null && !response.isEmpty() && response.length() > 10) {
                String token = response.trim().replace("\"", "");
                User user = new User(username, token);
                apiClient.setAuthToken(token);
                return user;
            }
        } catch (IOException e) {
            System.err.println("Login error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
        return null;
    }
}
