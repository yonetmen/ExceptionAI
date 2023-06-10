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

  public boolean isModified() {
    if (form != null) {
      return !form.getApiKey().equals(settings.getApiKey())
          || !form.getModel().equals(settings.getModel())
          || !form.getMaxTokens().equals(String.valueOf(settings.getMaxTokens()))
          || !form.getTemperature().equals(String.valueOf(settings.getTemperature()));
    }
    return false;
  }

  @Override
  public void apply() {
    if (form != null) {
      settings.setApiKey(form.getApiKey());
      settings.setModel(form.getModel());
      settings.setMaxTokens(getMaxTokensValue(form.getMaxTokens()));
      settings.setTemperature(getMaxTemperatureValue(form.getTemperature()));
    }
  }

  @Override
  public void reset() {
    if (form != null) {
      form.setApiKey(settings.getApiKey());
      form.setModel(settings.getModel());
      form.setMaxTokens(String.valueOf(settings.getMaxTokens()));
      form.setTemperature(String.valueOf(settings.getTemperature()));
    }
  }

  @Override
  public void disposeUIResources() {
    form = null;
  }

  private double getMaxTemperatureValue(String temperature) {
    try {
      double value = Double.parseDouble(temperature);
      if (value < 0) return 0.0;
      if (value > 2) return 2.0;
      return value;
    } catch (Exception e) {
      return 1.0;
    }
  }

  private int getMaxTokensValue(String maxTokens) {
    try {
      int value = Integer.parseInt(maxTokens);
      if (value < 100) return 100;
      return Math.min(value, 2048);
    } catch (Exception e) {
      return 100;
    }
  }
}