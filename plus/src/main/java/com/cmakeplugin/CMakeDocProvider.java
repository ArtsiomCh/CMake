package com.cmakeplugin;

import com.cmakeplugin.utils.CMakeIFWHILEcheck;
import com.cmakeplugin.utils.CMakePDC;

import com.cmakeplugin.utils.CMakePSITreeSearch;
import com.cmakeplugin.utils.CMakePlusPDC;
import com.intellij.lang.documentation.DocumentationProviderEx;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CMakeDocProvider extends DocumentationProviderEx {
  private static final Logger LOG = LoggerFactory.getLogger(CMakeDocProvider.class);

  @Nullable
  @Override
  public PsiElement getCustomDocumentationElement(
      @NotNull Editor editor, @NotNull PsiFile file, @Nullable PsiElement contextElement) {
    if (contextElement == null) return null;
    if (CMakePDC.COMMAND_NAME_CLASS.isInstance(contextElement.getParent()))
      return contextElement.getParent();
    if (CMakePlusPDC.COMMAND_KEYWORD_ELEMENT_TYPES.contains(contextElement.getNode().getElementType()))
      return contextElement;
    return null;
  }

  @Override
  @Nullable
  public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {

    if (!CMakeComponent.isCMakePlusActive) return null;

    if (CMakePDC.COMMAND_NAME_CLASS.isInstance(element)
        || CMakePlusPDC.COMMAND_KEYWORD_ELEMENT_TYPES.contains(element.getNode().getElementType()) ) {
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
      int offsetInText = getCaretOffsetInElement(originalElement);
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

  // fixme
  // https://intellij-support.jetbrains.com/hc/en-us/community/posts/206794335-How-to-get-cursor-position-in-the-current-editor-?
  private int getCaretOffsetInElement(PsiElement element){
    // PsiUtilBase.findEditor(element);
    // (Editor) DataManager.getInstance().getDataContext().getData(DataConstants.EDITOR);
    Editor[] editor = new Editor[1];
    ApplicationManager.getApplication()
        .invokeLater(
            () ->
                editor[0] =
                    FileEditorManager.getInstance(element.getProject()).getSelectedTextEditor());
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return editor[0].getCaretModel().getOffset() - element.getTextRange().getStartOffset();
    //      if (offsetInText < 0) offsetInText = 0 ;
  }
}
