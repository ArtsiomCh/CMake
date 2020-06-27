package com.cmakeplugin.annotator;

import com.cmakeplugin.psi.*;
import com.cmakeplugin.utils.CMakePDC;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import static com.cmakeplugin.annotator.CMakeAnnotatorUtils.*;
import static com.cmakeplugin.utils.CMakePDC.*;
import static com.cmakeplugin.utils.CMakeProxyToJB.*;
import static com.cmakeplugin.utils.CMakeProxyToJB.hasQuotedArg;

public class CMakeAnnotator implements Annotator {

  @Override
  public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
    if (CMakePDC.isCLION) {
      annotateCLion(element, holder);
    } else {
      annotateIdea(element, holder);
    }
  }

  private void annotateIdea(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
    if (element instanceof CMakeCommandName) {
      annotateCommand(element, holder);
    } else if (CMakePDC.FUNCTION_CLASS.isInstance(element)) {
      annotateFunctionName(element, holder);
    } else if (CMakePDC.MACRO_CLASS.isInstance(element)) {
      annotateMacrosName(element, holder);
    } else if (element instanceof CMakeQuotedArgumentContainer) {
      // Annotate Quoted argument
      assert element.getPrevSibling() instanceof CMakeBrace
          && element.getNextSibling() instanceof CMakeBrace;
      annotateVarReferences(element, holder);
    } else if (element instanceof CMakeUnquotedArgumentMaybeVariableContainer) {
      // Annotate Unquoted argument with possible Var declaration
      if (!(annotatePredefinedVariable(element, holder)
          || annotateProperty(element, holder)
          || annotateOperator(element, holder)
          || annotateVarDeclaration(element, holder))) {
        annotatePathURL(element, holder);
      }
    } else if (element instanceof CMakeUnquotedArgumentContainer) {
      // Annotate Unquoted argument
      if (!(annotateLegacy(element, holder)
          || annotatePredefinedVariable(element, holder)
          || annotateProperty(element, holder)
          || annotateOperator(element, holder))) {
        annotateVarReferences(element, holder);
        annotatePathURL(element, holder);
      }
    }
  }

  private void annotateCLion(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
    if (COMMAND_NAME_CLASS.isInstance(element)) {
      annotateCommand(element, holder);
    } else if (FUNCTION_CLASS.isInstance(element)) {
      annotateFunctionName(element, holder);
    } else if (MACRO_CLASS.isInstance(element)) {
      annotateMacrosName(element, holder);
    } else if (getCMakeArgumentClass().isInstance(element)) {
      if (!hasBracketArg(element)) {
        PsiElement cmakeLiteral = getCMakeLiteral(element);
        assert cmakeLiteral != null;
        if (hasQuotedArg(element)) {
          // Annotate Quoted argument
          annotateVarReferences(cmakeLiteral, holder);
        } else
          // Annotate Unquoted argument
          if (!(annotateLegacy(cmakeLiteral, holder)
                  || annotatePredefinedVariable(cmakeLiteral, holder)
                  || annotateProperty(cmakeLiteral, holder)
                  || annotateOperator(cmakeLiteral, holder)
                  || annotateVarDeclaration(cmakeLiteral, holder))) {
            annotateVarReferences(cmakeLiteral, holder);
            if (!(annotatePathURL(cmakeLiteral, holder))) {
              //              holder.createInfoAnnotation(cmakeLiteral, null)
              //                      .setTextAttributes(DefaultLanguageHighlighterColors.IDENTIFIER);
            }
          }
      }
    }
  }

}
