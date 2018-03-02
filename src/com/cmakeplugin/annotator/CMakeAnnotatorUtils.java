package com.cmakeplugin.annotator;

//import com.cmakeplugin.CMakeSyntaxHighlighter;

import static com.cmakeplugin.utils.CMakePSITreeSearch.*;
import com.cmakeplugin.utils.CMakeVariablesUtil;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

//import static CMakeKeywords.*;

public class CMakeAnnotatorUtils {

  protected static boolean annotateLegacy(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    if (element.getText().matches("(.*[^\\\\]\"([^\"]*[^\\\\])?\".*)+|(.*\\$\\(.*\\).*)")) { //fixme
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

    // Highlight Outer variables.
    for ( TextRange outerVarRange: CMakeVariablesUtil.getOuterVarRefs(argtext)) {
      holder.createInfoAnnotation( outerVarRange.shiftRight(pos), null)
              .setTextAttributes(DefaultLanguageHighlighterColors.INSTANCE_FIELD);
//              .setTextAttributes(CMakeSyntaxHighlighter.VARIABLE);
    }
    // Highlight Inner variables.
    outerloop:
    for ( TextRange innerVarRange: CMakeVariablesUtil.getInnerVars(argtext) ) {
      String innerVarName = argtext.substring(innerVarRange.getStartOffset(),innerVarRange.getEndOffset());

      // Highlight Inner CMake predefined variables
      for (String varRegexp: CMakeKeywords.variables_All ) {
        if ( innerVarName.matches(varRegexp)) {
          holder.createInfoAnnotation(innerVarRange.shiftRight(pos), null)
                  .setTextAttributes(DefaultLanguageHighlighterColors.CONSTANT);
//                  .setTextAttributes(CMakeSyntaxHighlighter.CMAKE_VARIABLE);
          continue outerloop;
        }
      }

      // Highlight Inner variable definition.
      List<PsiElement> elementVarDefinitions = findVariableDefinitions(element, innerVarName);
      if (!(elementVarDefinitions.isEmpty())) {
        for (PsiElement elementVarDefinition : elementVarDefinitions) {
          if (element.getContainingFile() == elementVarDefinition.getContainingFile()) {
            holder.createInfoAnnotation(elementVarDefinition, null)
                    .setTextAttributes(DefaultLanguageHighlighterColors.INSTANCE_FIELD);
            //                  .setTextAttributes(CMakeSyntaxHighlighter.VARIABLE);
          }
        }
      } else {
// TODO Move it to Inspections? Too many false negative.
          holder.createWeakWarningAnnotation(innerVarRange.shiftRight(pos),"Possibly not defined Variable: "+ innerVarName)
                  .setHighlightType(ProblemHighlightType.WEAK_WARNING);
      }
    }

    // Highlight ENV variables
    for ( TextRange innerVarRange: CMakeVariablesUtil.getInnerEnvVars(argtext) ) {
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
