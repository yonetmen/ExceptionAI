package com.ksmgl.exceptionai.config;

import com.intellij.openapi.options.SearchableConfigurable;
import com.ksmgl.exceptionai.form.OpenAIAPISettingsForm;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ExceptionAIConfigurable implements SearchableConfigurable {
  private OpenAIAPISettingsForm form;
  private final OpenAIAPISettings settings;

  public ExceptionAIConfigurable() {
    settings = OpenAIAPISettings.getInstance();
  }

  @Override
  public @NotNull
  @NonNls String getId() {
    return "ExceptionAI.Settings";
  }

  @Nls(capitalization = Nls.Capitalization.Title)
  @Override
  public String getDisplayName() {
    return "ExceptionAI Settings";
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    form = new OpenAIAPISettingsForm();
    return form.getMainPanel();
  }

  @Override
  public boolean isModified() {
    return !form.getApiKey().equals(settings.getApiKey())
        || !form.getModel().equals(settings.getModel())
        || !form.getMaxTokens().equals(String.valueOf(settings.getMaxTokens()))
        || !form.getN().equals(String.valueOf(settings.getN()))
        || !form.getTemperature().equals(String.valueOf(settings.getTemperature()));
  }

  @Override
  public void apply() {
    settings.setApiKey(form.getApiKey());
    settings.setModel(form.getModel());
    if (isMaxTokensValid(form.getMaxTokens()))
      settings.setMaxTokens(Integer.parseInt(form.getMaxTokens()));
    if (isNValid(form.getN()))
      settings.setN(Integer.parseInt(form.getN()));
    if (isTemperatureValid(form.getTemperature())) {
      double value = Double.parseDouble(form.getTemperature());
      if (value >= 0 || value <= 2)
        settings.setTemperature(Double.parseDouble(form.getTemperature()));
      else
        settings.setTemperature(1);
    }
  }

  private boolean isMaxTokensValid(String maxTokens) {
    try {
      int value = Integer.parseInt(maxTokens);
      return value >= 16 && value <= 2048;
    } catch (Exception e) {
      return false;
    }
  }

  private boolean isNValid(String n) {
    try {
      Integer.parseInt(n);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private boolean isTemperatureValid(String temperature) {
    try {
      Double.parseDouble(temperature);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public void reset() {
    form.setApiKey(settings.getApiKey());
    form.setMaxTokens(settings.getModel());
    form.setMaxTokens(String.valueOf(settings.getMaxTokens()));
    form.setN(String.valueOf(settings.getN()));
    form.setTemperature(String.valueOf(settings.getTemperature()));
  }

  @Override
  public void disposeUIResources() {
    form = null;
  }
}