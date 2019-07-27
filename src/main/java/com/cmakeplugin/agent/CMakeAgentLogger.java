package com.cmakeplugin.agent;

import static com.cmakeplugin.agent.CMakeInstrumentationAgent.getLoadedClass;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class CMakeAgentLogger {

  private Object logger;
  private Method warn_method;
  private Method info_method;

  public CMakeAgentLogger(Class<?> loggedClass, Instrumentation inst) {
    try {
      Class<?> classLoggerFactory = getLoadedClass("org.slf4j.LoggerFactory", inst);
      if (classLoggerFactory == null) return;
      Method methodGetLogger = classLoggerFactory.getMethod("getLogger", Class.class);
      logger = methodGetLogger.invoke(classLoggerFactory, loggedClass);
      Class<?> classLogger = getLoadedClass("org.slf4j.Logger", inst);
      if (classLogger == null) return;
      warn_method = classLogger.getMethod("warn", String.class);
      info_method = classLogger.getMethod("info", String.class);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  public void warn(String text) {
    log(warn_method, text);
  }

  public void info(String text) {
    log(info_method, text);
  }

  private void log(Method method, String text) {
    if (logger == null || method == null) {
      System.out.println("Logger not found at [com.cmakeplugin.agent]. Message to log: " + text);
      return;
    }
    try {
      method.invoke(logger, "[Agent] " + text);
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }
}
