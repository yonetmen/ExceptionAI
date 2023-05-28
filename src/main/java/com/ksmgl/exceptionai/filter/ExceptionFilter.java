package com.ksmgl.exceptionai.filter;

import com.intellij.execution.filters.ConsoleFilterProvider;
import com.intellij.execution.filters.Filter;
import com.intellij.openapi.project.Project;
import com.ksmgl.exceptionai.action.CodeEditorAction;
import com.ksmgl.exceptionai.action.ShowSuggestionAction;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExceptionFilter implements ConsoleFilterProvider {

  private static final CodeEditorAction cea = new CodeEditorAction();
  private static final ShowSuggestionAction ssa = new ShowSuggestionAction();
  private static final Pattern pattern = Pattern.compile("at ([a-zA-Z0-9_$.]+)\\.([a-zA-Z0-9_$]+)\\(([A-Za-z0-9-_$.]+\\.java):(\\d+)\\)");

  @NotNull
  @Override
  public Filter[] getDefaultFilters(@NotNull Project project) {
    return new Filter[]{
        (line, entireLength) -> {

          Matcher matcher = pattern.matcher(line);

          // Check if line matches the pattern before processing
          if (matcher.find()) {
            // Open the Class file in Code Editor
            cea.highlightExceptionSourceLine(project, line);
          }

          // Show suggestion about the Exception & Error message
          Filter.Result result = ssa.extract(line, entireLength);

          return result;
        }
    };
  }
}
