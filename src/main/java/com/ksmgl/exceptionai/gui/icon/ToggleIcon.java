package com.ksmgl.exceptionai.gui.icon;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class ToggleIcon {
  public static final Icon TOGGLE_ON =
      IconLoader.getIcon("/power_on.svg", ToggleIcon.class.getClassLoader());
  public static final Icon TOGGLE_OFF =
      IconLoader.getIcon("/power_off.svg", ToggleIcon.class.getClassLoader());
}
