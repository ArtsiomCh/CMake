package com.cmakeplugin;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class CMakeFileType extends LanguageFileType {
  public static final CMakeFileType INSTANCE = new CMakeFileType();
  private static final String[] DEFAULT_EXTENSIONS = {"cmake","txt"};

  private CMakeFileType() {
    super(CMakeLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public String getName() {
    return "CMake file";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "CMake language file";
  }

  @NotNull
  @Override
  public String getDefaultExtension() {
    return DEFAULT_EXTENSIONS[0];
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return CMakeIcons.FILE;
  }
}