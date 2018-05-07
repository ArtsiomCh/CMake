package com.cmakeplugin.utils;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
//import com.jetbrains.cidr.cpp.cmake.CMakeListsFileType;
//import com.jetbrains.cidr.cpp.cmake.psi.*;
import com.cmakeplugin.psi.*;
import com.cmakeplugin.CMakeFileType;

import static com.cmakeplugin.utils.CMakeProxyToJB.*;

/**
 * Provide Platform Dependent Code (IDEA/CLion) encapsulation into API
 */
public class CMakePDC {
  public static final boolean isCLION = hasOldCmake || hasNewCmake;

  static boolean isUnquotedArgument(PsiElement element) {
    return (isCLION)
            ? getCMakeLiteralClass().isInstance(element)
            : (element instanceof CMakeUnquotedArgumentContainer);
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

  static Class<? extends PsiElement> getPossibleVarDefClass(){
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

//  public static Lexer getHighlightingLexer() {
//    return (isCLION)
//            ? new CMakeLexer()
//            : new CMakeLexerAdapter();
//  }

}
