package com.cmakeplugin;

import com.cmakeplugin.utils.CMakeIFWHILEcheck;
import com.cmakeplugin.utils.CMakePDC;

import com.cmakeplugin.utils.CMakePSITreeSearch;
import com.cmakeplugin.utils.CMakePlusPDC;
import com.intellij.ide.DataManager;
import com.intellij.lang.documentation.DocumentationProviderEx;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CMakeDocProvider extends DocumentationProviderEx {

  @Nullable
  @Override
  public PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file, @Nullable PsiElement contextElement) {
    if (contextElement != null && CMakePDC.COMMAND_NAME_CLASS.isInstance(contextElement.getParent()) )
      return contextElement.getParent();
    return null;
  }

  @Override
  @Nullable
  public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {

    if (!CMakeComponent.isCMakePlusActive) return null;

    if (CMakePDC.COMMAND_NAME_CLASS.isInstance(element)) {
      return CMakeKeywords.getCommandHelp(element.getText());
    }

    if (CMakePDC.isClassOfVarDef(element)) {
      final PsiElement commandNameElement = CMakePSITreeSearch.getCommandNameElement(element);
      if (commandNameElement != null
          && (commandNameElement.textMatches("include")
              || commandNameElement.textMatches("find_package"))) {
        return CMakeKeywords.getModuleHelp(element.getText());
      }
    }

    if (CMakePDC.isClassOfVarDef(element) || CMakePDC.isClassOfVarRefInsideIfWhile(element)) {
      final String elementText = element.getText();

      if (elementText.matches("CMP[0-9]{4}")) {
        return CMakeKeywords.getPolicyHelp(elementText);
      }

      String propHelp = CMakeKeywords.getPropertyHelp(elementText);
      if (propHelp != null) return propHelp;

      String varHelp = CMakeKeywords.getVariableHelp(elementText);
      if (varHelp != null) return varHelp;
    }

    // varRefs inside text
    if (originalElement != null
        && CMakePlusPDC.VARREF_ELEMENT_TYPES.contains(originalElement.getNode().getElementType())) {
      final String elementText = originalElement.getText();
      final Editor editor =
          (Editor) DataManager.getInstance().getDataContext().getData(DataConstants.EDITOR);
      int offsetInText =
          editor.getCaretModel().getOffset() - element.getTextRange().getStartOffset();
      //      if (offsetInText < 0) offsetInText = 0 ;
      for (TextRange innerVarRange : CMakeIFWHILEcheck.getInnerVars(originalElement)) {
        if (innerVarRange.contains(offsetInText)) {
          String innerVarName =
              elementText.substring(innerVarRange.getStartOffset(), innerVarRange.getEndOffset());
          String varHelp = CMakeKeywords.getVariableHelp(innerVarName);
          if (varHelp != null) return varHelp;
        }
      }
    }

    return null;
  }
}
