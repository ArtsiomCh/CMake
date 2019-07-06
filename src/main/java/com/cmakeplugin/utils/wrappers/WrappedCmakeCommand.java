package com.cmakeplugin.utils.wrappers;

import com.intellij.lang.ASTNode;
import com.jetbrains.cmake.psi.CMakeCommandNameImpl;

public class WrappedCmakeCommand extends CMakeCommandNameImpl implements WrappedCmakeElement {

  public WrappedCmakeCommand(ASTNode node) {
    super(node);
  }

}
