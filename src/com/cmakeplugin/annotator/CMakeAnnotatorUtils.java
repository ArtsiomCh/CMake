package com.cmakeplugin.annotator;

import com.cmakeplugin.CMakeSyntaxHighlighter;

import com.cmakeplugin.utils.CMakePSITreeSearch;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import com.cmakeplugin.utils.CMakeIFWHILEcheck;
import com.cmakeplugin.utils.CMakeVarStringUtil;

//import static CMakeKeywords.*;

class CMakeAnnotatorUtils {

  static boolean annotateLegacy(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    if (element.getText().matches("(.*[^\\\\]\"([^\"]*[^\\\\])?\".*)+|(.*\\$\\(.*\\).*)")) { //fixme
      holder.createInfoAnnotation(element, null)
              .setTextAttributes(CMakeSyntaxHighlighter.UNQUOTED_LEGACY);
      return true;
    } else return false;
  }

  static boolean annotatePathURL(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    if (element.getText().contains("/")) {
      holder.createInfoAnnotation(element, null)
              .setTextAttributes(CMakeSyntaxHighlighter.CMAKE_PATH_URL);
      return true;
    } else return false;
  }

  static boolean annotatePredefinedVariable(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    for (String varRegexp : CMakeKeywords.variables_All) {
      if (element.getText().matches(varRegexp)
              && !CMakeIFWHILEcheck.isVarInsideIFWHILE(element)) {
        holder.createInfoAnnotation(element, null)
                .setTextAttributes(CMakeSyntaxHighlighter.CMAKE_VAR_DEF);
        return true;
      }
    }
    return false;
  }

  static boolean annotateVarDeclaration(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    if (CMakePSITreeSearch.existReferenceTo(element)) {
      holder.createInfoAnnotation(element, null)
              .setTextAttributes(CMakeSyntaxHighlighter.VAR_DEF);
      return true;
    }
    return false;
}

  static void annotateVarReferences(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    String argtext = element.getText();
    int pos = element.getTextRange().getStartOffset();

    // Highlight Outer variables.
    for ( TextRange outerVarRange: CMakeIFWHILEcheck.getOuterVarRefs(element)) {
      holder.createInfoAnnotation( outerVarRange.shiftRight(pos), null)
              .setTextAttributes(CMakeSyntaxHighlighter.VAR_REF);
    }

    // Highlight Inner variables.
    boolean isCmakePredefinedVar = false;
    for ( TextRange innerVarRange: CMakeIFWHILEcheck.getInnerVars(element) ) {
      String innerVarName = argtext.substring(innerVarRange.getStartOffset(),innerVarRange.getEndOffset());

      // Highlight Inner CMake predefined variables
      for (String varRegexp: CMakeKeywords.variables_All ) {
        if ( innerVarName.matches(varRegexp)) {
          holder.createInfoAnnotation(innerVarRange.shiftRight(pos), null)
                  .setTextAttributes(CMakeSyntaxHighlighter.CMAKE_VAR_REF);
          isCmakePredefinedVar = true;
          break;
        }
      }

// TODO Move it to Inspections? Also too many false negative.
      // Highlight not defined Inner variable.
      if (!isCmakePredefinedVar && CMakePSITreeSearch.findVariableDefinitions(element, innerVarName).isEmpty()) {
        holder.createWeakWarningAnnotation(innerVarRange.shiftRight(pos),"Possibly not defined Variable: "+ innerVarName)
                .setHighlightType(ProblemHighlightType.WEAK_WARNING);
      }
    }

    // Highlight Inner CMake predefined ENV variables
    //fixme: implement ref/resolve for ENV
    for ( TextRange innerVarRange: CMakeVarStringUtil.getInnerEnvVars(argtext) ) {
      for (String varRegexp: CMakeKeywords.variables_ENV ) {
        if (argtext.substring(innerVarRange.getStartOffset(),innerVarRange.getEndOffset())
                .matches(varRegexp)) {
          holder.createInfoAnnotation(innerVarRange.shiftRight(pos), null)
                  .setTextAttributes(CMakeSyntaxHighlighter.CMAKE_VAR_REF);
          break;
        }
      }
    }
  }

  static void annotateCommand(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    String commandName = element.getText().toLowerCase();
    if (CMakeKeywords.commands_Project.contains(commandName)
            || CMakeKeywords.commands_Scripting.contains(commandName)
            || CMakeKeywords.commands_Test.contains(commandName)
            ) {
      holder.createInfoAnnotation(element, null)
              .setTextAttributes(CMakeSyntaxHighlighter.CMAKE_COMMAND);
    } else if (CMakeKeywords.commands_Deprecated.contains(commandName)){
      holder.createWarningAnnotation(element,"Deprecated command")
            .setHighlightType(ProblemHighlightType.LIKE_DEPRECATED);
    }
  }

  static boolean annotateProperty(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    String propertyName = element.getText();
    for (String varRegexp: CMakeKeywords.properties_All){
      if (propertyName.matches(varRegexp)){
        holder.createInfoAnnotation(element, null)
                .setTextAttributes(CMakeSyntaxHighlighter.CMAKE_PROPERTY);
        return true;
      }
    }
    for (String varRegexp: CMakeKeywords.properties_Deprecated){
      if (propertyName.matches(varRegexp)) {
        holder.createWarningAnnotation(element, "Deprecated property")
                .setHighlightType(ProblemHighlightType.LIKE_DEPRECATED);
        return true;
      }
    }
    return false;
  }

  static boolean annotateOperator(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    String operatorName = element.getText();
    if (CMakeKeywords.operators.contains(operatorName) ) {
      holder.createInfoAnnotation(element, null)
              .setTextAttributes(CMakeSyntaxHighlighter.CMAKE_OPERATOR);
      return true;
    }
    for (String boolValue: CMakeKeywords.boolValues){
      if (operatorName.toUpperCase().matches(boolValue)){
        holder.createInfoAnnotation(element, null)
                .setTextAttributes(CMakeSyntaxHighlighter.CMAKE_PROPERTY);
        return true;
      }
    }
    return false;
  }

}
