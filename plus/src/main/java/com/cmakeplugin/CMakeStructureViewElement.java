package com.cmakeplugin;

import com.cmakeplugin.utils.CMakePlusPDC;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.PlatformIcons;
import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;

public class CMakeStructureViewElement implements StructureViewTreeElement, SortableTreeElement {
  private NavigatablePsiElement element;

  public CMakeStructureViewElement(NavigatablePsiElement element) {
    this.element = element;
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
    String name = CMakePlusPDC.getFunMacroName(element);
    return name != null ? name : "";
  }

  @NotNull
  @Override
  public ItemPresentation getPresentation() {
    ItemPresentation presentation = element.getPresentation();
    return presentation != null
        ? presentation
        : new PresentationData(CMakePlusPDC.getFunMacroName(element), null, getIcon(), null);
  }

  private Icon getIcon() {
    Icon result = null;
    if (CMakePlusPDC.FUNCTION_CLASS.isInstance(element)) result = PlatformIcons.FIELD_ICON;
    else if (CMakePlusPDC.MACRO_CLASS.isInstance(element)) result = PlatformIcons.METHOD_ICON;
    return result;
  }

  @Override
  @NotNull
  public TreeElement[] getChildren() {
    if (!CMakePlusPDC.getCMakeFileClass().isInstance(element)) return EMPTY_ARRAY;
    return PsiTreeUtil.findChildrenOfAnyType(
            element, CMakePlusPDC.MACRO_CLASS, CMakePlusPDC.FUNCTION_CLASS)
        .stream()
        .map(CMakeStructureViewElement::new)
        .toArray(TreeElement[]::new);
  }
}
