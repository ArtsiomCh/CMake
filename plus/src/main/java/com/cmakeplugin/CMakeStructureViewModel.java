package com.cmakeplugin;

import com.cmakeplugin.utils.CMakePlusPDC;
import com.intellij.ide.structureView.*;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class CMakeStructureViewModel extends StructureViewModelBase
    implements StructureViewModel.ElementInfoProvider {

  public CMakeStructureViewModel(PsiFile psiFile) {
    super(psiFile, new CMakeStructureViewElement(psiFile));
  }

  @NotNull
  public Sorter[] getSorters() {
    return new Sorter[] {Sorter.ALPHA_SORTER};
  }

  @Override
  public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
    return false;
  }

  @Override
  public boolean isAlwaysLeaf(StructureViewTreeElement element) {
    return CMakePlusPDC.getCMakeFileClass().isInstance(element);
  }
}
