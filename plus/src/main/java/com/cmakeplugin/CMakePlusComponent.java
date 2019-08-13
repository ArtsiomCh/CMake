package com.cmakeplugin;

import com.cmakeplugin.agent.CMakeInstrumentationUtils;
import com.cmakeplugin.utils.CMakePDC;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.ApplicationComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CMakePlusComponent implements ApplicationComponent {
  private static final Logger LOGGER = LoggerFactory.getLogger(CMakePlusComponent.class);

  @Override
  public void initComponent() {
    CMakeComponent.isCMakePlusActive = CheckLicense.isLicensed();
    if (!CMakeComponent.isCMakePlusActive) {
      final String message =
          "CMake Plus plugin License not found. Plugin functionality will be disabled.";
      LOGGER.warn(message);
      new Notification("CMake Plus", "CMake Plus", message, NotificationType.WARNING).notify(null);
      return;
    }
    if (CMakePDC.isCLION) {
      CMakeInstrumentationUtils.patchJBclasses();
    }
  }
}
