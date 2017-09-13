package com.cmakeplugin;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class CMakeLexerAdapter extends FlexAdapter {
  public CMakeLexerAdapter() {
    super(new CMakeLexer((Reader) null));
  }
}
