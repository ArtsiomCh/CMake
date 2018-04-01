package com.cmakeplugin.utils;

import com.cmakeplugin.psi.*;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.cidr.cpp.cmake.psi.CMakeLiteral;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Provide Platform independent (IDEA/CLion) API
 */
public class CMakeIFWHILEcheck {

  @NotNull
  public static List<TextRange> getOuterVarRefs(PsiElement element) {
    List<TextRange> result =  CMakeVarStringUtil.getOuterVarRefs(element.getText());
    if (result.isEmpty() && isVarInsideIFWHILE(element)) {
      result.add(new TextRange(0,element.getTextLength()));
    }
    return result;
  }

  @NotNull
  public static List<TextRange> getInnerVars(PsiElement element) {
    List<TextRange> result =  CMakeVarStringUtil.getInnerVars(element.getText());
    if (result.isEmpty() && isVarInsideIFWHILE(element)) {
      result.add(new TextRange(0,element.getTextLength()));
    }
    return result;
  }

  public static boolean isVarInsideIFWHILE(PsiElement element) {
    boolean result = false;
    if (element instanceof CMakeUnquotedArgumentContainer) { // IDEA
      result = PsiTreeUtil.getParentOfType(element
                ,CMakeIfExpr.class, CMakeElseifExpr.class, CMakeElseExpr.class, CMakeEndifExpr.class
                ,CMakeWhilebegin.class, CMakeWhileend.class)!=null
              && !element.getText().matches("[0-9]+"); //hack to skip numbers... not really correct
//    } else if (element instanceof CMakeLiteral) { // CLion
      //fixme implement for CLion
    }
    return result && CMakeVarStringUtil.isPossibleVarDefinition(element.getText());
  }
}
