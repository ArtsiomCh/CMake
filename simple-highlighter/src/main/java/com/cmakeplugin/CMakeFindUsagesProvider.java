package com.cmakeplugin;

import com.cmakeplugin.psi.CMakeUnquotedArgumentMaybeVariableContainer;
import com.intellij.lang.findUsages.EmptyFindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;

public class CMakeFindUsagesProvider extends EmptyFindUsagesProvider {

  @Override
  public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
    return psiElement instanceof PsiNamedElement;
  }

  @NotNull
  @Override
  public String getType(@NotNull PsiElement psiElement) {
    String result = "";
    if (psiElement instanceof CMakeUnquotedArgumentMaybeVariableContainer) result = "variable";
/*
    else
      // psiElement and WrappedCmakeElement have different class-loaders.
      try {
        Class<?> clazzWrappedCmakeElement =
            psiElement
                .getClass()
                .getClassLoader()
                .loadClass("com.cmakeplugin.utils.wrappers.WrappedCmakeElement");
        if (clazzWrappedCmakeElement.isInstance(psiElement)) {
          Method m = clazzWrappedCmakeElement.getMethod("getType");
          result = (String) m.invoke(psiElement);
        }
      } catch (ClassNotFoundException
          | NoSuchMethodException
          | IllegalAccessException
          | InvocationTargetException e) {
        System.out.println(e.getMessage());
      }
*/
    return result;
  }
}
