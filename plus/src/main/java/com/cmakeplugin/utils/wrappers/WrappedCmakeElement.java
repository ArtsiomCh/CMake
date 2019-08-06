package com.cmakeplugin.utils.wrappers;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.Nullable;

public interface WrappedCmakeElement extends PsiNameIdentifierOwner {

  @Nullable
  @Override
  default PsiElement getNameIdentifier() {
    return this;
  }
}

