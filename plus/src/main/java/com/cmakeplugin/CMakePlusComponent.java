package com.cmakeplugin;

import com.cmakeplugin.agent.CMakeInstrumentationUtils;
import com.cmakeplugin.utils.CMakePDC;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.extensions.PluginId;
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
    CMakeComponent.isCMakePlusActive = checkVersionOfCmakeSimpleHighlighter();
    if (!CMakeComponent.isCMakePlusActive) {
      final String message =
          "Update CMake Simple Highlighter plugin above " + FROM_BRANCH + "." + FROM_BUILD
              + " please, to enable CMake Plus functionality.";
      LOGGER.warn(message);
      new Notification("CMake Plus", "CMake Plus", message, NotificationType.WARNING).notify(null);
      return;
    }
    if (CMakePDC.isCLION) {
      CMakeInstrumentationUtils.patchJBclasses();
    }
  }

  private static final int FROM_BRANCH = 192;
  private static final int FROM_BUILD = 4;

  private boolean checkVersionOfCmakeSimpleHighlighter() {
    final IdeaPluginDescriptor pluginDescriptor =
        PluginManager.getPlugin(PluginId.getId("artsiomch.cmake"));
    String versionStr = (pluginDescriptor != null) ? pluginDescriptor.getVersion() : "";
    String[] versionArr = versionStr.split("\\.");
    return versionArr.length > 2
        && Integer.parseInt(versionArr[0]) >= FROM_BRANCH
        && Integer.parseInt(versionArr[1]) >= FROM_BUILD
        ;
  }
}
