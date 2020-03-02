package com.cmakeplugin.inspections;

import com.cmakeplugin.CMakeComponent;
import com.cmakeplugin.utils.CMakePDC;
import com.cmakeplugin.utils.CMakePSITreeSearch;
import com.cmakeplugin.utils.CMakeVarStringUtil;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

public class VariableUnusedInspection extends LocalInspectionTool {
  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new PsiElementVisitor() {
      @Override
      public void visitElement(PsiElement element) {
        super.visitElement(element);
        if (!CMakeComponent.isCMakePlusActive) return;
        if (!InspectionUtils.isCommandName(element, "set")) return;
        PsiElement commandArguments =
            PsiTreeUtil.getNextSiblingOfType(element, CMakePDC.ARGUMENTS_CLASS);
        PsiElement firstArgument =
            PsiTreeUtil.getChildOfAnyType(commandArguments, CMakePDC.COMMAND_ARGUMENT_CLASSES);
        if (firstArgument == null) return;
        firstArgument = CMakePDC.transformToLiteral(firstArgument);
        if (CMakePDC.isClassOfVarDef(firstArgument)
            && CMakeVarStringUtil.couldBeVarName(firstArgument.getText())
            && !CMakeVarStringUtil.isPredefinedCMakeVar(firstArgument.getText())
            && !CMakePSITreeSearch.existReferenceTo(firstArgument))
          holder.registerProblem(firstArgument, "Variable is set but never used.");
      }
    };
  }
}
