package com.jetbrains.cmake.psi;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;

public interface CMakeTokenTypes {
  IElementType QUOTE = new IElementType("", Language.ANY);
}
