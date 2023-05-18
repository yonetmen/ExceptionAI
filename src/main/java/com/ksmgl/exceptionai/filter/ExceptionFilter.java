package com.ksmgl.exceptionai.filter;

import com.intellij.execution.filters.ConsoleFilterProvider;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.JBColor;
import com.ksmgl.exceptionai.action.PluginToggleAction;
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
          // Open the Class file in Code Editor
          Pattern pattern = Pattern.compile("at ([a-zA-Z0-9_$.]+)\\.([a-zA-Z0-9_$]+)\\(([A-Za-z0-9-_$.]+\\.java):(\\d+)\\)");
          Matcher sourceClassMatcher = pattern.matcher(line);
          if (PluginToggleAction.isEnabled()) {
            ApplicationManager.getApplication().runReadAction(() -> {

              while (sourceClassMatcher.find()) {
                String matchedClassName = sourceClassMatcher.group(1);
//                String matchedMethodName = matcher1.group(2);
//                String matchedFileName = matcher1.group(3);
                int matchedLineNumber = Integer.parseInt(sourceClassMatcher.group(4));

                // Check if the class belongs to the current project
                PsiClass psiClass = JavaPsiFacade.getInstance(project)
                    .findClass(matchedClassName, GlobalSearchScope.projectScope(project));

                if (psiClass != null) {
                  // Check if the file is in the project's source root
                  VirtualFile classFile = psiClass.getContainingFile().getVirtualFile();
                  boolean isInSourceRoot = ProjectRootManager.getInstance(project).getFileIndex().isInSourceContent(classFile);
                  if (isInSourceRoot) {
                    // Open the class in the IntelliJ code editor
                    System.out.println("psiClass: " + psiClass);
                    System.out.println("classFile: " + classFile);
                    openClassInEditor(project, psiClass, matchedLineNumber);
                    break;
                  }
                }
              }
            });
          }

          // Show Suggestion
          Matcher matcher = EXCEPTION_PATTERN.matcher(line);
          if (PluginToggleAction.isEnabled() && matcher.find()) {
            String exception = matcher.group("exception");
            String message = matcher.group("message");

            HyperlinkInfo hyperlinkInfo = p -> {
              OpenAIAPISettings settings = OpenAIAPISettings.getInstance();
              if (!settings.isConfigured()) {
                showMissingApiKeyAlert(p);
              } else {
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

  private void openClassInEditor(Project project, PsiClass psiClass, int lineNumber) {
    PsiFile psiFile = psiClass.getContainingFile();
    if (psiFile != null) {
      VirtualFile virtualFile = psiFile.getVirtualFile();
      if (virtualFile != null) {
        // Navigate to the specified line number
        OpenFileDescriptor descriptor = new OpenFileDescriptor(project, virtualFile, lineNumber - 1);
        ApplicationManager.getApplication().invokeLater(() -> {
          if (descriptor.canNavigate()) {
            descriptor.navigate(true);
          }
        });
      }
    }
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
              String.format("%d ExceptionAI Error", apiResponse.getCode()),
              Messages.getErrorIcon());
        }
      });
    } catch (Throwable t) {
      // ignore.
    }
  }
}
