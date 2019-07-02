package com.cmakeplugin.agent;

import java.lang.instrument.Instrumentation;

public class CMakeInstrumentationAgent {

  public static final String CLASS_TO_TRANSFORM_REFERENCES =
      "com.jetbrains.cmake.psi.CMakeLiteralImplMixin";
  public static final String CLASS_TO_TRANSFORM_PRESENTATION =
      "com.intellij.psi.impl.PsiElementBase";
  public static final String CLASS_TO_TRANSFORM_RESOLVE =
      "com.jetbrains.cmake.resolve.CMakeElementEvaluator";

  //  private static Logger LOGGER = LoggerFactory.getLogger(CMakeInstrumentationAgent.class);

  public static void agentmain(String agentArgs, Instrumentation inst) {
    System.out.println("[Agent] In agentmain method");

    String srcInsertAfter =
        "{ "
            /*
                            + "System.out.println(\"" + m.getName() + " called at \" + $0);"
                            + "System.out.println(\"original result: \" + first);"
                            + "System.out.println(\"new result: \" + result);"
            */
            /*+ "Class klass = com.jetbrains.cmake.psi.CMakeLiteralImplMixin.class;"
            + "String location = klass.getResource('/' + klass.getName().replace('.', '/') + \".class\").toString();"
            + "System.out.println(\"CMakeLiteralImplMixin found at: \" + location);"*/
            + "$_ = ($r) com.cmakeplugin.agent.CMakeInstrumentationUtils.concatArrays( "
            + "  ($r)$_ ,"
            + "  com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry.getReferencesFromProviders(this));"
            + "}";
    transformClass(
        new MyClassFileTransformer(
            getLoadedClass(CLASS_TO_TRANSFORM_REFERENCES, inst),
            "getReferences",
            srcInsertAfter),
        inst);

    srcInsertAfter =
        "{ "
            + "$_ = ($r) com.cmakeplugin.agent.CMakeInstrumentationUtils.getPresentation( this, ($r)$_ );"
            + "}";
    transformClass(
        new MyClassFileTransformer(
            getLoadedClass(CLASS_TO_TRANSFORM_PRESENTATION, inst),
            "getPresentation",
            srcInsertAfter),
        inst);

    srcInsertAfter =
        "{ "
            + "if (!$_ && "
            + "  (parent instanceof com.jetbrains.cmake.psi.CMakeLiteral) && "
            + "  com.cmakeplugin.utils.CMakePSITreeSearch.existReferenceTo(parent)"
            + ") $_ = true;"
            + "}";
    transformClass(
        new MyClassFileTransformer(
            getLoadedClass(CLASS_TO_TRANSFORM_RESOLVE, inst),
            "isAcceptableNamedParent",
            srcInsertAfter),
        inst);
  }

  private static void transformClass(MyClassFileTransformer cft, Instrumentation inst) {

    inst.addTransformer(cft, true);
    try {
      inst.retransformClasses(cft.getTargetClass());
    } catch (Exception ex) {
      throw new RuntimeException(
          "[Agent] Transformation failed for class: [" + cft.getTargetClass().getName() + "]", ex);
    }
  }

  private static Class<?> getLoadedClass(String className, Instrumentation inst) {
    for (Class<?> clazz : inst.getAllLoadedClasses()) {
      if (clazz.getName().equals(className)) {
        System.out.println(
            "[Agent] class found: "
                + clazz.getName()
                + " with ClassLoader: "
                + clazz.getClassLoader());
        return clazz;
      }
    }
    System.out.println("[Agent] Failed to find class [" + className + "]");
    return null;
  }
}
