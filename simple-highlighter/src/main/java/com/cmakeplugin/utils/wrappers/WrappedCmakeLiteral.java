package com.cmakeplugin.utils.wrappers;

import com.intellij.lang.ASTNode;
import com.jetbrains.cmake.psi.CMakeLiteralImpl;

public class WrappedCmakeLiteral extends CMakeLiteralImpl implements WrappedCmakeElement {

  public WrappedCmakeLiteral(ASTNode node) {
    super(node);
  }

}
