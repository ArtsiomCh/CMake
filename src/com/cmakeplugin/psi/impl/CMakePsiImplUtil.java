package com.cmakeplugin.psi.impl;

import com.cmakeplugin.psi.*;

import static com.cmakeplugin.utils.CMakePSITreeSearch.*;
import static com.cmakeplugin.utils.CMakeVariablesUtil.*;

import com.intellij.lang.Language;
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
  public static String getName(CMakeUnquotedArgumentContainer o) {
    return ObjectUtils.assertNotNull(o.getUnquotedArgument()).getText();
  }

  @NotNull
  public static CMakeUnquotedArgumentContainer setName(CMakeUnquotedArgumentContainer o, String newName) {
    ObjectUtils.assertNotNull(o.getUnquotedArgument())
            .replace(CMakePsiElementFactory.createUnquotedArgumentFromText(o.getProject(), newName));
    return o;
  }

  @NotNull
  public static PsiElement getNameIdentifier(CMakeUnquotedArgumentContainer o) {
    return o.getUnquotedArgument();
  }

  @Nullable
  public static PsiReference findReferenceAt(PsiElement thisElement, int offset) {
    return null;
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

  private static class MyPsiPolyVariantReferenceBase <T extends PsiElement> extends PsiPolyVariantReferenceBase {
    /**
     * @param element PSI element
     * @param rangeInElement range relatively to the element's start offset
     */
    MyPsiPolyVariantReferenceBase(T element, TextRange rangeInElement) {
      super(element, rangeInElement);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
      return PsiElementResolveResult.createResults(
              findVariableDefinitions(getElement(), getValue()) );
    }

    @NotNull
    @Override
    public Object[] getVariants() {
      // TODO
//      CommonProcessors.CollectProcessor<CMakeUnquotedArgumentContainer> processor =
//              new CommonProcessors.CollectProcessor<>();
//      processUnquotedArgumentVariants(getElement(), processor);
//      return ArrayUtil.toObjectArray(processor.getResults());
      return EMPTY_ARRAY;
    }

//      @Override
//      public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
//        return getElement().getVariable().replace(CMakePsiElementFactory.createVariableFromText(getElement().getProject(), newElementName));
//      }

  }

  @NotNull
  public static PsiReference[] getReferences(PsiElement o) {
    List<TextRange> innerVars = getInnerVars(o.getText());
    PsiReference[] result = new PsiReference[innerVars.size()];// +1 ];
    for (int i=0; i<innerVars.size(); i++) {
      TextRange innerVar = innerVars.get(i);
      result[i] = new MyPsiPolyVariantReferenceBase<>(o, innerVar);
    }
//      result[result.length-1]= new PsiReferenceBase<PsiElement>(o,  TextRange.from(0, o.getTextRange().getLength()) ) {
//        @Nullable
//        @Override
//        public PsiElement resolve() {
//          return getElement();
//        }
//
//        @NotNull
//        @Override
//        public Object[] getVariants() {
//          return new Object[]{};
//        }
//      };
    return result;
  }

//  @NotNull
//  public static PsiReference[] getReferences(CMakeQuotedArgumentContainer o) {
//    String text = o.getText().substring( 1 , o.getText().length()-1 ); // fixme
//    List<TextRange> innerVars = getInnerVars(text);
//    PsiReference[] result = new PsiReference[innerVars.size()];
//    for (int i=0; i<innerVars.size(); i++) {
//      TextRange innerVar = innerVars.get(i);
//      result[i] = new MyPsiPolyVariantReferenceBase<>(o, innerVar.shiftRight(1));
//    }
//    return result;
//  }

  private static boolean processUnquotedArgumentVariants(PsiElement context, Processor<CMakeUnquotedArgumentContainer> processor) {
    final PsiFile containingFile = context.getContainingFile();
    List<CMakeUnquotedArgumentContainer> UnquotedArguments = CachedValuesManager.getCachedValue(
            containingFile,
            () -> CachedValueProvider.Result.create(computeElements(containingFile, CMakeUnquotedArgumentContainer.class), containingFile));
    return ContainerUtil.process(UnquotedArguments, processor);
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
