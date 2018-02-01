package com.cmakeplugin.psi.impl;

import com.cmakeplugin.psi.*;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.*;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CMakePsiImplUtil {
  @NotNull
  public static String getName(PsiNameIdentifierOwner o) {
    return ObjectUtils.assertNotNull(o.getNameIdentifier()).getText();
  }

  @NotNull
  public static PsiNameIdentifierOwner setName(PsiNameIdentifierOwner o, String newName) {
    ObjectUtils.assertNotNull(o.getNameIdentifier()).replace(CMakePsiElementFactory.createUnquotedArgumentFromText(o.getProject(), newName));
    return o;
  }

  @NotNull
  public static PsiElement getNameIdentifier(CMakeUnquotedArgumentContainer o) {
    return o.getUnquotedArgument();
  }

  @NotNull
  public static PsiReference getReference(CMakeVariableContainer o) {
    return new PsiReferenceBase<CMakeVariableContainer>(o, TextRange.from(0, o.getTextRange().getLength())) {
      @Nullable
      @Override
      public PsiElement resolve() {
        final String name = getElement().getVariable().getText();
        CommonProcessors.FindFirstProcessor<CMakeUnquotedArgumentContainer> processor =
                new CommonProcessors.FindFirstProcessor<CMakeUnquotedArgumentContainer>() {
                  @Override
                  protected boolean accept(CMakeUnquotedArgumentContainer o) {
                    return Comparing.equal(o.getName(), name);
                  }
                };
        processUnquotedArgumentVariants(getElement(), processor);
        return processor.getFoundValue();
      }

      @NotNull
      @Override
      public Object[] getVariants() {
        CommonProcessors.CollectProcessor<CMakeUnquotedArgumentContainer> processor =
                new CommonProcessors.CollectProcessor<>();
        processUnquotedArgumentVariants(getElement(), processor);
        return ArrayUtil.toObjectArray(processor.getResults());
      }

      @Override
      public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        return getElement().getVariable().replace(CMakePsiElementFactory.createVariableFromText(getElement().getProject(), newElementName));
      }
    };
  }

  private static boolean processUnquotedArgumentVariants(PsiElement context, Processor<CMakeUnquotedArgumentContainer> processor) {
    final PsiFile containingFile = context.getContainingFile();
    List<CMakeUnquotedArgumentContainer> macros = CachedValuesManager.getCachedValue(
            containingFile,
            () -> CachedValueProvider.Result.create(computeElements(containingFile, CMakeUnquotedArgumentContainer.class), containingFile));
    return ContainerUtil.process(macros, processor);
  }

  public static <T> List<T> computeElements(PsiFile psiFile, final Class<T> clazz) {
    final List<T> result = ContainerUtil.newArrayList();
    psiFile.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
      @Override
      public void visitElement(PsiElement element) {
        if (clazz.isInstance(element)) {
          result.add((T)element);
        }
        else
//          if (!(element instanceof JFlexLexicalRulesSection) &&
//                !(element instanceof JFlexUserCodeSection))
        {
          super.visitElement(element);
        }
      }
    });
    return result;
  }

}
