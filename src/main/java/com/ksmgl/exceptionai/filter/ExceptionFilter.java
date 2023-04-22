package com.ksmgl.exceptionai.filter;

import com.intellij.execution.filters.ConsoleFilterProvider;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;
import com.ksmgl.exceptionai.client.OpenAIClient;
import com.ksmgl.exceptionai.config.OpenAIAPISettings;
import com.ksmgl.exceptionai.config.ExceptionAIConfigurable;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExceptionFilter implements ConsoleFilterProvider {

  private static final Pattern EXCEPTION_PATTERN = Pattern.compile("(?<exception>\\S+Exception): (?<message>.*)");
  private OpenAIClient openAiClient;

  @NotNull
  @Override
  public Filter[] getDefaultFilters(@NotNull Project project) {
    return new Filter[]{
        (line, entireLength) -> {
          Matcher matcher = EXCEPTION_PATTERN.matcher(line);
          if (matcher.find()) {
            String exception = matcher.group("exception");
            String message = matcher.group("message");

            HyperlinkInfo hyperlinkInfo = p -> {
              OpenAIAPISettings settings = OpenAIAPISettings.getInstance();
              if (!settings.isConfigured()) {
                showMissingApiKeyAlert(p);
              } else {
                openAiClient = new OpenAIClient(settings.getApiKey(), settings.getModel(), settings.getTemperature());
                String suggestion = openAiClient.getSuggestions(exception, message);
                showSuggestion(p, suggestion);
              }
            };
            return new Filter.Result(0, entireLength, hyperlinkInfo,
                new TextAttributes(JBColor.RED, null, null, null, Font.PLAIN));
          }
          return null;
        }
    };
  }

  private void showMissingApiKeyAlert(Project project) {
    String message = "Please set your OpenAI API key in the plugin settings.";
    String title = "OpenAI API Key Missing";
    int messageType = JOptionPane.WARNING_MESSAGE;
    JOptionPane.showMessageDialog(null, message, title, messageType);

    showOpenAIAPISettings(project);
  }

  private void showOpenAIAPISettings(Project project) {
    ShowSettingsUtil.getInstance().showSettingsDialog(project, ExceptionAIConfigurable.class);
  }

  private void showSuggestion(Project project, String suggestion) {
    try {
      ApplicationManager.getApplication().invokeLater(() -> {
        if (StringUtils.isBlank(suggestion)) {
          String invalidKey = "\nPlease make sure that your API Key and other settings are correct.\n\n" +
              "To update your API Key and/or other settings:\n\n" +
              "Go to \"File > Settings > ExceptionAI Settings\" \n\n" +
              "If you are not sure how to configure these settings please follow the link below. \n\n" +
              "https://platform.openai.com/docs/api-reference/completions/create";
          Messages.showMessageDialog(project, invalidKey, "ExceptionAI Configuration Error", Messages.getErrorIcon());
        } else {
          Messages.showMessageDialog(project, "\n"+suggestion, "ExceptionAI Suggestions", Messages.getInformationIcon());
        }
      });
    } catch (Throwable t) {
      // ignore.
    }
  }
}
