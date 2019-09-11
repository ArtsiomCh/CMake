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

  public static final Class<? extends NavigatablePsiElement> MACRO_CLASS =
      (isCLION) ? com.jetbrains.cmake.psi.CMakeMacroCommandImpl.class : CMakeMbeginImpl.class;

  public static final Class<? extends NavigatablePsiElement> FUNCTION_CLASS =
      (isCLION) ? com.jetbrains.cmake.psi.CMakeFunctionCommandImpl.class : CMakeFbeginImpl.class;

  public static final Class<? extends NavigatablePsiElement> VARDEF_CLASS =
      (isCLION)
          ? com.jetbrains.cmake.psi.CMakeLiteralImpl.class
          : CMakeUnquotedArgumentMaybeVariableContainerImpl.class;

  public static String getFunMacroName(NavigatablePsiElement element) {
    PsiElement name = getFunMacroNameElement(element);
    return name != null ? name.getText() : element.getText();
  }

  public static NavigatablePsiElement getFunMacroNameElement(NavigatablePsiElement element) {
    PsiElement arguments = PsiTreeUtil.getChildOfType(element, ARGUMENTS_CLASS);
    PsiElement name = PsiTreeUtil.findChildOfAnyType(arguments, ARGUMENT_CLASS);
    return (name instanceof NavigatablePsiElement) ? (NavigatablePsiElement) name : null;
  }

  public static String getFunMacroArgs(NavigatablePsiElement element) {
    PsiElement arguments = PsiTreeUtil.getChildOfType(element, ARGUMENTS_CLASS);
    return PsiTreeUtil.findChildrenOfAnyType(arguments, ARGUMENT_CLASS).stream()
        .skip(1) // fun/macro name
        .map(PsiElement::getText)
        .collect(Collectors.joining(" "));
  }

  private static final Class<? extends PsiElement> ARGUMENTS_CLASS =
      (isCLION) ? com.jetbrains.cmake.psi.CMakeCommandArguments.class : CMakeArguments.class;

  @SuppressWarnings("unchecked")
  private static final Class<? extends PsiElement>[] ARGUMENT_CLASS =
      (isCLION)
          ? new Class[] {com.jetbrains.cmake.psi.CMakeArgument.class}
          : new Class[] {
            CMakeUnquotedArgumentContainer.class, CMakeUnquotedArgumentMaybeVariableContainer.class
          };
}
