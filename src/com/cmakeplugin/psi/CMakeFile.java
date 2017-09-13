package com.cmakeplugin.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.cmakeplugin.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CMakeFile extends PsiFileBase {
  public CMakeFile(@NotNull FileViewProvider viewProvider) {
    super(viewProvider, CMakeLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public FileType getFileType() {
    return CMakeFileType.INSTANCE;
  }

  @Override
  public String toString() {
    return "CMake File";
  }

  @Override
  public Icon getIcon(int flags) {
    return super.getIcon(flags);
  }
}