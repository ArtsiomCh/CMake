package com.cmakeplugin;

import com.intellij.lang.Language;

public class CMakeLanguage extends Language {
  public static final CMakeLanguage INSTANCE = new CMakeLanguage();

  private CMakeLanguage() {
    super("CMake");
  }
}
