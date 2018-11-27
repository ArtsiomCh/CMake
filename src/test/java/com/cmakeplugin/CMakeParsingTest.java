package com.cmakeplugin;

import com.intellij.testFramework.ParsingTestCase;

public class CMakeParsingTest extends ParsingTestCase {
  public CMakeParsingTest() {
    super("", "cmake", new CMakeParserDefinition());
  }

  public void testParsingTestData() {
    doTest(true);
  }

  @Override
  protected String getTestDataPath() {
    return "testData";
  }

  @Override
  protected boolean skipSpaces() {
    return false;
  }

  @Override
  protected boolean includeRanges() {
    return true;
  }
}
