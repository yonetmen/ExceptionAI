package com.ksmgl.exceptionai.filter;

import com.intellij.execution.filters.ConsoleFilterProvider;
import com.intellij.execution.filters.Filter;
import com.intellij.openapi.project.Project;
import com.ksmgl.exceptionai.action.CodeEditorAction;
import com.ksmgl.exceptionai.action.ShowSuggestionAction;
import org.jetbrains.annotations.NotNull;

public class ExceptionFilter implements ConsoleFilterProvider {

  private static final CodeEditorAction cea = new CodeEditorAction();
  private static final ShowSuggestionAction ssa = new ShowSuggestionAction();

  @NotNull
  @Override
  public Filter[] getDefaultFilters(@NotNull Project project) {
    return new Filter[]{
        (line, entireLength) -> {

          // Open the Class file in Code Editor
          cea.openClassInCodeEditor(project, line);

          // Show suggestion about the Exception & Error message
          Filter.Result result = ssa.extract(line, entireLength);

          return result;
        }
    };
  }
}
