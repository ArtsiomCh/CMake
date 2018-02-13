package com.cmakeplugin.annotator;

//import com.cmakeplugin.CMakeSyntaxHighlighter;

import com.cmakeplugin.utils.CMakeStringUtils;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import static CMakeKeywords.*;

public class CMakeAnnotatorUtils {

  protected static boolean annotateLegacy(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    if (element.getText().matches("(.*[^\\\\]\"(.*[^\\\\])?\".*)|(.*\\$\\(.*\\).*)")) {
      holder.createInfoAnnotation(element, null)
              .setTextAttributes(DefaultLanguageHighlighterColors.DOC_COMMENT_TAG);
      return true;
    } else return false;
  }

  protected static boolean annotatePathURL(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
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

  protected static boolean annotateVariable(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
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

  protected static void annotateVarReferences(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    String argtext = element.getText();
    int pos = element.getTextRange().getStartOffset();

    for ( TextRange outerVarRange: CMakeStringUtils.getOuterVarRefs(argtext)) {
      holder.createInfoAnnotation( outerVarRange.shiftRight(pos), null)
              .setTextAttributes(DefaultLanguageHighlighterColors.INSTANCE_FIELD);
//              .setTextAttributes(CMakeSyntaxHighlighter.VARIABLE);
    }
    for ( TextRange innerVarRange: CMakeStringUtils.getInnerVars(argtext) ) {
      String innerVar = argtext.substring(innerVarRange.getStartOffset(),innerVarRange.getEndOffset());

//      markVarInit(innerVar, element, holder);

      for (String varRegexp: CMakeKeywords.variables_All ) {
        if ( innerVar.matches(varRegexp)) {
          holder.createInfoAnnotation(innerVarRange.shiftRight(pos), null)
                  .setTextAttributes(DefaultLanguageHighlighterColors.CONSTANT);
//                  .setTextAttributes(CMakeSyntaxHighlighter.CMAKE_VARIABLE);
          break;
        }
      }
    }
    for ( TextRange innerVarRange: CMakeStringUtils.getInnerEnvVars(argtext) ) {
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

//  private void markVarInit(String varName, @NotNull PsiElement elementVarHolder, @NotNull AnnotationHolder holder) {
//    for (String varName : allVarsList) {
//      if (element.getText().equals(varName)) {
//        holder.createInfoAnnotation(element, null)
//                .setTextAttributes(DefaultLanguageHighlighterColors.INSTANCE_FIELD);
////                  .setTextAttributes(CMakeSyntaxHighlighter.VARIABLE);
//        return true;
//      }
//    }
//  }

  protected static void annotateCommand(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
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

  protected static boolean annotateProperty(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    String propertyName = element.getText().toUpperCase();
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

  protected static boolean annotateOperator(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    String operatorName = element.getText().toUpperCase();
    if (CMakeKeywords.operators.contains(operatorName) ) {
      holder.createInfoAnnotation(element, null)
              .setTextAttributes(DefaultLanguageHighlighterColors.METADATA);
//              .setTextAttributes(CMakeSyntaxHighlighter.CMAKE_OPERATOR);
      return true;
    } else return false;
  }

}
