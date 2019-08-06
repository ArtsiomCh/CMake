package com.cmakeplugin;

import com.cmakeplugin.agent.CMakeInstrumentationUtils;
import com.cmakeplugin.utils.CMakePDC;
import com.intellij.openapi.components.ApplicationComponent;

public class CMakePlusComponent implements ApplicationComponent {

  @Override
  public void initComponent() {
    if (CMakePDC.isCLION) {
      CMakeInstrumentationUtils.patchJBclasses();
    }
  }
}
