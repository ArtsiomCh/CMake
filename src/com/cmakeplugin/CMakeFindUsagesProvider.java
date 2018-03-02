package com.cmakeplugin;

import com.cmakeplugin.psi.CMakeTypes;
import com.intellij.lang.HelpID;
import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.ElementDescriptionUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.usageView.UsageViewLongNameLocation;
import com.intellij.usageView.UsageViewNodeTextLocation;
import com.intellij.usageView.UsageViewTypeLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import static com.cmakeplugin.utils.CMakeVariablesUtil.*;

public class CMakeFindUsagesProvider implements FindUsagesProvider {
  private static final DefaultWordsScanner WORDS_SCANNER =
          new DefaultWordsScanner(new CMakeLexerAdapter(),
                  TokenSet.create(CMakeTypes.COMMAND_NAME),
                  TokenSet.create(CMakeTypes.LINE_COMMENT,
                          CMakeTypes.BRACKET_COMMENT),
                  TokenSet.EMPTY);
  @Nullable
  @Override
  public WordsScanner getWordsScanner() {
    return WORDS_SCANNER;
  }

  @Override
  public boolean canFindUsagesFor(PsiElement psiElement) {
    return isPossibleVarDefinition(psiElement.getText());  /*psiElement instanceof CMakeNamedElement;*/
  }

  @Nullable
  @Override
  public String getHelpId(PsiElement psiElement) {
    return HelpID.FIND_OTHER_USAGES;
  }

  @NotNull
  @Override
  public String getType(PsiElement psiElement) {
    return "test Type";//ElementDescriptionUtil.getElementDescription(psiElement, UsageViewTypeLocation.INSTANCE);
  }

  @NotNull
  @Override
  public String getDescriptiveName(@NotNull PsiElement psiElement) {
    return "test DescriptiveName";//ElementDescriptionUtil.getElementDescription(psiElement, UsageViewLongNameLocation.INSTANCE);
  }

  @NotNull
  @Override
  public String getNodeText(@NotNull PsiElement psiElement, boolean b) {
    return "test NodeText";//ElementDescriptionUtil.getElementDescription(psiElement, UsageViewNodeTextLocation.INSTANCE);
  }
}
