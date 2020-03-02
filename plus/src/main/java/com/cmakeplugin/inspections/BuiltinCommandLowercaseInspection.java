package com.cmakeplugin.inspections;

import com.cmakeplugin.CMakeComponent;
import com.cmakeplugin.utils.CMakePlusPDC;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class BuiltinCommandLowercaseInspection extends LocalInspectionTool {

  private final BuiltinCommandLowercaseQuickFix myQuickFix = new BuiltinCommandLowercaseQuickFix();

  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new PsiElementVisitor() {
      @Override
      public void visitElement(PsiElement element) {
        super.visitElement(element);
        if (!CMakeComponent.isCMakePlusActive) return;
        if (InspectionUtils.isBuiltinCommand(element)) {
          String commandName = element.getText();
          if (!commandName.toLowerCase().equals(commandName))
            holder.registerProblem(element, "Builtin commands should be used in lowercase.", myQuickFix);
        }
      }
    };
  }

  private static class BuiltinCommandLowercaseQuickFix implements LocalQuickFix {
    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
      return "Convert command name to lowercase";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
      PsiElement commandName = descriptor.getPsiElement();
      String nameLowercased = commandName.getText().toLowerCase();
      commandName.replace(CMakePlusPDC.createCommandName(project, nameLowercased));
    }
  }

}
