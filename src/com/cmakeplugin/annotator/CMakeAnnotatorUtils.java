package com.cmakeplugin.annotator;

//import com.cmakeplugin.CMakeSyntaxHighlighter;

import com.cmakeplugin.utils.CMakePSITreeSearch;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.cmakeplugin.utils.CMakePlatformIndependentProxy.*;
import com.cmakeplugin.utils.CMakeVarStringUtil;

//import static CMakeKeywords.*;

class CMakeAnnotatorUtils {

  static boolean annotateLegacy(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    if (element.getText().matches("(.*[^\\\\]\"([^\"]*[^\\\\])?\".*)+|(.*\\$\\(.*\\).*)")) { //fixme
      holder.createInfoAnnotation(element, null)
              .setTextAttributes(DefaultLanguageHighlighterColors.DOC_COMMENT_TAG);
      return true;
    } else return false;
  }

  static boolean annotatePathURL(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    if (element.getText().contains("/")) {
//      TextRange range = new TextRange(element.getTextRange().getStartOffset(),
//              element.getTextRange().getStartOffset() + element.getTextRange().getLength());
//      holder.createInfoAnnotation(range, null)
      holder.createInfoAnnotation(element, null)
              .setTextAttributes(DefaultLanguageHighlighterColors.DOC_COMMENT_TAG_VALUE);
//              .setTextAttributes(CMakeSyntaxHighlighter.CMAKE_PATH_URL);
      return true;
    } else return false;
  }

  static boolean annotateVariable(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    for (String varRegexp : CMakeKeywords.variables_All) {
      if (element.getText().matches(varRegexp)) {
        holder.createInfoAnnotation(element, null)
                .setTextAttributes(DefaultLanguageHighlighterColors.CONSTANT);
//                  .setTextAttributes(CMakeSyntaxHighlighter.CMAKE_VARIABLE);
        return true;
      }
    }
    return false;
  }

  static void annotateVarDelcaration(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    // Highlight variable definition.
    holder.createInfoAnnotation(element, null)
            .setTextAttributes(DefaultLanguageHighlighterColors.INSTANCE_FIELD);
//                  .setTextAttributes(CMakeSyntaxHighlighter.VARIABLE);
  }

  static void annotateVarReferences(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    String argtext = element.getText();
    int pos = element.getTextRange().getStartOffset();

    // Highlight Outer variables.
    for ( TextRange outerVarRange: getPIOuterVarRefs(PLATFORM.IDEA, element)) {
      holder.createInfoAnnotation( outerVarRange.shiftRight(pos), null)
              .setTextAttributes(DefaultLanguageHighlighterColors.INSTANCE_FIELD);
//              .setTextAttributes(CMakeSyntaxHighlighter.VARIABLE);
    }

    // Highlight Inner variables.
    boolean isCmakePredefinedVar = false;
    for ( TextRange innerVarRange: getPIInnerVars(PLATFORM.IDEA, element) ) {
      String innerVarName = argtext.substring(innerVarRange.getStartOffset(),innerVarRange.getEndOffset());

      // Highlight Inner CMake predefined variables
      for (String varRegexp: CMakeKeywords.variables_All ) {
        if ( innerVarName.matches(varRegexp)) {
          holder.createInfoAnnotation(innerVarRange.shiftRight(pos), null)
                  .setTextAttributes(DefaultLanguageHighlighterColors.CONSTANT);
//                  .setTextAttributes(CMakeSyntaxHighlighter.CMAKE_VARIABLE);
          isCmakePredefinedVar = true;
          break;
        }
      }

      if (!isCmakePredefinedVar && CMakePSITreeSearch.findVariableDefinitions(element, innerVarName).isEmpty()) {
// TODO Move it to Inspections? Also too many false negative.
        holder.createWeakWarningAnnotation(innerVarRange.shiftRight(pos),"Possibly not defined Variable: "+ innerVarName)
                .setHighlightType(ProblemHighlightType.WEAK_WARNING);
      }
    }

    // Highlight ENV variables
    //fixme implement ref/resolve for ENV
    for ( TextRange innerVarRange: CMakeVarStringUtil.getInnerEnvVars(argtext) ) {
      for (String varRegexp: CMakeKeywords.variables_ENV ) {
        if (argtext.substring(innerVarRange.getStartOffset(),innerVarRange.getEndOffset())
                .matches(varRegexp)) {
          holder.createInfoAnnotation(innerVarRange.shiftRight(pos), null)
                  .setTextAttributes(DefaultLanguageHighlighterColors.CONSTANT);
//                  .setTextAttributes(CMakeSyntaxHighlighter.CMAKE_VARIABLE);
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
            .setTextAttributes(DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);
//              .setTextAttributes(CMakeSyntaxHighlighter.CMAKE_COMMAND);
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
                .setTextAttributes(DefaultLanguageHighlighterColors.NUMBER);
//                .setTextAttributes(CMakeSyntaxHighlighter.CMAKE_PROPERTY);
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
              .setTextAttributes(DefaultLanguageHighlighterColors.METADATA);
//              .setTextAttributes(CMakeSyntaxHighlighter.CMAKE_OPERATOR);
      return true;
    }
    for (String boolValue: CMakeKeywords.boolValues){
      if (operatorName.toUpperCase().matches(boolValue)){
        holder.createInfoAnnotation(element, null)
                .setTextAttributes(DefaultLanguageHighlighterColors.NUMBER);
//                .setTextAttributes(CMakeSyntaxHighlighter.CMAKE_PROPERTY);
        return true;
      }
    }
    return false;
  }

}
