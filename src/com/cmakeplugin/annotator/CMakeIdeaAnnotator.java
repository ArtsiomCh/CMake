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
      assert element.getFirstChild() instanceof CMakeBrace && element.getLastChild() instanceof CMakeBrace;
      PsiElement quotedArgument = element.getFirstChild().getNextSibling();
      if (quotedArgument.getNode().getElementType().getClass().isInstance(CMakeTypes.QUOTED_ARGUMENT))
        annotateVarReferences(quotedArgument, holder);
    } else if (element instanceof CMakeUnquotedArgumentContainer) {
      // Annotate Unquoted argument
      PsiElement unquotedArgument = element.getFirstChild();
      if (unquotedArgument!=null && unquotedArgument.getNode().getElementType().getClass().isInstance(CMakeTypes.UNQUOTED_ARGUMENT)) {
        if ( !(  annotateLegacy(unquotedArgument, holder)
                || annotateProperty(unquotedArgument, holder)
                || annotateVariable(unquotedArgument, holder)
                || annotateOperator(unquotedArgument, holder) )) {
          annotateVarReferences(unquotedArgument, holder);
          annotatePathURL(unquotedArgument, holder);
        }
      }
    }
  }
}
