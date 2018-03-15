package com.cmakeplugin.psi.impl;

import com.cmakeplugin.psi.*;

import com.cmakeplugin.utils.CMakePSITreeSearch;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.*;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

import static com.cmakeplugin.utils.CMakePlatformIndependentProxy.*;

public class CMakePsiImplUtil {
  // fixme
  public static final String FAKE_COMMAND_NAME_FOR_VAR_DECLARATION_CREATION="FAKE_COMMAND_NAME_FOR_VAR_DECLARATION_CREATION_1234567890";

  @NotNull
  public static String getName(CMakeVariableDeclaration o) {
    return ObjectUtils.assertNotNull(o.getUnquotedArgument()).getText();
  }

  @NotNull
  public static CMakeVariableDeclaration setName(CMakeVariableDeclaration o, String newName) {
    ObjectUtils.assertNotNull(o.getUnquotedArgument())
            .replace(CMakePsiElementFactory.createUnquotedArgumentFromText(o.getProject(), newName));
    return o;
  }

  @NotNull
  public static PsiElement getNameIdentifier(CMakeVariableDeclaration o) {
    return o.getUnquotedArgument();
  }

  @Nullable
  public static ItemPresentation getPresentation(CMakeVariableDeclaration o) {
    return new ItemPresentation() {

      @Override
      @Nullable
      public String getPresentableText() {
        PsiElement argumentsElement = PsiTreeUtil.getParentOfType(o, CMakeArguments.class);
        Document document =  o.getContainingFile().getViewProvider().getDocument();
        return (argumentsElement!=null && argumentsElement.getParent()!=null && document!=null)
                ? String.format("%s:%4d  %s",
                                o.getContainingFile().getName(),
                                document.getLineNumber(o.getTextOffset()) + 1,
                                argumentsElement.getParent().getText() )
                : o.getText();
      }

      @Override
      public String getLocationString() {
        return null;
      }

      @Override
      public Icon getIcon(boolean open) {
        return null;//CMakeIcons.FILE;
      }
    };
  }
//    @NotNull
//  public static PsiReference getReference(CMakeVariableContainer o) {
//    return new PsiReferenceBase<CMakeVariableContainer>(o, TextRange.from(0, o.getTextRange().getLength())) {
//      @Nullable
//      @Override
//      public PsiElement resolve() {
//        final String name = getElement().getVariable().getText();
//        CommonProcessors.FindFirstProcessor<CMakeUnquotedArgumentContainer> processor =
//                new CommonProcessors.FindFirstProcessor<CMakeUnquotedArgumentContainer>() {
//                  @Override
//                  protected boolean accept(CMakeUnquotedArgumentContainer o) {
//                    return Comparing.equal(o.getName(), name);
//                  }
//                };
//        processUnquotedArgumentVariants(getElement(), processor);
//        return processor.getFoundValue();
//      }
//
//      @NotNull
//      @Override
//      public Object[] getVariants() {
//        CommonProcessors.CollectProcessor<CMakeUnquotedArgumentContainer> processor =
//                new CommonProcessors.CollectProcessor<>();
//        processUnquotedArgumentVariants(getElement(), processor);
//        return ArrayUtil.toObjectArray(processor.getResults());
//      }
//
//      @Override
//      public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
//        return getElement().getVariable().replace(CMakePsiElementFactory.createVariableFromText(getElement().getProject(), newElementName));
//      }
//    };
//  }

  private static class MyPsiPolyVariantReferenceBase <T extends PsiElement> extends PsiPolyVariantReferenceBase {
    /**
     * @param element PSI element
     * @param rangeInElement range relatively to the element's start offset
     */
//    private TextRange myOwnRangeInElement;

    MyPsiPolyVariantReferenceBase(T element, TextRange rangeInElement) {
      super(element, rangeInElement);
//      myOwnRangeInElement = rangeInElement;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
      return PsiElementResolveResult.createResults(
              CMakePSITreeSearch.findVariableDefinitions(getElement(), getValue()) ); //myElement, myOwnRangeInElement.substring(myElement.getText())
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
// fixme
    List<TextRange> innerVars = getPIInnerVars(PLATFORM.IDEA, o);
    PsiReference[] result = new PsiReference[innerVars.size()];
    for (int i=0; i<innerVars.size(); i++) {
      TextRange innerVar = innerVars.get(i);
      result[i] = new MyPsiPolyVariantReferenceBase<>(o, innerVar);
    }
    return result;
  }

  private static boolean processUnquotedArgumentVariants(PsiElement context, Processor<CMakeUnquotedArgumentContainer> processor) {
    final PsiFile containingFile = context.getContainingFile();
    List<CMakeUnquotedArgumentContainer> UnquotedArguments = CachedValuesManager.getCachedValue(
            containingFile,
            () -> CachedValueProvider.Result.create(computeElementsOfClass(containingFile, CMakeUnquotedArgumentContainer.class), containingFile));
    return ContainerUtil.process(UnquotedArguments, processor);
  }

  public static <T> List<T> computeElementsOfClass(PsiFile psiFile, final Class<T> clazz) {
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
