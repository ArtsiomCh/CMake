package com.cmakeplugin.annotator;

import com.cmakeplugin.CMakeKeywords;
import com.cmakeplugin.CMakeSyntaxHighlighter;

import com.cmakeplugin.utils.CMakePSITreeSearch;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import com.cmakeplugin.utils.CMakeIFWHILEcheck;
import com.cmakeplugin.utils.CMakeVarStringUtil;

// import static CMakeKeywords.*;

class CMakeAnnotatorUtils {

  static boolean annotateLegacy(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    if (CMakeVarStringUtil.isCMakeLegacy(element.getText())) {
      return createInfoAnnotation(element, holder, CMakeSyntaxHighlighter.UNQUOTED_LEGACY);
    }
    return false;
  }

  static boolean annotatePathURL(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    if (element.getText().contains("/")) {
      return createInfoAnnotation(element, holder, CMakeSyntaxHighlighter.CMAKE_PATH_URL);
    }
    return false;
  }

  static boolean annotatePredefinedVariable(
      @NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    if (CMakeIFWHILEcheck.couldBeVarDef(element)
        && CMakeVarStringUtil.isPredefinedCMakeVar(element.getText())) {
      return createInfoAnnotation(element, holder, CMakeSyntaxHighlighter.CMAKE_VAR_DEF);
    }
    return false;
  }

  static boolean annotateVarDeclaration(
      @NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    if (CMakePSITreeSearch.existReferenceTo(element)) {
      return createInfoAnnotation(element, holder, CMakeSyntaxHighlighter.VAR_DEF);
    }
    return false;
  }

  static void annotateVarReferences(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    String argtext = element.getText();
    int elementStartInFile = element.getTextRange().getStartOffset();

    // Highlight Outer variables.
    for (TextRange outerVarRange : CMakeIFWHILEcheck.getOuterVarRefs(element)) {
      createInfoAnnotation(
          outerVarRange.shiftRight(elementStartInFile), holder, CMakeSyntaxHighlighter.VAR_REF);
    }

    // Highlight Inner variables.
    for (TextRange innerVarRange : CMakeIFWHILEcheck.getInnerVars(element)) {
      String innerVarName =
          argtext.substring(innerVarRange.getStartOffset(), innerVarRange.getEndOffset());

      // Highlight Inner CMake predefined variables
      if (CMakeVarStringUtil.isPredefinedCMakeVar(innerVarName)) {
        createInfoAnnotation(
            innerVarRange.shiftRight(elementStartInFile),
            holder,
            CMakeSyntaxHighlighter.CMAKE_VAR_REF);
      }
      // Highlight not defined Inner variable.
      else if (!CMakePSITreeSearch.existDefinitionOf(element, innerVarName)
          // exclude unquoted arg inside If/While
          && !element.textMatches(innerVarName)) {
        // TODO Move it to Inspections? Also too many false negative.
        createWeakWarningAnnotation(
            innerVarRange.shiftRight(elementStartInFile),
            holder,
            "Possibly not defined Variable: " + innerVarName);
      }
    }

    // Highlight Inner CMake predefined ENV variables
    // fixme: implement ref/resolve for ENV
    for (TextRange innerVarRange : CMakeVarStringUtil.getInnerEnvVars(argtext)) {
      final String varEnv =
          argtext.substring(innerVarRange.getStartOffset(), innerVarRange.getEndOffset());
      if (CMakeKeywords.isVariableENV(varEnv)) {
        createInfoAnnotation(
            innerVarRange.shiftRight(elementStartInFile),
            holder,
            CMakeSyntaxHighlighter.CMAKE_VAR_REF);
      }
    }
  }

  static void annotateCommand(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    String commandName = element.getText().toLowerCase();
    if (CMakeKeywords.isCommandDeprecated(commandName)) {
      createDeprecatedAnnotation(element, holder, "Deprecated command");
    } else if (CMakeKeywords.isCommand(commandName)) {
      createInfoAnnotation(element, holder, CMakeSyntaxHighlighter.CMAKE_COMMAND);
    } else if (CMakePSITreeSearch.existFunctionDefFor(element)) {
      createInfoAnnotation(element, holder, CMakeSyntaxHighlighter.FUNCTION);
    } else if (CMakePSITreeSearch.existMacroDefFor(element)) {
      createInfoAnnotation(element, holder, CMakeSyntaxHighlighter.MACROS);
    }
  }

  static void annotateFunctionName(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    PsiElement nameElement = CMakePSITreeSearch.getFunMacroNameElement(element);
    if (nameElement != null)
      createInfoAnnotation(nameElement, holder, CMakeSyntaxHighlighter.FUNCTION);
  }

  static void annotateMacrosName(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    PsiElement nameElement = CMakePSITreeSearch.getFunMacroNameElement(element);
    if (nameElement != null)
      createInfoAnnotation(nameElement, holder, CMakeSyntaxHighlighter.MACROS);
  }

  static boolean annotateProperty(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    String propertyName = element.getText();
    if (CMakeVarStringUtil.isCMakePropertyDeprecated(propertyName)) {
      return createDeprecatedAnnotation(element, holder, "Deprecated property");
    }
    if (CMakeVarStringUtil.isCMakeProperty(propertyName)) {
      return createInfoAnnotation(element, holder, CMakeSyntaxHighlighter.CMAKE_PROPERTY);
    }
    return false;
  }

  static boolean annotateOperator(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    String operatorName = element.getText();
    if (CMakeVarStringUtil.isCMakeOperator(operatorName)) {
      return createInfoAnnotation(element, holder, CMakeSyntaxHighlighter.CMAKE_OPERATOR);
    }
    if (CMakeVarStringUtil.isCMakeBoolValue(operatorName)) {
      return createInfoAnnotation(element, holder, CMakeSyntaxHighlighter.CMAKE_BOOLEAN);
    }
    // Modules
    if (CMakeVarStringUtil.isCMakeModule(operatorName)) {
      final PsiElement commandNameElement = CMakePSITreeSearch.getCommandNameElement(element);
      if (commandNameElement != null
          && (commandNameElement.textMatches("include")
          || commandNameElement.textMatches("find_package"))) {
      return createInfoAnnotation(element, holder, CMakeSyntaxHighlighter.CMAKE_MODULE);
      }
    }
    return false;
  }

  private static boolean createWeakWarningAnnotation(
      @NotNull TextRange range, @NotNull AnnotationHolder holder, @NotNull String message) {
    holder
        .createWeakWarningAnnotation(range, message)
        .setHighlightType(ProblemHighlightType.WEAK_WARNING);
    return true;
  }

  private static boolean createDeprecatedAnnotation(
      @NotNull PsiElement element, @NotNull AnnotationHolder holder, @NotNull String message) {
    holder
        .createWarningAnnotation(element, message)
        .setHighlightType(ProblemHighlightType.LIKE_DEPRECATED);
    return true;
  }

  private static boolean createInfoAnnotation(
      @NotNull PsiElement element,
      @NotNull AnnotationHolder holder,
      @NotNull TextAttributesKey textAttKey) {
    return createInfoAnnotation(element.getTextRange(), holder, textAttKey);
  }

  private static boolean createInfoAnnotation(
      @NotNull TextRange range,
      @NotNull AnnotationHolder holder,
      @NotNull TextAttributesKey textAttKey) {
    Annotation annotation = holder.createInfoAnnotation(range, textAttKey.getExternalName());
    annotation.setTooltip(null);
    annotation.setTextAttributes(textAttKey);
    return true;
  }
}
