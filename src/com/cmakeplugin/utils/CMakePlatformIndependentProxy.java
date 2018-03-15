package com.cmakeplugin.utils;

import com.cmakeplugin.psi.*;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Provide Platform independent (IDEA/CLion) API
 */
public class CMakePlatformIndependentProxy {
  public enum PLATFORM {IDEA, CLION}

  @NotNull
  public static List<TextRange> getPIOuterVarRefs(PLATFORM platform, PsiElement element) {
    List<TextRange> result =  CMakeVarStringUtil.getOuterVarRefs(element.getText());
    if (result.isEmpty() && isVarInsideIFWHILE(platform, element)) {
      result.add(new TextRange(0,element.getTextLength()));
    }
    return result;
  }

  @NotNull
  public static List<TextRange> getPIInnerVars(PLATFORM platform, PsiElement element) {
    List<TextRange> result =  CMakeVarStringUtil.getInnerVars(element.getText());
    if (result.isEmpty() && isVarInsideIFWHILE(platform, element)) {
      result.add(new TextRange(0,element.getTextLength()));
    }
    return result;
  }

  static boolean isVarInsideIFWHILE(PLATFORM platform, PsiElement element) {
    boolean result = false;
    if (platform == PLATFORM.IDEA) {
      result = (element instanceof CMakeUnquotedArgumentContainer)
              && PsiTreeUtil.getParentOfType(element
                ,CMakeIfExpr.class, CMakeElseifExpr.class, CMakeElseExpr.class, CMakeEndifExpr.class
                ,CMakeWhilebegin.class, CMakeWhileend.class)!=null
              && !element.getText().matches("[0-9]+"); //hack to skip numbers... not really correct
    } else if (platform == PLATFORM.CLION) {
      //fixme implement for CLion
    }
    return result && CMakeVarStringUtil.isPossibleVarDefinition(element.getText());
  }
}
