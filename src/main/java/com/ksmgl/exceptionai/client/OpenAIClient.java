package com.ksmgl.exceptionai.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OpenAIClient {
  private static final String API_URL = "https://api.openai.com/v1/chat/completions";
  private final OkHttpClient client;
  private static final Gson gson = new Gson();
  private final String apiKey;
  private final String model;
  private final double temperature;

  public OpenAIClient(String apiKey, String model, double temperature) {
    this.client = new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build();
    this.apiKey = apiKey;
    this.model = model;
    this.temperature = temperature;
  }

  public String getSuggestions(String exception, String message) {
    String prompt = String.format("I encountered a Java exception: %s with the message: %s. " +
        "What are some possible solutions to fix this issue?", exception, message);

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("model", this.model);
    requestBody.put("temperature", this.temperature);

    List<Map<String, String>> messages = new ArrayList<>();
    Map<String, String> userMessage = new HashMap<>();
    userMessage.put("role", "user");
    userMessage.put("content", prompt);
    messages.add(userMessage);
    requestBody.put("messages", messages);

    Gson gson = new Gson();
    String jsonRequestBody = gson.toJson(requestBody);

    RequestBody body = RequestBody.create(jsonRequestBody, MediaType.parse("application/json"));

    Request request = new Request.Builder()
        .url(API_URL)
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", String.format("Bearer %s", this.apiKey))
        .post(body)
        .build();

    String suggestion = "";
    try (Response response = client.newCall(request).execute()) {
      if (response.isSuccessful() && response.body() != null) {
        String responseBody = response.body().string();
        JsonObject jsonResponse = OpenAIClient.gson.fromJson(responseBody, JsonObject.class);
        JsonArray choices = jsonResponse.getAsJsonArray("choices");
        if (choices.size() > 0) {
          JsonObject jsonObject = choices.get(0).getAsJsonObject().getAsJsonObject("message");
          suggestion = jsonObject.get("content").getAsString().trim();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return suggestion;
  }

}
