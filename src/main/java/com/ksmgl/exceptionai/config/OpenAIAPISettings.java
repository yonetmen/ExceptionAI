package com.ksmgl.exceptionai.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "ExceptionAIConfig",
    storages = {@Storage("ExceptionAIConfig.xml")}
)
public class OpenAIAPISettings implements PersistentStateComponent<OpenAIAPISettings.State> {

  private static final OpenAIAPISettings instance =
      ApplicationManager.getApplication().getService(OpenAIAPISettings.class);

  public static class State {
    public String apiKey = "";
    public String model = "gpt-3.5-turbo";
    public int maxTokens = 500;
    public double temperature = 1;
  }

  private final State appState = new State();

  public static OpenAIAPISettings getInstance() {
    return instance;
  }

  public boolean isConfigured() {
    return !StringUtils.isBlank(appState.apiKey) && !StringUtils.isBlank(appState.model);
  }

  public String getApiKey() {
    return appState.apiKey;
  }

  public void setApiKey(String apiKey) {
    appState.apiKey = apiKey;
  }

  public String getModel() {
    return appState.model;
  }

  public void setModel(String model) {
    appState.model = model;
  }

  public int getMaxTokens() {
    return appState.maxTokens;
  }

  public void setMaxTokens(int maxTokens) {
    appState.maxTokens = maxTokens;
  }

  public double getTemperature() {
    return appState.temperature;
  }

  public void setTemperature(double temperature) {
    appState.temperature = temperature;
  }

  @Nullable
  @Override
  public State getState() {
    return appState;
  }

  @Override
  public void loadState(@NotNull State state) {
    XmlSerializerUtil.copyBean(state, appState);
  }
}
