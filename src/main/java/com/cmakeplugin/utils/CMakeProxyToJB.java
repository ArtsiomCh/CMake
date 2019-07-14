package com.cmakeplugin.utils;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiElement;

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

  static Class<? extends PsiElement> getCMakeCommandArgumentsClass() {
//    if (hasOldCmake) return com.jetbrains.cidr.cpp.cmake.psi.CMakeCommandArguments.class;
    if (hasNewCmake) return com.jetbrains.cmake.psi.CMakeCommandArguments.class;
    throw new java.lang.RuntimeException("Unknown CMake classes");
  }

  public static Class<? extends PsiElement> getCMakeLiteralClass() {
//    if (hasOldCmake) return com.jetbrains.cidr.cpp.cmake.psi.CMakeLiteral.class;
    if (hasNewCmake) return com.jetbrains.cmake.psi.CMakeLiteral.class;
    throw new java.lang.RuntimeException("Unknown CMake classes");
  }

  static Class<? extends PsiElement> getCMakeIfCommandCallClass() {
//    if (hasOldCmake) return com.jetbrains.cidr.cpp.cmake.psi.CMakeIfCommandCall.class;
    if (hasNewCmake) return com.jetbrains.cmake.psi.CMakeIfCommandCall.class;
    throw new java.lang.RuntimeException("Unknown CMake classes");
  }

  static Class<? extends PsiElement> getCMakeElseIfCommandCallClass() {
//    if (hasOldCmake) return com.jetbrains.cidr.cpp.cmake.psi.CMakeElseIfCommandCall.class;
    if (hasNewCmake) return com.jetbrains.cmake.psi.CMakeElseIfCommandCall.class;
    throw new java.lang.RuntimeException("Unknown CMake classes");
  }

  static Class<? extends PsiElement> getCMakeElseCommandCallClass() {
//    if (hasOldCmake) return com.jetbrains.cidr.cpp.cmake.psi.CMakeElseCommandCall.class;
    if (hasNewCmake) return com.jetbrains.cmake.psi.CMakeElseCommandCall.class;
    throw new java.lang.RuntimeException("Unknown CMake classes");
  }

  static Class<? extends PsiElement> getCMakeEndIfCommandCallClass() {
//    if (hasOldCmake) return com.jetbrains.cidr.cpp.cmake.psi.CMakeEndIfCommandCall.class;
    if (hasNewCmake) return com.jetbrains.cmake.psi.CMakeEndIfCommandCall.class;
    throw new java.lang.RuntimeException("Unknown CMake classes");
  }

  static Class<? extends PsiElement> getCMakeWhileCommandCallClass() {
//    if (hasOldCmake) return com.jetbrains.cidr.cpp.cmake.psi.CMakeWhileCommandCall.class;
    if (hasNewCmake) return com.jetbrains.cmake.psi.CMakeWhileCommandCall.class;
    throw new java.lang.RuntimeException("Unknown CMake classes");
  }

  static Class<? extends PsiElement> getCMakeEndWhileCommandCallClass() {
//    if (hasOldCmake) return com.jetbrains.cidr.cpp.cmake.psi.CMakeEndWhileCommandCall.class;
    if (hasNewCmake) return com.jetbrains.cmake.psi.CMakeEndWhileCommandCall.class;
    throw new java.lang.RuntimeException("Unknown CMake classes");
  }

  public static Class<? extends PsiElement> getCMakeCommandNameClass() {
//    if (hasOldCmake) return com.jetbrains.cidr.cpp.cmake.psi.CMakeCommandName.class;
    if (hasNewCmake) return com.jetbrains.cmake.psi.CMakeCommandName.class;
    throw new java.lang.RuntimeException("Unknown CMake classes");
  }

  public static Class<? extends PsiElement> getCMakeArgumentClass() {
//    if (hasOldCmake) return com.jetbrains.cidr.cpp.cmake.psi.CMakeArgument.class;
    if (hasNewCmake) return com.jetbrains.cmake.psi.CMakeArgument.class;
    throw new java.lang.RuntimeException("Unknown CMake classes");
  }

  public static Class<? extends PsiElement> getCMakeArgumentsClass() {
//    if (hasOldCmake) return com.jetbrains.cidr.cpp.cmake.psi.CMakeCommandArguments.class;
    if (hasNewCmake) return com.jetbrains.cmake.psi.CMakeCommandArguments.class;
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
    return new com.jetbrains.cmake.psi.CMakeLexer();
  }
}
