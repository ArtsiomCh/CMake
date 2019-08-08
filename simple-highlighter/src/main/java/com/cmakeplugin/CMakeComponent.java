package com.cmakeplugin;

import com.cmakeplugin.utils.CMakePDC;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.lang.*;
import com.intellij.notification.*;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;

public class CMakeComponent implements ApplicationComponent {

  private static final boolean isCMakePlusActive =
      isCMakePlusEnabled(); //&& com.cmakeplugin.CheckLicense.isLicensed();

  private static boolean isCMakePlusEnabled() {
    final String idString = "artsiomch.cmake.plus";
    final IdeaPluginDescriptor pluginDescriptor = PluginManager.getPlugin(PluginId.getId(idString));
    return pluginDescriptor != null && pluginDescriptor.isEnabled();
  }

  @Override
  public void initComponent() {
    if (CMakePDC.isCLION) {

    } else {
      LanguageParserDefinitions.INSTANCE.addExplicitExtension(
          CMakeLanguage.INSTANCE, new CMakeParserDefinition());
      SyntaxHighlighterFactory.LANGUAGE_FACTORY.addExplicitExtension(
          CMakeLanguage.INSTANCE, new CMakeSyntaxHighlighterFactory());
      LanguageBraceMatching.INSTANCE.addExplicitExtension(
          CMakeLanguage.INSTANCE, new CMakeBraceMatcher());
    }
    if (!isCMakePlusActive) {
      final String content =
          "For more functionality (variables navigation/renaming) please consider "
              + "<b><a href=\"https://plugins.jetbrains.com/plugin/10089-cmake-simple-highlighter\">CMake Plus</a></b> plugin.<br/>";
      Notifications.Bus.notify(
          NotificationGroup.balloonGroup("com.cmakeplugin")
              .createNotification(
                  "CMake simple highlighter",
                  content,
                  NotificationType.INFORMATION,
                  NotificationListener.URL_OPENING_LISTENER));
    }
  }
}
