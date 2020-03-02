package com.cmakeplugin.inspections;

import com.cmakeplugin.CMakeComponent;
import com.cmakeplugin.utils.CMakePDC;
import com.cmakeplugin.utils.CMakePlusPDC;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class EndOfBlockCommandArgumentsInspection extends LocalInspectionTool {

  private final MyQuickFix myQuickFix = new MyQuickFix();

  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new PsiElementVisitor() {
      @Override
      public void visitElement(PsiElement element) {
        super.visitElement(element);
        if (!CMakeComponent.isCMakePlusActive) return;
        if (CMakePlusPDC.END_OF_COMMAND_KEYWORD_ELEMENT_TYPES.contains(
            element.getNode().getElementType())) {
          PsiElement arguments = PsiTreeUtil.getChildOfType(element, CMakePDC.ARGUMENTS_CLASS);
          if (arguments != null && !arguments.getText().replace(" ", "").equals("()"))
            holder.registerProblem(
                arguments,
                "Commands else, endif, endforeach, endwhile, endfunction, endmacro should not take arguments.",
                myQuickFix);
        }
      }
    };
  }

  private static class MyQuickFix implements LocalQuickFix {

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
      return "Convert to empty arguments list";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
      PsiElement arguments = descriptor.getPsiElement();
      assert arguments != null;
      // Can't replace arguments element due to extra whitespace insertion in CLion (bug?)
//      arguments.replace(CMakePlusPDC.createEmptyArguments(project));
      for (PsiElement child : arguments.getChildren()) {
        if (child.getPrevSibling() == null || child.getNextSibling() == null) continue;
        child.delete();
      }
    }
  }
}
