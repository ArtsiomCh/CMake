package com.cmakeplugin;

import com.cmakeplugin.utils.CMakePDC;
import com.cmakeplugin.utils.CMakePlusPDC;
import com.intellij.ide.structureView.*;
import com.intellij.ide.util.treeView.smartTree.ActionPresentation;
import com.intellij.ide.util.treeView.smartTree.ActionPresentationData;
import com.intellij.ide.util.treeView.smartTree.Filter;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CMakeStructureViewModel extends StructureViewModelBase
    implements StructureViewModel.ElementInfoProvider {

  public CMakeStructureViewModel(PsiFile psiFile, @Nullable Editor editor) {
    super(psiFile, editor, new CMakeFileElement(psiFile));
  }

  @NotNull
  public Sorter[] getSorters() {
    return new Sorter[] {Sorter.ALPHA_SORTER};
  }

  @Override
  @NotNull
  public Filter[] getFilters() {
    return new Filter[] {new FunctionFilter(), new MacroFilter(), new VarDefFilter()};
  }

  @Override
  @NotNull
  protected Class[] getSuitableClasses() {
    return new Class[] {
      CMakePlusPDC.CMAKE_FILE_CLASS,
      CMakePDC.FUNCTION_CLASS,
      CMakePDC.MACRO_CLASS,
      CMakePlusPDC.VARDEF_CLASS
    };
  }

  @Override
  public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
    return false;
  }

  @Override
  public boolean isAlwaysLeaf(StructureViewTreeElement element) {
    return CMakePlusPDC.CMAKE_FILE_CLASS.isInstance(element);
  }

  private static class VarDefFilter implements Filter {

    @Override
    public boolean isVisible(TreeElement treeNode) {
      return !(treeNode instanceof VarDefElement);
    }

    @Override
    public boolean isReverted() {
      return true;
    }

    @NotNull
    @Override
    public ActionPresentation getPresentation() {
      return new ActionPresentationData(
          "Show Variables (re)definitions", null, CMakePlusPDC.ICON_VAR);
    }

    @NotNull
    @Override
    public String getName() {
      return "SHOW_VAR_DEF";
    }
  }

  private static class MacroFilter implements Filter {

    @Override
    public boolean isVisible(TreeElement treeNode) {
      return !(treeNode instanceof MacroElement);
    }

    @Override
    public boolean isReverted() {
      return true;
    }

    @NotNull
    @Override
    public ActionPresentation getPresentation() {
      return new ActionPresentationData(
          "Show Macros definitions", null, CMakePlusPDC.ICON_CMAKE_MACRO);
    }

    @NotNull
    @Override
    public String getName() {
      return "SHOW_MACRO";
    }
  }

  private static class FunctionFilter implements Filter {

    @Override
    public boolean isVisible(TreeElement treeNode) {
      return !(treeNode instanceof FunctionElement);
    }

    @Override
    public boolean isReverted() {
      return true;
    }

    @NotNull
    @Override
    public ActionPresentation getPresentation() {
      return new ActionPresentationData(
          "Show Functions definitions", null, CMakePlusPDC.ICON_CMAKE_FUNCTION);
    }

    @NotNull
    @Override
    public String getName() {
      return "SHOW_FUNCTIONS";
    }
  }
}
