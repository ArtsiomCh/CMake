package com.cmakeplugin;

import com.intellij.lang.findUsages.EmptyFindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;
import static com.cmakeplugin.utils.CMakeVarStringUtil.*;

public class CMakeFindUsagesProvider extends EmptyFindUsagesProvider {

  @Override
  public boolean canFindUsagesFor(PsiElement psiElement) {
    return psiElement instanceof PsiNamedElement
           && isPossibleVarDefinition(psiElement.getText());
  }

  @NotNull
  @Override
  public String getType(PsiElement psiElement) {
    return "Variable";//ElementDescriptionUtil.getElementDescription(psiElement, UsageViewTypeLocation.INSTANCE);
  }

}
