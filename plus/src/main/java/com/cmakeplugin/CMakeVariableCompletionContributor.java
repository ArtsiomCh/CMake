package com.cmakeplugin;

import com.cmakeplugin.annotator.CMakeKeywords;
import com.cmakeplugin.utils.CMakePDC;
import com.cmakeplugin.utils.CMakePSITreeSearch;
import com.cmakeplugin.utils.CMakePlusPDC;
import com.cmakeplugin.utils.SemanticChecks;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import java.awt.Color;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class CMakeVariableCompletionContributor extends CompletionContributor {

  /** See {@link com.jetbrains.cmake.completion.CMakeCompletionContributor} */
  public CMakeVariableCompletionContributor() {
    if (!CMakeComponent.isCMakePlusActive) return;
    extend(
        CompletionType.BASIC,
        PlatformPatterns.psiElement()
            .withLanguage(CMakePlusPDC.getLanguageInstance())
            .withElementType(CMakePlusPDC.VARDEF_ELEMENT_TYPES),
        new CMakeVarDefCompletionProvider());
    extend(
        CompletionType.BASIC,
        PlatformPatterns.psiElement()
            .withLanguage(CMakePlusPDC.getLanguageInstance())
            .withElementType(CMakePlusPDC.VARREF_ELEMENT_TYPES),
        new CMakeVarRefCompletionProvider());
  }

  private static class CMakeVarDefCompletionProvider
      extends CompletionProvider<CompletionParameters> {

    public void addCompletions(
        @NotNull CompletionParameters parameters,
        @NotNull ProcessingContext context,
        @NotNull CompletionResultSet resultSet) {

      if (SemanticChecks.possibleVarDef(parameters.getPosition())) {
/*
        final Color varDefColor =
            CMakeSyntaxHighlighter.VAR_DEF.getDefaultAttributes().getForegroundColor();

        resultSet.addAllElements(
            CMakePSITreeSearch.getAllReferencedVarDefs(parameters.getOriginalFile())
                .map(LookupElementBuilder::create)
                .map(it -> it.withIcon(CMakePlusPDC.ICON_VAR))
                .map(it -> it.withItemTextForeground(varDefColor))
                .collect(Collectors.toList()));
*/

        resultSet.addAllElements(
            CMakePSITreeSearch.getAllPossibleVarDefs(parameters.getOriginalFile())
                .map(LookupElementBuilder::create)
                .map(it -> it.withIcon(CMakePlusPDC.ICON_VAR))
                .collect(Collectors.toList()));

//        if (!CMakePDC.isCLION)
          resultSet.addAllElements(
              CMakeKeywords.getAllVariables().stream()
                  .map(LookupElementBuilder::create)
                  .collect(Collectors.toList()));
      }
    }
  }

  private static class CMakeVarRefCompletionProvider
      extends CompletionProvider<CompletionParameters> {

    public void addCompletions(
        @NotNull CompletionParameters parameters,
        @NotNull ProcessingContext context,
        @NotNull CompletionResultSet resultSet) {

      final PsiElement element = parameters.getPosition();
      final String text = element.getText();
      if (text.length() < 2) return;
      final int offsetInElement = parameters.getOffset() - element.getTextRange().getStartOffset();
      int varDefStart = text.substring(0, offsetInElement).lastIndexOf("${");
      if (varDefStart >= 0) {
        resultSet = resultSet.withPrefixMatcher(text.substring(varDefStart + 2, offsetInElement));
/*
        final Color varDefColor =
            CMakeSyntaxHighlighter.VAR_DEF.getDefaultAttributes().getForegroundColor();

        resultSet.addAllElements(
            CMakePSITreeSearch.getAllReferencedVarDefs(parameters.getOriginalFile())
                .map(LookupElementBuilder::create)
                .map(it -> it.withIcon(CMakePlusPDC.ICON_VAR))
                .map(it -> it.withItemTextForeground(varDefColor))
                .map(it -> it.withInsertHandler(getVarRefInsertHandler()))
                .collect(Collectors.toList()));
*/

        resultSet.addAllElements(
            CMakePSITreeSearch.getAllPossibleVarDefs(parameters.getOriginalFile())
                .map(LookupElementBuilder::create)
                .map(it -> it.withIcon(CMakePlusPDC.ICON_VAR))
                .map(it -> it.withInsertHandler(getVarRefInsertHandler()))
                .collect(Collectors.toList()));

        if (!CMakePDC.isCLION)
          resultSet.addAllElements(
              CMakeKeywords.getAllVariables().stream()
                  .map(LookupElementBuilder::create)
                  .map(it -> it.withInsertHandler(getVarRefInsertHandler()))
                  .collect(Collectors.toList()));
      }
      /*
            if (text.contains("${")) {
              final String[] allPossibleVarDefs =
                  CMakePSITreeSearch.getAllPossibleVarDefs(parameters.getOriginalFile())
                      .toArray(String[]::new);
              CMakeVariableProviderFriend.myAddCompletions(parameters, resultSet, "${", allPossibleVarDefs);
            }
      */
    }

    private static InsertHandler<LookupElement> getVarRefInsertHandler() {
      return new InsertHandler<LookupElement>() {
        @Override
        public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {
          final int indexLookupEnd = context.getStartOffset() + item.getLookupString().length();
          int caretShift = 0;
          if (context.getDocument().getText().charAt(indexLookupEnd) != '}')
            context.getDocument().insertString(context.getTailOffset(), "}");
          else caretShift = 1;
          context.getEditor().getCaretModel().moveToOffset(context.getTailOffset() + caretShift);
        }
      };
    }
  }
}
