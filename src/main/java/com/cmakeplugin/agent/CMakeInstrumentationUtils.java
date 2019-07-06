package com.cmakeplugin.agent;

import static com.cmakeplugin.agent.CMakeInstrumentationAgent.*;
import static com.cmakeplugin.utils.CMakePDC.getPossibleVarDefClass;

import com.cmakeplugin.psi.impl.CMakePsiImplUtil;
import com.cmakeplugin.utils.CMakePSITreeSearch;
import com.cmakeplugin.utils.wrappers.WrappedCmakeCommand;
import com.cmakeplugin.utils.wrappers.WrappedCmakeLiteral;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.application.PathManager;
import com.intellij.psi.PsiElement;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import org.jetbrains.annotations.Nullable;

public class CMakeInstrumentationUtils {

  public static void patchJBclasses() {
    String pluginsPath = PathManager.getPluginsPath();
    String checkedFilePath = pluginsPath + "/CMake.jar";

    if (!new File(checkedFilePath).isFile()) {
      checkedFilePath = pluginsPath + "/CMake/lib/CMake.jar";
      if (!new File(checkedFilePath).isFile())
        throw new RuntimeException("Agent can't be found at: " + pluginsPath);
    }
    String agentFilePath = checkedFilePath;
    String applicationName = "com.intellij.idea.Main";

    for (VirtualMachineDescriptor jvmd : VirtualMachine.list()) {
      if (jvmd.displayName().contains(applicationName)) {
        try {
          VirtualMachine jvm = VirtualMachine.attach(jvmd);

          //fixme
          String platfromPrefix = jvm.getSystemProperties().getProperty("idea.platform.prefix");
          if (platfromPrefix.equals("CLion") || platfromPrefix.equals("AndroidStudio")) {

            // initialize classes for patching to be visible by agent
            Class.forName(CLASS_TO_TRANSFORM_REFERENCES);
            Class.forName(CLASS_TO_TRANSFORM_RESOLVE);
            Class.forName(CLASS_TO_TRANSFORM_SHOWUSAGES);
            Class.forName(CLASS_TO_TRANSFORM_FINDUSAGES);

            ClassLoader cl = Class.forName(CLASS_TO_TRANSFORM_PRESENTATION).getClassLoader();
            // make com.cmakeplugin classes visible inside patched IDEA classes
            Method method = cl.getClass().getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(cl, new File(agentFilePath).toURI().toURL());

            jvm.loadAgent(agentFilePath);
            //            System.out.println("Attached to target JVM and loaded Java agent
            // successfully");
          }
          jvm.detach();
        } catch (Exception e) {
          //                  throw new RuntimeException(e);
          System.out.println("Exception: " + e);
          e.printStackTrace();
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
    return getPossibleVarDefClass().isInstance(o) && prevResult == null
        ? CMakePsiImplUtil.getPresentation(o)
        : prevResult;
  }

  public static PsiElement addNameIdentifierIfVarDef(PsiElement originalCMakeLiteral){
    if (has_getInfoAt_in_stack() && CMakePSITreeSearch.existReferenceTo(originalCMakeLiteral))
      return new WrappedCmakeLiteral(originalCMakeLiteral.getNode());
    return originalCMakeLiteral;
  }

  public static PsiElement addNameIdentifierCommand(PsiElement originalCMakeCommand){
    if (has_getInfoAt_in_stack())
        return new WrappedCmakeCommand(originalCMakeCommand.getNode());
    return originalCMakeCommand;
  }

  private static boolean has_getInfoAt_in_stack(){
    final StackTraceElement[] st = Thread.currentThread().getStackTrace();
    for (StackTraceElement ste : st) {
      if (ste.getMethodName().equals("getInfoAt"))
        return true;
    }
    return false;
  }
}
