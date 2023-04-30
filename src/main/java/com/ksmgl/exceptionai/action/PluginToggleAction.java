package com.ksmgl.exceptionai.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.Toggleable;
import com.ksmgl.exceptionai.gui.icon.ToggleIcon;
import org.jetbrains.annotations.NotNull;

public class PluginToggleAction extends AnAction implements Toggleable {
  private static boolean isEnabled = false;

  public PluginToggleAction() {
    super("Toggle ExceptionAI");
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    isEnabled = !isEnabled;
    event.getPresentation().putClientProperty(Toggleable.SELECTED_PROPERTY, isEnabled);
  }

  public static boolean isEnabled() {
    return isEnabled;
  }

  @Override
  public void update(@NotNull AnActionEvent event) {
    super.update(event);
    Presentation presentation = event.getPresentation();
    presentation.setEnabled(true);
    presentation.setIcon(ToggleIcon.MY_PLUGIN_ICON);
    presentation.setDescription("Turn On/Off ExceptionAI");
  }
}