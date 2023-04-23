package com.ksmgl.exceptionai.form;

import com.intellij.openapi.ui.DialogWrapper;

import javax.annotation.Nullable;
import javax.swing.*;

public class OpenAIAPISettingsForm {
  private JPanel mainPanel;
  private JTextField apiKeyTextField;
  private JLabel apiKeyLabel;
  private JTextField modelTextField;
  private JLabel modelLabel;
  private JTextField maxTokensTextField;
  private JLabel maxTokenLabel;
  private JTextField tempTextField;
  private JLabel tempLabel;

  public JPanel getMainPanel() {
    return mainPanel;
  }

  public String getApiKey() {
    return apiKeyTextField.getText();
  }

  public String getModel() {
    return modelTextField.getText();
  }

  public String getMaxTokens() {
    return maxTokensTextField.getText();
  }

  public String getTemperature() {
    return tempTextField.getText();
  }

  public void setApiKey(String apiKey) {
    apiKeyTextField.setText(apiKey);
  }

  public void setModel(String model) {
    modelTextField.setText(model);
  }

  public void setMaxTokens(String maxTokens) {
    maxTokensTextField.setText(maxTokens);
  }

  public void setTemperature(String temperature) {
    tempTextField.setText(temperature);
  }

  public void show() {
    DialogWrapper dialog = new DialogWrapper(true) {
      {
        init();
      }

      @Nullable
      @Override
      protected JComponent createCenterPanel() {
        return mainPanel;
      }
    };
    dialog.setTitle("OpenAI API Configuration");
    dialog.showAndGet();
  }
}
