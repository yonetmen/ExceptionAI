package com.ksmgl.exceptionai.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.ksmgl.exceptionai.form.OpenAIAPISettingsForm;
import org.jetbrains.annotations.NotNull;

public class ShowSettingsAction extends AnAction {
  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    Project project = e.getProject();
    if (project != null) {
      OpenAIAPISettingsForm openAIAPISettingsForm = new OpenAIAPISettingsForm();
      openAIAPISettingsForm.show();
    }
  }
}