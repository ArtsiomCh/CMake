package com.cmakeplugin.utils;

import com.cmakeplugin.CMakeLexerAdapter;
import com.intellij.lexer.EmptyLexer;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.cmakeplugin.psi.*;
import com.cmakeplugin.CMakeFileType;

import static com.cmakeplugin.utils.CMakeProxyToJB.*;

/**
 * Provide Platform Dependent Code (IDEA/CLion) encapsulation into API
 */
public class CMakePDC {
  public static final boolean isCLION = hasOldCmake || hasNewCmake;

  static boolean isClassOfVarRefInsideIfWhile(PsiElement element) {
    return (isCLION)
            ? getCMakeLiteralClass().isInstance(element) && !hasQuotedArg(element.getParent())
            : (element instanceof CMakeUnquotedArgumentContainer);
  }

  static boolean isClassOfVarDef(PsiElement element) {
    return (isCLION)
        ? getCMakeLiteralClass().isInstance(element) && !hasQuotedArg(element.getParent())
        : (element instanceof CMakeUnquotedArgumentMaybeVariableContainer);
  }

  static boolean hasIfWhileParent(PsiElement element) {
    if (isCLION) {
      PsiElement commandArguments = PsiTreeUtil.getParentOfType( element, getCMakeCommandArgumentsClass());
      assert (commandArguments != null && commandArguments.getPrevSibling() != null);
      PsiElement prevSibling = commandArguments.getPrevSibling();
      if (prevSibling instanceof PsiWhiteSpace && prevSibling.getPrevSibling() != null) { // workaround for "if (...)"
        prevSibling = prevSibling.getPrevSibling();
      }
      return PsiTreeUtil.instanceOf( prevSibling ,
              getCMakeIfCommandCallClass(), getCMakeElseIfCommandCallClass(), getCMakeElseCommandCallClass(),
              getCMakeEndIfCommandCallClass(), getCMakeWhileCommandCallClass(), getCMakeEndWhileCommandCallClass());
    } else {
      return PsiTreeUtil.getParentOfType( element
              ,CMakeIfExpr.class, CMakeElseifExpr.class, CMakeElseExpr.class, CMakeEndifExpr.class
              ,CMakeWhilebegin.class, CMakeWhileend.class)!=null;
    }
  }

  static FileType getCmakeFileType() {
    return (isCLION)
            ? getCMakeListsFileTypeINSTANCE()
            : CMakeFileType.INSTANCE;
  }

  public static Class<? extends PsiElement> getPossibleVarDefClass(){
    return (isCLION)
            ? getCMakeLiteralClass()
            : CMakeUnquotedArgumentMaybeVariableContainer.class;
  }

  static Class<? extends PsiElement> getUnquotedArgumentClass(){
    return (isCLION)
            ? getCMakeLiteralClass()
            : CMakeUnquotedArgumentContainer.class;
  }

  static Class<? extends PsiElement> getQuotedArgumentClass(){
    return (isCLION)
            ? getCMakeLiteralClass()
            : CMakeQuotedArgumentContainer.class;
  }

  public static Class<? extends PsiElement> getArgumentsClass(){
    return (isCLION)
            ? getCMakeArgumentsClass()
            : CMakeArguments.class;
  }

  public static Lexer getCMakeLexer() {
    return (isCLION)
            ? new EmptyLexer()//getJBCMakeLexer()
            : new CMakeLexerAdapter();
  }

}
