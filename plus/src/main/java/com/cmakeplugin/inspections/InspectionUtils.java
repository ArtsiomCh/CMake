package com.cmakeplugin.inspections;

import com.cmakeplugin.CMakeKeywords;
import com.cmakeplugin.utils.CMakePDC;
import com.intellij.psi.PsiElement;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InspectionUtils {

  public static boolean isCommandName(PsiElement element, String commandName) {
    return CMakePDC.COMMAND_NAME_CLASS.isInstance(element)
        && element.getText().toLowerCase().equals(commandName);
  }

  public static boolean isBuiltinCommand(PsiElement element) {
    return CMakePDC.COMMAND_NAME_CLASS.isInstance(element)
        && CMakeKeywords.isCommand(element.getText());
  }

  private static final Map<String, List<String>> mapCommandToSignatures = new ConcurrentHashMap<>();

  public static boolean checkSignature(PsiElement arguments, String commandName) {
    return mapCommandToSignatures.getOrDefault(commandName, Collections.emptyList()).stream()
            .anyMatch(signature -> signatureMatch(arguments, signature));
  }

  private static boolean signatureMatch(PsiElement arguments, String signature) {
    return false;
  }

  static {
    mapCommandToSignatures.put(
        "set",
        Arrays.asList(
            "<variable> <value>... [PARENT_SCOPE]",
            "<variable> <value>... CACHE <type> <docstring> [FORCE]",
            "ENV{<variable>} [<value>]"));
  }
}
