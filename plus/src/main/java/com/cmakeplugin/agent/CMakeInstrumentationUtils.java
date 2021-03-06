package com.cmakeplugin.agent;

import static com.cmakeplugin.agent.CMakeInstrumentationAgent.*;

import com.cmakeplugin.psi.impl.CMakePsiImplUtil;
import com.cmakeplugin.utils.CMakeIFWHILEcheck;
import com.cmakeplugin.utils.CMakePDC;
import com.cmakeplugin.utils.CMakePSITreeSearch;
import com.cmakeplugin.utils.wrappers.WrappedCmakeCommand;
import com.cmakeplugin.utils.wrappers.WrappedCmakeLiteral;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.application.PathManager;
import com.intellij.psi.PsiElement;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import org.avaje.agentloader.AgentLoader;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CMakeInstrumentationUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(CMakeInstrumentationUtils.class);

  public static void patchJBclasses() {
    LOGGER.info("Start patching bundled com.jetbrains.cmake.* classes");

    String agentFilePath = PathManager.getJarPathForClass(CMakeInstrumentationUtils.class);
    if (agentFilePath == null || !new File(agentFilePath).isFile()) {
      LOGGER.warn("Agent (CMake-plus) not found at: {}", agentFilePath);
      return;
    }
    String simpleHighlighterFilePath = PathManager.getJarPathForClass(CMakePSITreeSearch.class);
    if (simpleHighlighterFilePath == null || !new File(simpleHighlighterFilePath).isFile()) {
      LOGGER.warn("CMake-simple-highlighter not found at: {}", simpleHighlighterFilePath);
      return;
    }

    String bundledCmakeFilePath = PathManager.getJarPathForClass(CMakePDC.ARGUMENTS_CLASS);
    if (bundledCmakeFilePath == null || !new File(bundledCmakeFilePath).isFile()) {
      LOGGER.warn("Bundled JB Cmake not found at: {}", bundledCmakeFilePath);
      return;
    }

    try {
      // initialize classes for patching to be visible by agent
      Class.forName(CLASS_TO_TRANSFORM_REFERENCES);
      Class.forName(CLASS_TO_TRANSFORM_RESOLVE);
      Class.forName(CLASS_TO_TRANSFORM_SHOWUSAGES);
      Class.forName(CLASS_TO_TRANSFORM_FINDUSAGES);
      Class.forName(CLASS_TO_TRANSFORM_HIGHLIGHT_MULTIRESOLVE);
      Class.forName(CLASS_TO_TRANSFORM_PRESENTATION);

      // make com.cmakeplugin classes visible inside patched IDEA classes
      ClassLoader cl = Class.forName("com.intellij.psi.impl.PsiElementBase").getClassLoader();
      Method method = cl.getClass().getMethod("addURL", URL.class);
      method.setAccessible(true);
      method.invoke(cl, new File(agentFilePath).toURI().toURL());
      method.invoke(cl, new File(simpleHighlighterFilePath).toURI().toURL());
      // make bundled JB cmake classes visible inside patched IDEA classes
      method.invoke(cl, new File(bundledCmakeFilePath).toURI().toURL());

      //make com.cmakeplugin classes visible inside patched JB Cmake classes
      ClassLoader cl2 = Class.forName("com.jetbrains.cmake.resolve.CMakeElementEvaluator").getClassLoader();
      Method method2 = cl2.getClass().getMethod("addURL", URL.class);
      method2.setAccessible(true);
      method2.invoke(cl2, new File(agentFilePath).toURI().toURL());
      method2.invoke(cl2, new File(simpleHighlighterFilePath).toURI().toURL());

    } catch (IllegalAccessException
        | InvocationTargetException
        | ClassNotFoundException
        | NoSuchMethodException
        | MalformedURLException e) {
      LOGGER.warn("Exception performing reflective operation over class: ", e);
      return;
    }

    LOGGER.info("Loading Java agent: {}", agentFilePath);
    if (AgentLoader.loadAgent(agentFilePath))
      LOGGER.info("Loaded Java agent successfully: {}", agentFilePath);
    else
      LOGGER.warn("Exception loading Java agent: {}", agentFilePath);
  }

  public static <T> T[] concatArrays(T[] first, T[] second) {
    if (first.length == 0) return second;
    if (second.length == 0) return first;
    T[] result = Arrays.copyOf(first, first.length + second.length);
    System.arraycopy(second, 0, result, first.length, second.length);
    return result;
  }

  @Nullable
  public static ItemPresentation getPresentation(PsiElement o, ItemPresentation prevResult) {
    return isClassOfVarDef(o) && prevResult == null
        ? CMakePsiImplUtil.getPresentation(o)
        : prevResult;
  }

  private static boolean isClassOfVarDef(PsiElement o) {
    return o.getClass().getName().equals("com.jetbrains.cmake.psi.CMakeLiteralImpl") &&
            o.getParent() != null &&
            !o.getParent().getFirstChild().textMatches("\"");
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

  public static boolean existReferenceTo(PsiElement cmakeLiteral) {
    return CMakePSITreeSearch.existReferenceTo(cmakeLiteral);
  }

  private static boolean hasCallInStack(String clazz, String method) {
    final StackTraceElement[] st = Thread.currentThread().getStackTrace();
    for (StackTraceElement ste : st) {
      if (ste.getMethodName().equals(method) && ste.getClassName().contains(clazz)) return true;
    }
    return false;
  }
}
