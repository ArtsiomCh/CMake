package com.cmakeplugin;

import com.cmakeplugin.utils.CMakeProxyToJB;
import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.openapi.extensions.ExtensionFactory;

public class CMakePlusExtensionFactory implements ExtensionFactory {
  @Override
  public Object createInstance(String factoryArgument, String implementationClass) {
    switch (factoryArgument) {
      case "refactoringSupport":
        return CMakeProxyToJB.isCLION
            ? new RefactoringSupportProvider() {}
            : new CMakeRefactoringSupportProvider();
      default:
        throw new java.lang.RuntimeException(
            "Unknown factoryArgument for CMakePlusExtensionFactory: " + factoryArgument);
    }
  }
}
