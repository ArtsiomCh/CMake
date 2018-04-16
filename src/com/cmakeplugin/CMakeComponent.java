package com.cmakeplugin;

import com.cmakeplugin.annotator.CMakeCLionAnnotator;
import com.cmakeplugin.annotator.CMakeIdeaAnnotator;
import com.cmakeplugin.utils.CMakePDC;
import com.intellij.lang.*;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.lang.findUsages.LanguageFindUsages;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;

public class CMakeComponent implements ApplicationComponent {
  @Override
  public void initComponent() {
    if (CMakePDC.isCLION) return;
//    {
//      LanguageAnnotators.INSTANCE
//              .addExplicitExtension( com.jetbrains.cidr.cpp.cmake.CMakeLanguage.INSTANCE, new CMakeCLionAnnotator() );
//    } else {
      LanguageParserDefinitions.INSTANCE
              .addExplicitExtension( CMakeLanguage.INSTANCE, new CMakeParserDefinition() );
      SyntaxHighlighterFactory.LANGUAGE_FACTORY
              .addExplicitExtension( CMakeLanguage.INSTANCE, new CMakeSyntaxHighlighterFactory() );
      LanguageBraceMatching.INSTANCE
              .addExplicitExtension( CMakeLanguage.INSTANCE, new CMakeBraceMatcher() );
//      LanguageAnnotators.INSTANCE
//              .addExplicitExtension( CMakeLanguage.INSTANCE, new CMakeIdeaAnnotator() );
//      LanguageRefactoringSupport.INSTANCE
//              .addExplicitExtension( CMakeLanguage.INSTANCE, new CMakeRefactoringSupportProvider() );
//      LanguageFindUsages.INSTANCE
//              .addExplicitExtension( CMakeLanguage.INSTANCE, new CMakeFindUsagesProvider() );
//      Extensions.getRootArea().registerExtension()
//    }
  }

}
