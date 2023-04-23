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
import com.ksmgl.exceptionai.api.APIResponse;
import com.ksmgl.exceptionai.api.OpenAIClient;
import com.ksmgl.exceptionai.config.ExceptionAIConfigurable;
import com.ksmgl.exceptionai.config.OpenAIAPISettings;
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
                System.out.println(settings);
                openAiClient = new OpenAIClient(
                    settings.getApiKey(),
                    settings.getModel(),
                    settings.getTemperature(),
                    settings.getMaxTokens()
                );
                APIResponse apiResponse = openAiClient.getSuggestions(exception, message);
                showSuggestion(p, apiResponse);
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

  private void showSuggestion(Project project, APIResponse apiResponse) {
    try {
      ApplicationManager.getApplication().invokeLater(() -> {
        if (apiResponse.getCode() == 200) {
          Messages.showMessageDialog(
              project,
              "\n" + apiResponse.getMessage(),
              "ExceptionAI Suggestions",
              Messages.getInformationIcon());
        } else {
          Messages.showMessageDialog(
              project,
              apiResponse.getMessage(),
              "ExceptionAI Configuration Error",
              Messages.getErrorIcon());
        }
      });
    } catch (Throwable t) {
      // ignore.
    }
  }
}
