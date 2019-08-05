package com.cmakeplugin.psi;

import com.intellij.psi.tree.IElementType;
import com.cmakeplugin.CMakeLanguage;
import org.jetbrains.annotations.*;

public class CMakeElementType extends IElementType {
  public CMakeElementType(@NotNull @NonNls String debugName) {
    super(debugName, CMakeLanguage.INSTANCE);
  }
}