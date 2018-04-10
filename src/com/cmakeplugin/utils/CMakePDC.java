package com.cmakeplugin.utils;

import com.cmakeplugin.CMakeLexerAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.cidr.cpp.cmake.CMakeListsFileType;
import com.jetbrains.cidr.cpp.cmake.psi.*;
import com.cmakeplugin.psi.*;
import com.cmakeplugin.CMakeFileType;
import com.intellij.util.PlatformUtils;

/**
 * Provide Platform Dependent Code (IDEA/CLion) encapsulation into API
 */
public class CMakePDC {
  public static boolean isCLION = PlatformUtils.isCLion();
//  public static boolean isIDEA = !isCLION;//PlatformUtils.isIntelliJ();

  static boolean isUnquotedArgument(PsiElement element) {
    return (isCLION)
            ? (element instanceof CMakeLiteral)
            : (element instanceof CMakeUnquotedArgumentContainer);
  }

  static boolean hasIfWhileParent(PsiElement element) {
    if (isCLION) {
      PsiElement commandArguments = PsiTreeUtil.getParentOfType(element, CMakeCommandArguments.class);
      assert (commandArguments != null && commandArguments.getPrevSibling() != null);
      PsiElement prevSibling = commandArguments.getPrevSibling();
      return     prevSibling instanceof CMakeIfCommandCall
              || prevSibling instanceof CMakeElseIfCommandCall
              || prevSibling instanceof CMakeElseCommandCall
              || prevSibling instanceof CMakeEndIfCommandCall
              || prevSibling instanceof CMakeWhileCommandCall
              || prevSibling instanceof CMakeEndWhileCommandCall;
    } else {
      return PsiTreeUtil.getParentOfType(element
              ,CMakeIfExpr.class, CMakeElseifExpr.class, CMakeElseExpr.class, CMakeEndifExpr.class
              ,CMakeWhilebegin.class, CMakeWhileend.class)!=null;
    }
  }

  static FileType getCmakeFileType() {
    return (isCLION)
            ? CMakeListsFileType.INSTANCE
            : CMakeFileType.INSTANCE;
  }

  static Class<? extends PsiElement> getPossibleVarDefClass(){
    return (isCLION)
            ? CMakeLiteral.class
            : CMakeUnquotedArgumentMaybeVariableContainer.class;
  }

  static Class<? extends PsiElement> getUnquotedArgumentClass(){
    return (isCLION)
            ? CMakeLiteral.class
            : CMakeUnquotedArgumentContainer.class;
  }

  static Class<? extends PsiElement> getQuotedArgumentClass(){
    return (isCLION)
            ? CMakeLiteral.class
            : CMakeQuotedArgumentContainer.class;
  }

//  public static Lexer getHighlightingLexer() {
//    return (isCLION)
//            ? new CMakeLexer()
//            : new CMakeLexerAdapter();
//  }

}
