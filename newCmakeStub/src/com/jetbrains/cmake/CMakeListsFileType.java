package com.jetbrains.cmake;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CMakeListsFileType extends LanguageFileType {
  public static final FileType INSTANCE = new CMakeListsFileType();

  protected CMakeListsFileType() { super(null); }

  @NotNull
  @Override
  public String getName() {
    return null;
  }

  @NotNull
  @Override
  public String getDescription() {
    return null;
  }

  @NotNull
  @Override
  public String getDefaultExtension() {
    return null;
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return null;
  }
}
