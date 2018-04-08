package com.cmakeplugin.utils;

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
  public static boolean isIDEA = PlatformUtils.isIntelliJ();
  public static boolean isCLION = PlatformUtils.isCLion();

  static boolean isUnquotedArgument(PsiElement element) {
    if (isIDEA) return (element instanceof CMakeUnquotedArgumentContainer);
    if (isCLION) return (element instanceof CMakeLiteral);
    return false;
  }

  static boolean hasIfWhileParent(PsiElement element) {
    if (isIDEA) {
      return PsiTreeUtil.getParentOfType(element
              ,CMakeIfExpr.class, CMakeElseifExpr.class, CMakeElseExpr.class, CMakeEndifExpr.class
              ,CMakeWhilebegin.class, CMakeWhileend.class)!=null;
    } else if (isCLION) {
      PsiElement commandArguments = PsiTreeUtil.getParentOfType(element, CMakeCommandArguments.class);
      if (commandArguments != null && commandArguments.getPrevSibling() != null) {
        PsiElement prevSibling = commandArguments.getPrevSibling();
        return     prevSibling instanceof CMakeIfCommandCall
                || prevSibling instanceof CMakeElseIfCommandCall
                || prevSibling instanceof CMakeElseCommandCall
                || prevSibling instanceof CMakeEndIfCommandCall
                || prevSibling instanceof CMakeWhileCommandCall
                || prevSibling instanceof CMakeEndWhileCommandCall;

      }
    }
    return false;
  }

  static FileType getCmakeFileType() {
    if (isIDEA) {
      return CMakeFileType.INSTANCE;
    } else if (isCLION) {
      return CMakeListsFileType.INSTANCE;
    }
    return null;
  }

  static Class<? extends PsiElement> getPossibleVarDefClass(){
    if (isIDEA) {
      return CMakeUnquotedArgumentMaybeVariableContainer.class;
    } else if (isCLION) {
      return CMakeLiteral.class;
    }
    return null;
  }

  static Class<? extends PsiElement> getUnquotedArgumentClass(){
    if (isIDEA) {
      return CMakeUnquotedArgumentContainer.class;
    } else if (isCLION) {
      return CMakeLiteral.class;
    }
    return null;
  }

  static Class<? extends PsiElement> getQuotedArgumentClass(){
    if (isIDEA) {
      return CMakeQuotedArgumentContainer.class;
    } else if (isCLION) {
      return CMakeLiteral.class;
    }
    return null;
  }
}
