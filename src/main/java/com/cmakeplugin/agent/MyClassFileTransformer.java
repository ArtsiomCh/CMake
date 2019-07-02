package com.cmakeplugin.agent;

import com.intellij.psi.PsiReference;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.stream.Stream;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MyClassFileTransformer implements ClassFileTransformer {

  //  private static Logger LOGGER = LoggerFactory.getLogger(MyClassFileTransformer.class);

  private Class targetClass;
  private String methodName;
  private String srcInsertAfter;

  public MyClassFileTransformer(Class targetClass, String methodName, String srcInsertAfter) {
    this.targetClass = targetClass;
    this.methodName = methodName;
    this.srcInsertAfter = srcInsertAfter;
  }

  public Class getTargetClass() {
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
      // LOGGER.info(
      System.out.println("[Agent] Transforming class: " + className);
      try {
        ClassPool cp = ClassPool.getDefault();
        cp.insertClassPath(new ClassClassPath(targetClass));
        CtClass cc = cp.get(targetClass.getName());
        /*
                String src = "public PsiReference[] getReferences() { "
                    + "return com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry.getReferencesFromProviders(this);"
                    + "}";
                CtMethod m = CtNewMethod.make(src, cc);
                cc.addMethod(m);
        */
        CtMethod m =
            Arrays.stream(cc.getMethods())
                .filter(it -> it.getName().equals(methodName))
                .findFirst()
                .orElseThrow(
                    () -> new NotFoundException(methodName + " method not found in " + className));
        //        m.insertBefore("System.out.println(\"" + m.getName() + " called at \" + $0);");
        m.insertAfter(srcInsertAfter);

        byteCode = cc.toBytecode();
        cc.detach();
        System.out.println("[Agent] Successfully patched class: " + className);
      } catch (NotFoundException | CannotCompileException | IOException e) {
        System.out.println("---------------------------------------------------------------------");
        System.out.println("[Agent] EXCEPTION: " + e);
        System.out.println("---------------------------------------------------------------------");
      }
    }
    return byteCode;
  }
}
