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
  private static final Map<Pair<String, Integer>, RangeHighlighter> highlighters = new HashMap<>();

  public void highlightExceptionSourceLine(@NotNull Project project, String line) {
    clearAllHighlights();
    Pattern pattern = Pattern.compile("at ([a-zA-Z0-9_$.]+)\\.([a-zA-Z0-9_$]+)\\(([A-Za-z0-9-_$.]+\\.java):(\\d+)\\)");
    Matcher sourceClassMatcher = pattern.matcher(line);
    if (PluginToggleAction.isEnabled()) {
      ApplicationManager.getApplication().runReadAction(() -> {
        while (sourceClassMatcher.find()) {
          String matchedClassName = sourceClassMatcher.group(1);
          int matchedLineNumber = Integer.parseInt(sourceClassMatcher.group(4));

          // Check if the class belongs to the current project
          PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(matchedClassName, GlobalSearchScope.projectScope(project));

          if (psiClass != null) {
            // Check if the file is in the project's source root
            VirtualFile classFile = psiClass.getContainingFile().getVirtualFile();
            boolean isInSourceRoot = ProjectRootManager.getInstance(project).getFileIndex().isInSourceContent(classFile);
            if (isInSourceRoot) {
              navigateToAndHighlightLine(project, psiClass, matchedLineNumber);
              break;
            }
          }
        }
      });
    }
  }

  private void navigateToAndHighlightLine(Project project, PsiClass psiClass, int lineNumber) {
    PsiFile psiFile = psiClass.getContainingFile();
    if (psiFile != null) {
      VirtualFile virtualFile = psiFile.getVirtualFile();
      if (virtualFile != null) {
        // Navigate to the specified line number
        OpenFileDescriptor descriptor = new OpenFileDescriptor(project, virtualFile, lineNumber - 1);
        ApplicationManager.getApplication().invokeLater(() -> {
          if (descriptor.canNavigate()) {
            descriptor.navigate(true);
            final Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (editor != null) {
              Runnable[] command = new Runnable[1];
              command[0] = () -> {
                if (editor.getCaretModel().getOffset() == descriptor.getOffset()) {
                  editor.getSelectionModel().selectLineAtCaret();
                  // Get the document and markup model for the editor
                  Document document = editor.getDocument();

                  // Get the start and end offsets for the line
                  int lineStartOffset = document.getLineStartOffset(lineNumber - 1);
                  int lineEndOffset = document.getLineEndOffset(lineNumber - 1);

                  // Create the text attributes (e.g., with a yellow background)
                  JBColor semiTransparentYellow = new JBColor(new Color(33, 66, 131, 255), new Color(33, 66, 131, 255));
                  TextAttributes attributes = new TextAttributes(null, semiTransparentYellow, null, null, 0);

                  Pair<String, Integer> key = new Pair<>(psiClass.getQualifiedName(), lineNumber);
                  RangeHighlighter existingHighlighter = highlighters.get(key);
                  if (existingHighlighter == null || !existingHighlighter.isValid()) {
                    RangeHighlighter highlighter = editor.getMarkupModel().addRangeHighlighter(lineStartOffset, lineEndOffset, HighlighterLayer.ERROR, attributes, HighlighterTargetArea.LINES_IN_RANGE);
                    highlighters.put(key, highlighter);
                  }
                } else {
                  editor.getCaretModel().moveToOffset(descriptor.getOffset());
                  ApplicationManager.getApplication().invokeLater(command[0]);
                }
              };
              command[0].run();
            }
          }
        });
      }
    }
  }

  public void clearAllHighlights() {
    ApplicationManager.getApplication().invokeLater(() -> {
      for (Map.Entry<Pair<String, Integer>, RangeHighlighter> entry : highlighters.entrySet()) {
        entry.getValue().dispose();
      }
      highlighters.clear();
    });
  }

}
