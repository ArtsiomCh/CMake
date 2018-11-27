package com.cmakeplugin.utils;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
    return CMakePDC.isUnquotedArgument(element)
            && CMakePDC.hasIfWhileParent(element)
            && !element.getText().matches("[0-9]+") //hack to skip numbers... not really correct
            && CMakeVarStringUtil.isPossibleVarDefinition(element.getText());
  }
}
