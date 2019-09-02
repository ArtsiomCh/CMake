package com.cmakeplugin.utils;

import static com.cmakeplugin.utils.CMakePDC.isCLION;

import com.cmakeplugin.psi.*;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class CmakePlusPDC {

  @NotNull
  @SuppressWarnings("unchecked")
  public static final Class<? extends PsiElement>[] foldableBodies =
      (isCLION)
          ? new Class[] {com.jetbrains.cmake.psi.CMakeBodyBlock.class}
          : new Class[] {
            CMakeFunbody.class,
            CMakeMacrobody.class,
            CMakeIfbody.class,
            CMakeForbody.class,
            CMakeWhilebody.class
//            PsiComment.class
          };
}
