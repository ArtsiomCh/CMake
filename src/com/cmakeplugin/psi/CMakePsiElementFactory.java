package com.cmakeplugin.psi;

import com.cmakeplugin.CMakeLanguage;
import com.cmakeplugin.psi.impl.CMakePsiImplUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import org.jetbrains.annotations.NotNull;

public class CMakePsiElementFactory {

  private static PsiFile createFile(@NotNull Project project, @NotNull String text) {
    return PsiFileFactory.getInstance(project)
            .createFileFromText("a.cmake", CMakeLanguage.INSTANCE, text, false, false);
  }

  public static PsiElement createUnquotedArgumentFromText(@NotNull Project project, @NotNull String text) {
    return CMakePsiImplUtil.computeElementsOfClass(createFile(project,"set(" + text + ")"), CMakeUnquotedArgumentMaybeVariableContainer.class)
            .get(0).getUnquotedArgumentMaybeVarDef();
  }

}
