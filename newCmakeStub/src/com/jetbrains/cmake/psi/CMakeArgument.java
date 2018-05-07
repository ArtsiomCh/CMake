package com.jetbrains.cmake.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public class CMakeArgument extends baseStubClass {
  @Nullable
  public CMakeLiteral getCMakeLiteral(){
    return null;
  }

  @Nullable
  PsiElement getBracketArgEnd() {
    return null;
  }

  @Nullable
  public PsiElement getBracketArgStart() {
    return null;
  }
}
