package com.cmakeplugin.psi;

import com.intellij.psi.tree.IElementType;
import com.cmakeplugin.CMakeLanguage;
import org.jetbrains.annotations.*;

public class CMakeTokenType extends IElementType {
  public CMakeTokenType(@NotNull @NonNls String debugName) {
    super(debugName, CMakeLanguage.INSTANCE);
  }

  @Override
  public String toString() {
    return "CMakeTokenType." + super.toString();
  }
}