package com.cmakeplugin.utils;

import com.cmakeplugin.CMakeLexerAdapter;
import com.cmakeplugin.psi.impl.CMakeFunDefImpl;
import com.cmakeplugin.psi.impl.CMakeMacroDefImpl;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.cmakeplugin.psi.*;
import com.cmakeplugin.CMakeFileType;

import static com.cmakeplugin.utils.CMakeProxyToJB.*;

/** Provide Platform Dependent Code (IDEA/CLion) encapsulation into API */
public class CMakePDC {
  public static final boolean isCLION = hasOldCmake || hasNewCmake;

  public static final Class<? extends NavigatablePsiElement> MACRO_CLASS =
      (isCLION) ? com.jetbrains.cmake.psi.CMakeMacroCommandImpl.class : CMakeMacroDefImpl.class;

  public static final Class<? extends NavigatablePsiElement> FUNCTION_CLASS =
      (isCLION) ? com.jetbrains.cmake.psi.CMakeFunctionCommandImpl.class : CMakeFunDefImpl.class;

  @SuppressWarnings("unchecked")
  public static final Class<? extends PsiElement>[] FUN_MACRO_END_CLASSES =
      (isCLION)
          ? new Class[] {
            com.jetbrains.cmake.psi.CMakeEndFunctionCommand.class,
            com.jetbrains.cmake.psi.CMakeEndMacroCommand.class
          }
          : new Class[] {CMakeFend.class, CMakeMend.class};

  public static final Class<? extends PsiElement> ARGUMENTS_CLASS =
      (isCLION) ? com.jetbrains.cmake.psi.CMakeCommandArguments.class : CMakeArguments.class;

  public static final Class<? extends PsiElement> COMMAND_NAME_CLASS =
      (isCLION) ? com.jetbrains.cmake.psi.CMakeCommandName.class : CMakeCommandName.class;

  public static final Class<? extends PsiElement> COMMAND_CLASS =
      (isCLION) ? com.jetbrains.cmake.psi.CMakeCommand.class : CMakeCmd.class;

  @SuppressWarnings("unchecked")
  public static final Class<? extends PsiElement>[] ARGUMENT_CLASSES =
      (isCLION)
          ? new Class[] {com.jetbrains.cmake.psi.CMakeArgument.class}
          : new Class[] {
            CMakeUnquotedArgumentContainer.class, CMakeUnquotedArgumentMaybeVariableContainer.class
          };

  public static TokenSet getJBKeywords(){
    return (isCLION) ? com.jetbrains.cmake.psi.CMakeElementTypes.KEYWORDS : TokenSet.EMPTY;
  }

  public static IElementType getJBComment() {
    return (isCLION) ? com.jetbrains.cmake.psi.CMakeElementTypes.COMMENT : null;
  }

  public static IElementType getJBLiteral() {
    return (isCLION) ? com.jetbrains.cmake.psi.CMakeElementTypes.LITERAL : null;
  }

  public static boolean isClassOfVarRefInsideIfWhile(PsiElement element) {
    return (isCLION)
        ? getCMakeLiteralClass().isInstance(element) && !hasQuotedArg(element.getParent())
        : (element instanceof CMakeUnquotedArgumentContainer);
  }

  public static boolean isClassOfVarDef(PsiElement element) {
    return (isCLION)
        ? getCMakeLiteralClass().isInstance(element) && !hasQuotedArg(element.getParent())
        : (element instanceof CMakeUnquotedArgumentMaybeVariableContainer);
  }

  @SuppressWarnings("unchecked")
  private static final Class<? extends PsiElement>[] IF_WHILE_CLASSES =
      (isCLION)
          ? new Class[] {
            com.jetbrains.cmake.psi.CMakeIfCommand.class,
            com.jetbrains.cmake.psi.CMakeElseIfCommand.class,
            com.jetbrains.cmake.psi.CMakeElseCommand.class,
            com.jetbrains.cmake.psi.CMakeEndIfCommand.class,
            com.jetbrains.cmake.psi.CMakeWhileCommand.class,
            com.jetbrains.cmake.psi.CMakeEndWhileCommand.class
          }
          : new Class[] {
            CMakeIfExpr.class,
            CMakeElseifExpr.class,
            CMakeElseExpr.class,
            CMakeEndifExpr.class,
            CMakeWhilebegin.class,
            CMakeWhileend.class
          };

  static boolean isIfWhileConditionArgument(PsiElement element) {
    PsiElement condCommandName = PsiTreeUtil.getParentOfType(element, IF_WHILE_CLASSES);
    if (condCommandName == null) return false;
    if (isCLION) {
      PsiElement commandArguments = PsiTreeUtil.getParentOfType(element, ARGUMENTS_CLASS);
      if (commandArguments == null) return false;
      PsiElement condArguments = PsiTreeUtil.getChildOfType(condCommandName, ARGUMENTS_CLASS);
      return commandArguments == condArguments;
    }
    return true;
  }

  static FileType getCmakeFileType() {
    return (isCLION) ? getCMakeListsFileTypeINSTANCE() : CMakeFileType.INSTANCE;
  }

  public static boolean classCanHoldVarRef(PsiElement element) {
    return (isCLION)
        ? getCMakeLiteralClass().isInstance(element)
        : element instanceof CMakeUnquotedArgumentContainer
            || element instanceof CMakeQuotedArgumentContainer;
  }

  public static Lexer getCMakeLexer() {
    return (isCLION) ? /*new EmptyLexer()*/ getJBCMakeLexer() : new CMakeLexerAdapter();
  }
}
