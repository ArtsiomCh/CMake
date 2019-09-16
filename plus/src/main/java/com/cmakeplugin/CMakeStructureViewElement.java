package com.cmakeplugin;

import com.cmakeplugin.annotator.CMakeKeywords;
import com.cmakeplugin.utils.CMakeIFWHILEcheck;
import com.cmakeplugin.utils.CMakePDC;
import com.cmakeplugin.utils.CMakePSITreeSearch;
import com.cmakeplugin.utils.CMakePlusPDC;
import com.cmakeplugin.utils.CMakeVarStringUtil;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.PlatformIcons;
import java.util.stream.Stream;
import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;

public abstract class CMakeStructureViewElement
    implements StructureViewTreeElement, SortableTreeElement {
  NavigatablePsiElement element;
  ItemPresentation presentation;
  String presentableText;
  String notesText = null;
  Icon icon = null;
  TextAttributesKey attributesKey = null;

  CMakeStructureViewElement(NavigatablePsiElement element) {
    this.element = element;
    this.presentation = element.getPresentation();
    this.presentableText = element.getText();
  }

  @Override
  public Object getValue() {
    return element;
  }

  @Override
  public void navigate(boolean requestFocus) {
    element.navigate(requestFocus);
  }

  @Override
  public boolean canNavigate() {
    return element.canNavigate();
  }

  @Override
  public boolean canNavigateToSource() {
    return element.canNavigateToSource();
  }

  @NotNull
  @Override
  public String getAlphaSortKey() {
    return presentableText != null ? presentableText : "";
  }

  @NotNull
  @Override
  public ItemPresentation getPresentation() {
    return new PresentationData(presentableText, notesText, icon, attributesKey);
  }

  @Override
  @NotNull
  public TreeElement[] getChildren() {
    return EMPTY_ARRAY;
  }
}

class CMakeFileElement extends CMakeStructureViewElement {

  CMakeFileElement(PsiFile element) {
    super(element);
  }

  @NotNull
  @Override
  public ItemPresentation getPresentation() {
    if (CMakePlusPDC.CMAKE_FILE_CLASS.isInstance(element) && presentation != null)
      return presentation;
    return super.getPresentation();
  }

  @Override
  @NotNull
  public TreeElement[] getChildren() {
    if (!CMakePlusPDC.CMAKE_FILE_CLASS.isInstance(element)) return super.getChildren();

    Stream<TreeElement> macroElements =
        PsiTreeUtil.findChildrenOfAnyType(element, CMakePDC.MACRO_CLASS).stream()
            .map(MacroElement::new);

    Stream<TreeElement> functionElements =
        PsiTreeUtil.findChildrenOfAnyType(element, CMakePDC.FUNCTION_CLASS).stream()
            .map(FunctionElement::new);

    Stream<TreeElement> varDefElements =
        PsiTreeUtil.findChildrenOfAnyType(element, CMakePlusPDC.VARDEF_CLASS).stream()
            .filter(this::isVarDef)
            .map(VarDefElement::new);

    return Stream.of(functionElements, macroElements, varDefElements)
        .flatMap(s -> s)
        .toArray(TreeElement[]::new);
  }

  private boolean isVarDef(NavigatablePsiElement element) {
    return CMakePSITreeSearch.existReferenceTo(element)
        || (CMakeIFWHILEcheck.couldBeVarDef(element)
            && CMakeVarStringUtil.isPredefinedCMakeVar(element.getText()));
  }
}

abstract class FunMacroBase extends CMakeStructureViewElement {

  FunMacroBase(NavigatablePsiElement element) {
    super(element);
    presentableText = CMakePSITreeSearch.getFunMacroName(element);
    notesText = CMakePSITreeSearch.getFunMacroArgs(element);
  }

  @Override
  public void navigate(boolean requestFocus) {
    NavigatablePsiElement name = CMakePSITreeSearch.getFunMacroNameElement(element);
    name = (name != null) ? name : element;
    name.navigate(requestFocus);
  }
}

class MacroElement extends FunMacroBase {

  MacroElement(NavigatablePsiElement element) {
    super(element);
    icon = PlatformIcons.METHOD_ICON;
    attributesKey = CMakeSyntaxHighlighter.MACROS;
  }
}

class FunctionElement extends FunMacroBase {

  FunctionElement(NavigatablePsiElement element) {
    super(element);
    icon = PlatformIcons.FUNCTION_ICON;
    attributesKey = CMakeSyntaxHighlighter.FUNCTION;
  }
}

class VarDefElement extends CMakeStructureViewElement {

  VarDefElement(NavigatablePsiElement element) {
    super(element);
    if (presentation != null
        && (notesText = presentation.getPresentableText()) != null
        && notesText.length() > 20) notesText = notesText.substring(20);
    icon = PlatformIcons.VARIABLE_ICON;
    attributesKey =
        CMakeVarStringUtil.isPredefinedCMakeVar(element.getText())
            ? CMakeSyntaxHighlighter.CMAKE_VAR_DEF
            : CMakeSyntaxHighlighter.VAR_DEF;
  }
}
