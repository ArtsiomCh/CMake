package com.cmakeplugin.utils;

import static com.cmakeplugin.utils.CMakePDC.isCLION;

import com.cmakeplugin.psi.*;
import com.cmakeplugin.psi.impl.*;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

/** Provide Platform Dependent Code (IDEA/CLion) encapsulation into API */
public class CMakePlusPDC {

  @NotNull
  @SuppressWarnings("unchecked")
  public static final Class<? extends PsiElement>[] FOLDABLE_BODIES =
      (isCLION)
          ? new Class[] {
            com.jetbrains.cmake.psi.CMakeBodyBlock.class,
            com.jetbrains.cmake.psi.CMakeCommandArguments.class,
            PsiComment.class
          }
          : new Class[] {
            CMakeFunbody.class,
            CMakeMacrobody.class,
            CMakeIfbody.class,
            CMakeForbody.class,
            CMakeWhilebody.class,
            CMakeArguments.class,
            PsiComment.class
          };

  public static boolean isLineComment(PsiComment comment) {
    return (isCLION)
        ? !comment.getText().matches("(#\\[=*\\[)(.|\n|\r)*(]=*])")
        : comment.getNode().getElementType() == CMakeTypes.LINE_COMMENT;
  }

  public static boolean isSubsequentLineCommentsGlueElement(PsiElement element) {
    return (isCLION)
        ? element instanceof PsiWhiteSpace || (isEOL(element) && !isEOL(element.getNextSibling()))
        : element instanceof PsiWhiteSpace
            // accept only one caret-return inside subsequent comments
            && element.getText().split("\n", 3).length == 2;
  }

  private static boolean isEOL(PsiElement element) {
    return element != null
        && element.getNode().getElementType() == com.jetbrains.cmake.psi.CMakeTokenTypes.EOL;
  }

  public static TextRange getBodyBlockRangeToFold(PsiElement element) {
    TextRange range = element.getTextRange();
    if (isCLION
        && !range.isEmpty()
        && isEOL(element.getContainingFile().findElementAt(range.getEndOffset() - 1))) {
      // exclude EOL with '\n' that belongs to BodyBlock
      range = new TextRange(range.getStartOffset(), range.getEndOffset() - 1);
    }
    return range;
  }

  public static final Class<? extends PsiFile> CMAKE_FILE_CLASS =
      (isCLION) ? com.jetbrains.cmake.psi.CMakeFile.class : CMakeFile.class;

  public static final Class<? extends NavigatablePsiElement> VARDEF_CLASS =
      (isCLION)
          ? com.jetbrains.cmake.psi.CMakeLiteralImpl.class
          : CMakeUnquotedArgumentMaybeVariableContainerImpl.class;

}
