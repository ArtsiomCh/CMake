package com.jetbrains.cmake.completion.contributors.providers;

import com.cmakeplugin.utils.CMakePDC;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CMakeVariableProviderFriend extends CMakeVariableProvider{

  private static final Logger LOGGER = LoggerFactory.getLogger(CMakeVariableProviderFriend.class);

  public static void myAddCompletions(
      @NotNull CompletionParameters parameters,
      @NotNull CompletionResultSet result,
      @NotNull String variablePrefix,
      @NotNull String[] completionVariables) {
    if (CMakePDC.isCLION) {
//      com.jetbrains.cmake.completion.contributors.providers.CMakeVariableProvider.addCompletions(
      addCompletions(
          parameters, result, variablePrefix, completionVariables);
    }
  }
}
