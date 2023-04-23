package com.ksmgl.exceptionai.config;

import org.apache.commons.lang.StringUtils;

public class OpenAIAPISettings {

  private static final OpenAIAPISettings instance = new OpenAIAPISettings();

  private OpenAIAPISettings() {
  }

  public static OpenAIAPISettings getInstance() {
    return instance;
  }

  private String apiKey = "";
  private String model = "gpt-3.5-turbo";
  private int maxTokens = 500;
  private double temperature = 1;

  public boolean isConfigured() {
    return !StringUtils.isBlank(apiKey) && !StringUtils.isBlank(model);
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public int getMaxTokens() {
    return maxTokens;
  }

  public void setMaxTokens(int maxTokens) {
    this.maxTokens = maxTokens;
  }

  public double getTemperature() {
    return temperature;
  }

  public void setTemperature(double temperature) {
    this.temperature = temperature;
  }

}
