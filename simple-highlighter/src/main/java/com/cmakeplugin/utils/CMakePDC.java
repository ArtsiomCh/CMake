package com.cmakeplugin.utils;

import com.cmakeplugin.CMakeLexerAdapter;
import com.cmakeplugin.psi.impl.CMakeFbeginImpl;
import com.cmakeplugin.psi.impl.CMakeMbeginImpl;
import com.intellij.lexer.EmptyLexer;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.cmakeplugin.psi.*;
import com.cmakeplugin.CMakeFileType;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

import static com.cmakeplugin.utils.CMakeProxyToJB.*;

/** Provide Platform Dependent Code (IDEA/CLion) encapsulation into API */
public class CMakePDC {
  public static final boolean isCLION = hasOldCmake || hasNewCmake;

  public static final Class<? extends NavigatablePsiElement> MACRO_CLASS =
      (isCLION) ? com.jetbrains.cmake.psi.CMakeMacroCommandImpl.class : CMakeMbeginImpl.class;

  public static final Class<? extends NavigatablePsiElement> FUNCTION_CLASS =
      (isCLION) ? com.jetbrains.cmake.psi.CMakeFunctionCommandImpl.class : CMakeFbeginImpl.class;

  public static final Class<? extends PsiElement> ARGUMENTS_CLASS =
      (isCLION) ? com.jetbrains.cmake.psi.CMakeCommandArguments.class : CMakeArguments.class;

  @SuppressWarnings("unchecked")
  public static final Class<? extends PsiElement>[] ARGUMENT_CLASS =
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

  static boolean hasIfWhileParent(PsiElement element) {
    if (isCLION) {
      PsiElement commandName = getCommandNameElement(element);
      return PsiTreeUtil.instanceOf(
          commandName,
          getCMakeIfCommandCallClass(),
          getCMakeElseIfCommandCallClass(),
          getCMakeElseCommandCallClass(),
          getCMakeEndIfCommandCallClass(),
          getCMakeWhileCommandCallClass(),
          getCMakeEndWhileCommandCallClass());
    } else {
      return PsiTreeUtil.getParentOfType(
              element,
              CMakeIfExpr.class,
              CMakeElseifExpr.class,
              CMakeElseExpr.class,
              CMakeEndifExpr.class,
              CMakeWhilebegin.class,
              CMakeWhileend.class)
          != null;
    }
  }

  @NotNull
  private static PsiElement getCommandNameElement(PsiElement element) {
    PsiElement commandArguments = PsiTreeUtil.getParentOfType(element, getArgumentsClass());
    assert (commandArguments != null && commandArguments.getPrevSibling() != null);
    PsiElement prevSibling = commandArguments.getPrevSibling();
    if (prevSibling instanceof PsiWhiteSpace
        && prevSibling.getPrevSibling() != null) { // workaround for "if (...)"
      prevSibling = prevSibling.getPrevSibling();
    }
    return prevSibling;
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

  public static Class<? extends PsiElement> getArgumentsClass() {
    return (isCLION) ? getCMakeCommandArgumentsClass() : CMakeArguments.class;
  }

  public static Lexer getCMakeLexer() {
    return (isCLION)
        ? /*new EmptyLexer()*/ getJBCMakeLexer()
        : new CMakeLexerAdapter();
  }

  static boolean checkSetCommandSemantic(PsiElement element) {
    PsiElement commandName = getCommandNameElement(element);
    //      assert getCMakeCommandNameClass().isInstance(commandName);
    if (commandName.textMatches("set")) {
      PsiElement prevArgument =
          isCLION ? element.getParent().getPrevSibling() : element.getPrevSibling();
      while (prevArgument != null) {
        if (isClassOfVarDef(isCLION ? prevArgument.getFirstChild() : prevArgument)) return false;
        prevArgument = prevArgument.getPrevSibling();
      }
    }
    return true;
  }
}
