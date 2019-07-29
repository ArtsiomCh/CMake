package com.cmakeplugin.agent;

import static com.cmakeplugin.agent.CMakeInstrumentationAgent.*;

import com.cmakeplugin.utils.CMakePDC;

import com.cmakeplugin.psi.impl.CMakePsiImplUtil;
import com.cmakeplugin.utils.CMakeIFWHILEcheck;
import com.cmakeplugin.utils.CMakePSITreeSearch;
import com.cmakeplugin.utils.wrappers.WrappedCmakeCommand;
import com.cmakeplugin.utils.wrappers.WrappedCmakeLiteral;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.application.PathManager;
import com.intellij.psi.PsiElement;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CMakeInstrumentationUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(CMakeInstrumentationUtils.class);

  public static void patchJBclasses() {
    LOGGER.info("Start patching bundled com.jetbrains.cmake.* classes");
    String pluginsPath = PathManager.getPluginsPath();
    String checkedFilePath = pluginsPath + "/CMake.jar";

    if (!new File(checkedFilePath).isFile()) {
      checkedFilePath = pluginsPath + "/CMake/lib/CMake.jar";
      if (!new File(checkedFilePath).isFile()) {
        LOGGER.warn("Agent can't be found at: {}", checkedFilePath);
        return;
      }
    }
    final String agentFilePath = checkedFilePath;
    final String applicationName = "com.intellij.idea.Main";

    for (VirtualMachineDescriptor jvmd : VirtualMachine.list()) {
      if (jvmd.displayName().contains(applicationName)) {
        try {
          LOGGER.info("Attaching to target JVM: {}:{}", jvmd.displayName(), jvmd.id());
          VirtualMachine jvm = VirtualMachine.attach(jvmd);
          String platfromPrefix = jvm.getSystemProperties().getProperty("idea.platform.prefix");
          if (platfromPrefix.equals("CLion") || platfromPrefix.equals("AndroidStudio")) {
            try {
              // initialize classes for patching to be visible by agent
              Class.forName(CLASS_TO_TRANSFORM_REFERENCES);
              Class.forName(CLASS_TO_TRANSFORM_RESOLVE);
              Class.forName(CLASS_TO_TRANSFORM_SHOWUSAGES);
              Class.forName(CLASS_TO_TRANSFORM_FINDUSAGES);
              Class.forName(CLASS_TO_TRANSFORM_HIGHLIGHT_MULTIRESOLVE);

              ClassLoader cl = Class.forName(CLASS_TO_TRANSFORM_PRESENTATION).getClassLoader();
              // make com.cmakeplugin classes visible inside patched IDEA classes
              Method method = cl.getClass().getDeclaredMethod("addURL", URL.class);
              method.setAccessible(true);
              method.invoke(cl, new File(agentFilePath).toURI().toURL());
            } catch (IllegalAccessException
                | InvocationTargetException
                | ClassNotFoundException
                | NoSuchMethodException e) {
              LOGGER.warn("Exception performing reflective operation over class: ", e);
            }

            try {
              LOGGER.info("Loading Java agent: {}", agentFilePath);
              jvm.loadAgent(agentFilePath);
              LOGGER.info("Loaded Java agent successfully: {}", agentFilePath);
            } catch (AgentLoadException | AgentInitializationException e) {
              LOGGER.warn("Exception loading Java agent: ", e);
            }

            LOGGER.info("Attached to target JVM successfully: {} ({}:{})", platfromPrefix, jvmd.displayName(), jvmd.id());
          }
          jvm.detach();
        } catch (AttachNotSupportedException | IOException e) {
          LOGGER.warn("Exception attaching to target JVM: {}:{}", jvmd.displayName(), jvmd.id(), e);
        }
      }
    }
  }

  public static <T> T[] concatArrays(T[] first, T[] second) {
    T[] result;
    if (first.length == 0) result = second;
    else if (second.length == 0) result = first;
    else {
      result = Arrays.copyOf(first, first.length + second.length);
      System.arraycopy(second, 0, result, first.length, second.length);
    }
    return result;
  }

  @Nullable
  public static ItemPresentation getPresentation(PsiElement o, ItemPresentation prevResult) {
    return CMakePDC.isClassOfVarDef(o) && prevResult == null
        ? CMakePsiImplUtil.getPresentation(o)
        : prevResult;
  }

  public static PsiElement addNameIdentifierIfVarDef(PsiElement originalCMakeLiteral) {
    if (hasCallInStack("CtrlMouseHandler", "getInfoAt")
        && CMakePSITreeSearch.existReferenceTo(originalCMakeLiteral))
      return new WrappedCmakeLiteral(originalCMakeLiteral.getNode());
    return originalCMakeLiteral;
  }

  public static PsiElement addNameIdentifierCommand(PsiElement originalCMakeCommand) {
    if (hasCallInStack("CtrlMouseHandler", "getInfoAt"))
      return new WrappedCmakeCommand(originalCMakeCommand.getNode());
    return originalCMakeCommand;
  }

  public static PsiElement getNullIfVarRefMultiResolve(PsiElement originalCMakeLiteral) {
    if (hasCallInStack("IdentifierHighlighterPass", "doCollectInformation")) {
      /*
            Editor editor = null;
            try {
              editor =
                  Objects.requireNonNull(
                          DataManager.getInstance().getDataContextFromFocusAsync().blockingGet(2000))
                      .getData(DataKeys.EDITOR);
            } catch (TimeoutException | ExecutionException e) {
              e.printStackTrace();
            }
            PsiReference ref = TargetElementUtil.findReference(Objects.requireNonNull(editor));
            if (ref instanceof PsiPolyVariantReference) return null;
      */
      if (!CMakeIFWHILEcheck.getInnerVars(originalCMakeLiteral).isEmpty()) return null;
    }
    return originalCMakeLiteral;
  }

  private static boolean hasCallInStack(String clazz, String method) {
    final StackTraceElement[] st = Thread.currentThread().getStackTrace();
    for (StackTraceElement ste : st) {
      if (ste.getMethodName().equals(method) && ste.getClassName().contains(clazz)) return true;
    }
    return false;
  }
}
