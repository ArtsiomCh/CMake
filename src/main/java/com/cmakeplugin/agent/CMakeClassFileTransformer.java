package com.cmakeplugin.agent;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

class CMakeClassFileTransformer implements ClassFileTransformer {

//  private static final Logger LOGGER = LoggerFactory.getLogger(CMakeClassFileTransformer.class);
  private static CMakeAgentLogger LOGGER;

  private Class targetClass;
  private String methodName;
  private String srcInsertAfter;
  private String[] imports;

  CMakeClassFileTransformer(Class targetClass, String methodName, String srcInsertAfter, Instrumentation inst) {
    this(targetClass, new String[0], methodName, srcInsertAfter, inst);
  }

  CMakeClassFileTransformer(Class targetClass, String[] imports, String methodName, String srcInsertAfter, Instrumentation inst) {
    this.targetClass = targetClass;
    this.imports = imports;
    this.methodName = methodName;
    this.srcInsertAfter = srcInsertAfter;
    LOGGER = new CMakeAgentLogger(CMakeClassFileTransformer.class, inst);
  }

  Class getTargetClass() {
    return targetClass;
  }

  @Override
  public byte[] transform(
      ClassLoader loader,
      String className,
      Class<?> classBeingRedefined,
      ProtectionDomain protectionDomain,
      byte[] classfileBuffer)
      throws IllegalClassFormatException {
    byte[] byteCode = null;

    if (classBeingRedefined.equals(targetClass)) {
      try {
        ClassPool cp = ClassPool.getDefault();
        cp.insertClassPath(new ClassClassPath(targetClass));
        for (String importStr : imports) {
          cp.importPackage(importStr);
        }
        CtClass cc = cp.get(targetClass.getName());
        CtMethod m = cc.getDeclaredMethod(methodName);
        //        m.insertBefore("System.out.println(\"" + m.getName() + " called at \" + $0);");
        m.insertAfter(srcInsertAfter);

        byteCode = cc.toBytecode();
        cc.detach();
        LOGGER.info("Successfully transformed class: " + className);
      } catch (NotFoundException | CannotCompileException | IOException e) {
        LOGGER.warn("EXCEPTION transforming class: " + className + e);
      }
    }
    return byteCode;
  }
}
