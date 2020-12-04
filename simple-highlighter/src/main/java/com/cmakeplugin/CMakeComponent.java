package com.cmakeplugin;

import com.cmakeplugin.utils.CMakeProxyToJB;
import com.intellij.ide.BrowserUtil;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.lang.*;
import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.DumbAwareAction;
import org.jetbrains.annotations.NotNull;

public class CMakeComponent implements ApplicationComponent {
  private static final Logger LOG = Logger.getInstance(CMakeComponent.class);

  public static boolean isCMakePlusActive =
      isCMakePlusEnabled(); //&& com.cmakeplugin.CheckLicense.isLicensed();

  private static boolean isCMakePlusEnabled() {
    final String idString = "artsiomch.cmake.plus";
    final IdeaPluginDescriptor pluginDescriptor = PluginManager.getPlugin(PluginId.getId(idString));
    return pluginDescriptor != null && pluginDescriptor.isEnabled();
  }

  @Override
  public void initComponent() {
    if (CMakeProxyToJB.isCLION) {

    } else {
      LanguageParserDefinitions.INSTANCE.addExplicitExtension(
          CMakeLanguage.INSTANCE, new CMakeParserDefinition());
      SyntaxHighlighterFactory.LANGUAGE_FACTORY.addExplicitExtension(
          CMakeLanguage.INSTANCE, new CMakeSyntaxHighlighterFactory());
      LanguageBraceMatching.INSTANCE.addExplicitExtension(
          CMakeLanguage.INSTANCE, new CMakeBraceMatcher());
    }
    if (!isCMakePlusActive) {
      final Notification notification = new balloonCmakePlusLink();
      notification.notify(null);
    }
  }

  private static class balloonCmakePlusLink extends Notification {
    balloonCmakePlusLink() {
      super("CMake simple highlighter",
          "CMake simple highlighter",
          "For more functionality consider plugin: ",
          NotificationType.INFORMATION);
      addAction(new ShowCmakePlusAction());
    }
  }

  private static class ShowCmakePlusAction extends DumbAwareAction {
    ShowCmakePlusAction() {
      super("CMake Plus");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
      BrowserUtil.open("https://plugins.jetbrains.com/plugin/12869-cmake-plus");
    }
  }

}
