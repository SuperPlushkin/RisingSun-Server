package com.RisingSun.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.List;

public class MessageService {
    private final ApiClient apiClient;
    private final Gson gson;

    public MessageService(String authToken) {
        this.apiClient = new ApiClient();
        this.gson = new Gson();
        this.apiClient.setAuthToken(authToken);
    }

    public List<String> getMessages() {
        try {
            String response = apiClient.get("/actions/messages");
            return gson.fromJson(response, new TypeToken<List<String>>(){}.getType());
        } catch (IOException e) {
            System.err.println("Error getting messages: " + e.getMessage());
            return null;
        }
    }

    public boolean sendMessage(String message) {
        try {
            String response = apiClient.post("/actions/messages", gson.toJson(message));
            return response != null && response.contains("Сообщение добавлено");
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
            return false;
        }
    }

    public String getSystemStatus() {
        try {
            return apiClient.get("/actions/status");
        } catch (IOException e) {
            System.err.println("Error getting status: " + e.getMessage());
            return null;
        }
    }

    public String getHello() {
        try {
            return apiClient.get("/actions/hello");
        } catch (IOException e) {
            System.err.println("Error getting hello: " + e.getMessage());
            return null;
        }
    }
}
