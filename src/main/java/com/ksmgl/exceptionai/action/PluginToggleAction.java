package com.ksmgl.exceptionai.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.Toggleable;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;

import static com.ksmgl.exceptionai.gui.icon.ToggleIcon.TOGGLE_OFF;
import static com.ksmgl.exceptionai.gui.icon.ToggleIcon.TOGGLE_ON;

public class PluginToggleAction extends AnAction implements Toggleable {
  private static boolean isEnabled = true;

  public PluginToggleAction() {
    super("Toggle On ExceptionAI");
  }

  public static boolean isEnabled() {
    return isEnabled;
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    isEnabled = !isEnabled;
  }

  @Override
  public void update(@NotNull AnActionEvent event) {
    super.update(event);
    Presentation presentation = event.getPresentation();
    presentation.putClientProperty(Key.create("selected"), isEnabled);
    presentation.setEnabled(true);
    presentation.setIcon(isEnabled ? TOGGLE_ON : TOGGLE_OFF);
    presentation.setDescription("Toggle ExceptionAI");
    presentation.setText(isEnabled ? "Turn Off ExceptionAI" : "Turn On ExceptionAI");
  }
}