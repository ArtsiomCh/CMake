package com.cmakeplugin.utils;

import com.cmakeplugin.CMakeLexerAdapter;
import com.cmakeplugin.psi.impl.CMakeFunDefImpl;
import com.cmakeplugin.psi.impl.CMakeMacroDefImpl;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
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

  public static final Class<? extends PsiElement> ARGUMENTS_CLASS =
      (isCLION) ? com.jetbrains.cmake.psi.CMakeCommandArguments.class : CMakeArguments.class;

  public static final Class<? extends PsiElement> COMMAND_NAME_CLASS =
      (isCLION) ? com.jetbrains.cmake.psi.CMakeCommandName.class : CMakeCommandName.class;

  @SuppressWarnings("unchecked")
  public static final Class<? extends PsiElement>[] ARGUMENT_CLASSES =
      (isCLION)
          ? new Class[] {com.jetbrains.cmake.psi.CMakeArgument.class}
          : new Class[] {
            CMakeUnquotedArgumentContainer.class, CMakeUnquotedArgumentMaybeVariableContainer.class
          };

  static boolean isClassOfVarRefInsideIfWhile(PsiElement element) {
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

  static boolean classCanHoldVarRef(PsiElement element) {
    return (isCLION)
        ? getCMakeLiteralClass().isInstance(element)
        : element instanceof CMakeUnquotedArgumentContainer
            || element instanceof CMakeQuotedArgumentContainer;
  }

  public static Lexer getCMakeLexer() {
    return (isCLION) ? /*new EmptyLexer()*/ getJBCMakeLexer() : new CMakeLexerAdapter();
  }

  static boolean checkSetCommandSemantic(PsiElement possibleVarDef) {
    if (!PsiTreeUtil.instanceOf(possibleVarDef, ARGUMENT_CLASSES))
      possibleVarDef = PsiTreeUtil.getParentOfType(possibleVarDef, ARGUMENT_CLASSES);
    PsiElement commandArguments = PsiTreeUtil.getParentOfType(possibleVarDef, ARGUMENTS_CLASS);
    PsiElement commandName = PsiTreeUtil.getPrevSiblingOfType(commandArguments, COMMAND_NAME_CLASS);
    if (commandName != null && commandName.textMatches("set")) {
      final PsiElement firstArgument =
          PsiTreeUtil.getChildOfAnyType(commandArguments, ARGUMENT_CLASSES);
      return possibleVarDef == firstArgument;
    }
    return true;
  }
}
