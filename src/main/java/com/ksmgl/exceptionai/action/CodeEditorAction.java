package com.ksmgl.exceptionai.action;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.JBColor;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeEditorAction {

  private static final Map<Pair<String, Integer>, RangeHighlighter> HIGHLIGHTERS = new HashMap<>();
  private static final JBColor HIGHLIGHT_COLOR =
      new JBColor(new Color(33, 66, 131, 255), new Color(33, 66, 131, 255));
  private static final TextAttributes HIGHLIGHT_ATTRIBUTES =
      new TextAttributes(null, HIGHLIGHT_COLOR, null, null, 0);

  public void handleExceptionTrace(@NotNull Project project, String line, Pattern pattern) {
    Matcher matcher = pattern.matcher(line);
    if (PluginToggleAction.isEnabled()) {
      ApplicationManager.getApplication().runReadAction(() -> {
        while (matcher.find()) {
          String className = matcher.group(1);
          int lineNumber;
          try {
            lineNumber = Integer.parseInt(matcher.group(4));
          } catch (NumberFormatException e) {
            continue;
          }

          PsiClass psiClass = getClassFromProject(project, className);
          if (psiClass != null) {
            navigateAndHighlightLine(project, psiClass, lineNumber);
          }
        }
      });
    }
  }

  private PsiClass getClassFromProject(Project project, String className) {
    PsiClass psiClass = JavaPsiFacade.getInstance(project)
        .findClass(className, GlobalSearchScope.projectScope(project));
    if (psiClass != null) {
      VirtualFile classFile = psiClass.getContainingFile().getVirtualFile();
      boolean isInSourceRoot = ProjectRootManager.getInstance(project)
          .getFileIndex().isInSourceContent(classFile);
      if (isInSourceRoot) {
        return psiClass;
      }
    }
    return null;
  }

  private void navigateAndHighlightLine(Project project, PsiClass psiClass, int lineNumber) {
    PsiFile psiFile = psiClass.getContainingFile();
    if (psiFile != null) {
      VirtualFile virtualFile = psiFile.getVirtualFile();
      if (virtualFile != null) {
        OpenFileDescriptor descriptor = new OpenFileDescriptor(project, virtualFile, lineNumber - 1);
        ApplicationManager.getApplication().invokeLater(() -> {
          if (descriptor.canNavigate()) {
            descriptor.navigate(true);
            final Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (editor != null) {
              highlightLineInEditor(editor, descriptor, psiClass, lineNumber);
            }
          }
        });
      }
    }
  }

  private void highlightLineInEditor(
      Editor editor,
      OpenFileDescriptor descriptor,
      PsiClass psiClass,
      int lineNumber) {
    Runnable[] command = new Runnable[1];
    command[0] = () -> {
      if (editor.getCaretModel().getOffset() == descriptor.getOffset()) {
        editor.getSelectionModel().selectLineAtCaret();
        Document document = editor.getDocument();
        int lineStartOffset = document.getLineStartOffset(lineNumber - 1);
        int lineEndOffset = document.getLineEndOffset(lineNumber - 1);

        Pair<String, Integer> key = new Pair<>(psiClass.getQualifiedName(), lineNumber);
        RangeHighlighter existingHighlighter = HIGHLIGHTERS.get(key);
        if (existingHighlighter == null || !existingHighlighter.isValid()) {
          RangeHighlighter highlighter = editor.getMarkupModel()
              .addRangeHighlighter(
                  lineStartOffset,
                  lineEndOffset,
                  HighlighterLayer.ERROR,
                  HIGHLIGHT_ATTRIBUTES,
                  HighlighterTargetArea.LINES_IN_RANGE);
          HIGHLIGHTERS.put(key, highlighter);
        }
      } else {
        editor.getCaretModel().moveToOffset(descriptor.getOffset());
        ApplicationManager.getApplication().invokeLater(command[0]);
      }
    };
    command[0].run();
  }

  public void clearAllHighlights() {
    ApplicationManager.getApplication().invokeLater(() -> {
      for (Map.Entry<Pair<String, Integer>, RangeHighlighter> entry : HIGHLIGHTERS.entrySet()) {
        entry.getValue().dispose();
      }
      HIGHLIGHTERS.clear();
    });
  }
}