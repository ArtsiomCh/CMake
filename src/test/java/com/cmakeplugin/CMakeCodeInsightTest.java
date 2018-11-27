package com.cmakeplugin;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

public class CMakeCodeInsightTest extends LightCodeInsightFixtureTestCase {

  @Override
  protected String getTestDataPath() {
    return "testData";
  }

  public void testAnnotator() {
    myFixture.configureByFiles("AnnotatorTestData.cmake");
    myFixture.checkHighlighting(true, true, true, false);
  }

}
