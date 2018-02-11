package com.cmakeplugin.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import com.jetbrains.cidr.cpp.cmake.psi.*;

import static com.cmakeplugin.annotator.CMakeAnnotatorUtils.*;

public class CMakeCLionAnnotator implements Annotator {

  @Override
  public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
    if (element instanceof CMakeCommandName) {
      annotateCommand(element, holder);
    } else if (element instanceof CMakeArgument) {
      if (((CMakeArgument) element).getBracketArgStart()==null) {
        PsiElement cmakeLiteral = ((CMakeArgument) element).getCMakeLiteral();
        assert cmakeLiteral!=null;
        if (element.getFirstChild().getNode().getElementType() == CMakeTokenTypes.QUOTE) {
          // Annotate Quoted argument
          annotateVarReferences(cmakeLiteral, holder);
        } else
          // Annotate Unquoted argument
          if ( !(  annotateLegacy(cmakeLiteral, holder)
                  || annotateProperty(cmakeLiteral, holder)
                  || annotateVariable(cmakeLiteral, holder)
                  || annotateOperator(cmakeLiteral, holder) )) {
            annotateVarReferences(cmakeLiteral, holder);
            if (!(annotatePathURL(cmakeLiteral, holder))) {
              holder.createInfoAnnotation(cmakeLiteral, null)
                      .setTextAttributes(DefaultLanguageHighlighterColors.IDENTIFIER);
            }
          }
      }
    }
  }
}
