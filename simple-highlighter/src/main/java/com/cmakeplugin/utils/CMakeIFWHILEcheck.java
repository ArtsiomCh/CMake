package com.cmakeplugin.utils;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import java.util.Collections;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CMakeIFWHILEcheck {

  @NotNull
  public static List<TextRange> getOuterVarRefs(PsiElement element) {
    List<TextRange> result = CMakeVarStringUtil.getOuterVarRefs(element.getText());
    if (result.isEmpty()
        && isVarRefInsideIFWHILE(element)
        // exclude unquoted arg inside If/While
        && CMakePSITreeSearch.existDefinitionOf(element, element.getText())) {
      return Collections.singletonList(new TextRange(0, element.getTextLength()));
    }
    return result;
  }

  @NotNull
  public static List<TextRange> getInnerVars(PsiElement element) {
    List<TextRange> result = CMakeVarStringUtil.getInnerVars(element.getText());
    if (result.isEmpty() && isVarRefInsideIFWHILE(element)) {
      return Collections.singletonList(new TextRange(0, element.getTextLength()));
    }
    return result;
  }

  public static boolean isVarRefInsideIFWHILE(PsiElement element) {
    return CMakePDC.isClassOfVarRefInsideIfWhile(element)
        && CMakePDC.hasIfWhileParent(element)
        && CMakeVarStringUtil.couldBeVarName(element.getText())
        // hack to skip numbers... not really correct
        && !element.getText().matches("[0-9]+")
    //        && CMakePSITreeSearch.existDefinitionOf(element, element.getText())
    ;
  }

  public static boolean couldBeVarDef(PsiElement element) {
    return CMakePDC.isClassOfVarDef(element)
        && !CMakePDC.hasIfWhileParent(element)
        && CMakeVarStringUtil.couldBeVarName(element.getText())
        && CMakePDC.checkSetCommandSemantic(element)
        ;
  }
}
