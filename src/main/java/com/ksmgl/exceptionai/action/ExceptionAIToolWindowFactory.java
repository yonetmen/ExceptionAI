package com.ksmgl.exceptionai.action;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class ExceptionAIToolWindowFactory implements ToolWindowFactory {

  @Override
  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    // Create an empty content
    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    Content content = contentFactory.createContent(null, "", false);
    toolWindow.getContentManager().addContent(content);

    // Create a toolbar with the toggle action
    DefaultActionGroup actionGroup = new DefaultActionGroup("ExceptionAIToggleToolbarGroup", false);
    actionGroup.add(ActionManager.getInstance().getAction("PluginToggleAction"));
    toolWindow.setAdditionalGearActions(actionGroup);
  }
}