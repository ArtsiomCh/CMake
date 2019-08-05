package com.cmakeplugin.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import com.cmakeplugin.psi.*;

import static com.cmakeplugin.annotator.CMakeAnnotatorUtils.*;

public class CMakeIdeaAnnotator implements Annotator {

  @Override
  public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
    if (element instanceof CMakeCommandName) {
      annotateCommand(element, holder);
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
}
