package com.cmakeplugin.psi.impl;

import com.cmakeplugin.CMakeComponent;
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
import java.io.File;
import java.util.List;

import static com.cmakeplugin.utils.CMakeIFWHILEcheck.*;
import static com.cmakeplugin.utils.CMakePDC.*;

public class CMakePsiImplUtil {

  public static String getName(PsiElement o) {
    return o.getText();
  }

  @NotNull
  public static CMakeUnquotedArgumentMaybeVariableContainer setName(CMakeUnquotedArgumentMaybeVariableContainer o, String newName) {
    ObjectUtils.assertNotNull(o.getUnquotedArgumentMaybeVarDef())
            .replace(CMakePsiElementFactory.createArgumentFromText(o, newName, CMakeUnquotedArgumentMaybeVariableContainer.class));
    return o;
  }

  @Nullable
  public static PsiElement getNameIdentifier(CMakeUnquotedArgumentMaybeVariableContainer o) {
    return (CMakeComponent.isCMakePlusActive && CMakePSITreeSearch.existReferenceTo(o))
        ? o.getUnquotedArgumentMaybeVarDef()
        : null;
  }

  @Nullable
  public static ItemPresentation getPresentation(PsiElement o) {
    return new ItemPresentation() {

      @Override
      @Nullable
      public String getPresentableText() {
        PsiFile file = o.getContainingFile();
        Document document =  file.getViewProvider().getDocument();
        if ((document == null))  return o.getText();

        final String filePath = file.getContainingDirectory() + File.separator + file.getName();
        final String shortFilePath = (filePath.length() > 30) ? filePath.substring(filePath.length() - 30) : filePath;
        final int lineNumber = document.getLineNumber(o.getTextOffset());
        final TextRange lineRange = new TextRange(
                document.getLineStartOffset(lineNumber),
                document.getLineEndOffset(lineNumber));
        final String lineText = document.getText(lineRange).replaceAll(" {2,}"," ");

        return String.format("%30.30s:%4d  %s", shortFilePath, lineNumber, lineText );
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

    MyPsiPolyVariantReferenceBase(T element, TextRange rangeInElement) {
      super(element, rangeInElement);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
//        final String name = getValue();
//        CommonProcessors.FindFirstProcessor<CMakeUnquotedArgumentContainer> processor =
//                new CommonProcessors.FindFirstProcessor<CMakeUnquotedArgumentContainer>() {
//                  @Override
//                  protected boolean accept(CMakeUnquotedArgumentContainer o) {
//                    return Comparing.equal(o.getName(), name);
//                  }
//                };
//        processUnquotedArgumentVariants(getElement(), processor);
//        return PsiElementResolveResult.createResults(
//                processor.getFoundValue());
      return PsiElementResolveResult.createResults(
              CMakePSITreeSearch.findVariableDefinitions(getElement(), getValue()) );
    }

    @NotNull
    @Override
    public Object[] getVariants() {
      // TODO
//      CommonProcessors.CollectProcessor<CMakeUnquotedArgumentContainer> processor =
//              new CommonProcessors.CollectProcessor<>();
//      processUnquotedArgumentVariants(getElement(), processor);
//      return ArrayUtil.toObjectArray(processor.getResults());

      //return CMakePSITreeSearch.getAllPossibleVarDefs(getElement()).toArray();
      return EMPTY_ARRAY;
    }

      @Override
      public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        if (getElement() instanceof CMakeUnquotedArgumentContainer) {
          return getElement().getFirstChild().replace( CMakePsiElementFactory.createArgumentFromText(getElement(),
                    getRangeInElement().replace( getElement().getText(), newElementName),
                    CMakeUnquotedArgumentContainer.class));
        } else if (getElement() instanceof CMakeQuotedArgumentContainer) {
          return getElement().getFirstChild().replace( CMakePsiElementFactory.createArgumentFromText(getElement(),
                    "\"" + getRangeInElement().replace( getElement().getText(), newElementName) + "\"",
                    CMakeQuotedArgumentContainer.class));
        } else throw new IncorrectOperationException("Unknown type of Argument to replace: " + getElement().getClass());
      }

  }

  @NotNull
  public static PsiReference[] getReferences(PsiElement o) {
    if (!CMakeComponent.isCMakePlusActive) return PsiReference.EMPTY_ARRAY;
    List<TextRange> innerVars = getInnerVars(o);
    if (innerVars.isEmpty()) return PsiReference.EMPTY_ARRAY;
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
