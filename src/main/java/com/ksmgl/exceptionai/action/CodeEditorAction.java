package com.ksmgl.exceptionai.action;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeEditorAction {

  public void openClassInCodeEditor(@NotNull Project project, String line) {
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
              openClassInEditor(project, psiClass, matchedLineNumber);
              break;
            }
          }
        }
      });
    }
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
}
