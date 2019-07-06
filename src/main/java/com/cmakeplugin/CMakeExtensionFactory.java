package com.cmakeplugin;

import com.cmakeplugin.annotator.CMakeCLionAnnotator;
import com.cmakeplugin.annotator.CMakeIdeaAnnotator;
import com.cmakeplugin.utils.CMakePDC;
import com.intellij.lang.findUsages.EmptyFindUsagesProvider;
import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.openapi.extensions.ExtensionFactory;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import org.jetbrains.annotations.NotNull;

public class CMakeExtensionFactory implements ExtensionFactory {
  @Override
  public Object createInstance(String factoryArgument, String implementationClass) {
    //    Object result = null;
    //    if (factoryArgument.equals("Annotator")) {
    //      result = CMakePDC.isCLION
    //              ? new CMakeCLionAnnotator()
    //              : new CMakeIdeaAnnotator();
    //    }
    //    else if (!CMakePDC.isCLION) {
    //      switch (factoryArgument) {
    //        case "parserDefinition" : result = new CMakeParserDefinition(); break;
    //        case "syntaxHighlighterFactory" : result = new CMakeSyntaxHighlighterFactory(); break;
    //        case "braceMatcher" : result = new CMakeBraceMatcher(); break;
    //        case "refactoringSupport" : result = new CMakeRefactoringSupportProvider(); break;
    //        case "findUsagesProvider" : result = new CMakeFindUsagesProvider(); break;
    //      }
    //    }
    //    return result;
    switch (factoryArgument) {
      case "Annotator":
        return CMakePDC.isCLION ? new CMakeCLionAnnotator() : new CMakeIdeaAnnotator();
      case "refactoringSupport":
        return CMakePDC.isCLION
            ? new RefactoringSupportProvider() {}
            : new CMakeRefactoringSupportProvider();
//      case "findUsagesProvider":
//        return /*CMakePDC.isCLION ? new EmptyFindUsagesProvider() :*/ new CMakeFindUsagesProvider();
/*
      case "referenceContributor":
        return CMakePDC.isCLION
            ? new CMakeReferenceContributor()
            : new PsiReferenceContributor() {
              @Override
              public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {}
            };
*/
      default:
        throw new java.lang.RuntimeException(
            "Unknown factoryArgument for CMakeExtensionFactory: " + factoryArgument);
    }
  }
}
