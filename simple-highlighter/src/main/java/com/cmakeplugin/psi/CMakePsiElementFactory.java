package com.cmakeplugin.psi;

import com.cmakeplugin.CMakeLanguage;
import com.cmakeplugin.psi.impl.CMakePsiImplUtil;
import com.cmakeplugin.utils.CMakeIFWHILEcheck;
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

  public static PsiElement createArgumentFromText(@NotNull PsiElement element,
                                                  @NotNull String text,
                                                  @NotNull final Class<? extends PsiElement> aClass) {
    String fileText = CMakeIFWHILEcheck.isVarRefInsideIFWHILE(element)
            ? "if(" + text + ") endif()"
            : "set(" + text + ")";
    return CMakePsiImplUtil.computeElementsOfClass( createFile( element.getProject(), fileText), aClass)
            .get(0).getFirstChild();
  }

}
