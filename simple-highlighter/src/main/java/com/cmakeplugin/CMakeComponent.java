package com.cmakeplugin;

import com.cmakeplugin.utils.CMakePDC;
import com.intellij.lang.*;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;

public class CMakeComponent implements ApplicationComponent {

  @Override
  public void initComponent() {
    if (CMakePDC.isCLION) {

    } else {
      LanguageParserDefinitions.INSTANCE.addExplicitExtension(
          CMakeLanguage.INSTANCE, new CMakeParserDefinition());
      SyntaxHighlighterFactory.LANGUAGE_FACTORY.addExplicitExtension(
          CMakeLanguage.INSTANCE, new CMakeSyntaxHighlighterFactory());
      LanguageBraceMatching.INSTANCE.addExplicitExtension(
          CMakeLanguage.INSTANCE, new CMakeBraceMatcher());
    }
  }
}
