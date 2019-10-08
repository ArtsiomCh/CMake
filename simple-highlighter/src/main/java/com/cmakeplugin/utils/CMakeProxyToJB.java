package com.cmakeplugin.utils;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;

/**
 * Provide "Proxy" to either of:
 * new JB CMake implementation - com.jetbrains.cidr.cpp.cmake
 * old JB CMake implementation - com.jetbrains.cmake
 * Depending of availability at the current runtime IntelliJ platform.
 */
public class CMakeProxyToJB {
  static final boolean hasOldCmake = false;// isClass("com.jetbrains.cidr.cpp.cmake.");
  static final boolean hasNewCmake = isClass("com.jetbrains.cmake.");

  private static boolean isClass(String cmakePath) {
    try  {
      Class.forName(cmakePath + "CMakeLanguage");
      return true;
    }  catch (ClassNotFoundException e) {
      return false;
    }
  }

  static FileType getCMakeListsFileTypeINSTANCE() {
//    if (hasOldCmake) return com.jetbrains.cidr.cpp.cmake.CMakeListsFileType.INSTANCE;
    if (hasNewCmake) return com.jetbrains.cmake.CMakeListsFileType.INSTANCE;
    throw new java.lang.RuntimeException("Unknown CMake classes");
  }

  public static Class<? extends PsiElement> getCMakeLiteralClass() {
//    if (hasOldCmake) return com.jetbrains.cidr.cpp.cmake.psi.CMakeLiteral.class;
    if (hasNewCmake) return com.jetbrains.cmake.psi.CMakeLiteral.class;
    throw new java.lang.RuntimeException("Unknown CMake classes");
  }

  public static Class<? extends PsiElement> getCMakeArgumentClass() {
//    if (hasOldCmake) return com.jetbrains.cidr.cpp.cmake.psi.CMakeArgument.class;
    if (hasNewCmake) return com.jetbrains.cmake.psi.CMakeArgument.class;
    throw new java.lang.RuntimeException("Unknown CMake classes");
  }

  public static boolean hasBracketArg(PsiElement cmakeArgument) {
//    if (hasOldCmake) return (((com.jetbrains.cidr.cpp.cmake.psi.CMakeArgument) cmakeArgument).getBracketArgStart()!=null);
    if (hasNewCmake) return (((com.jetbrains.cmake.psi.CMakeArgument) cmakeArgument).getBracketArgStart()!=null);
    throw new java.lang.RuntimeException("Unknown CMake classes");
  }

  public static PsiElement getCMakeLiteral(PsiElement cmakeArgument) {
//    if (hasOldCmake) return (((com.jetbrains.cidr.cpp.cmake.psi.CMakeArgument) cmakeArgument).getCMakeLiteral());
    if (hasNewCmake) return (((com.jetbrains.cmake.psi.CMakeArgument) cmakeArgument).getCMakeLiteral());
    throw new java.lang.RuntimeException("Unknown CMake classes");
  }

  public static boolean hasQuotedArg(PsiElement cmakeArgument) {
//    if (hasOldCmake) return (cmakeArgument.getFirstChild().getNode().getElementType()
//            == com.jetbrains.cidr.cpp.cmake.psi.CMakeTokenTypes.QUOTE);
    if (hasNewCmake) return (cmakeArgument.getFirstChild().getNode().getElementType()
            == com.jetbrains.cmake.psi.CMakeTokenTypes.QUOTE);
    throw new java.lang.RuntimeException("Unknown CMake classes");
  }

  static Lexer getJBCMakeLexer(){
    if (hasNewCmake) {
      try {
        return (Lexer) Class.forName("com.jetbrains.cmake.psi.CMakeLexer").getDeclaredConstructor().newInstance();
      } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
        e.printStackTrace();
      }
    }
    throw new java.lang.RuntimeException("Unknown CMake classes");
  }

}
