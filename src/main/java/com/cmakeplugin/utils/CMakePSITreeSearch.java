package com.cmakeplugin.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.indexing.FileBasedIndex;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static com.cmakeplugin.utils.CMakeIFWHILEcheck.*;
import static com.cmakeplugin.utils.CMakeVarStringUtil.isPossibleVarDefinition;

public class CMakePSITreeSearch {

  /**
   * looking ANY definitions of Variable in Project scope.
   *
   * @param varReference PsiElement to start from
   * @param varName Variable name to looking for
   * @return List of PsiElements with Variable definition or empty List
   */
  @NotNull
  public static List<PsiElement> findVariableDefinitions(
      @NotNull PsiElement varReference, String varName) {
    PsiElementFilter filterVarDef =
        possibleVarDefElement ->
            CMakePDC.getPossibleVarDefClass().isInstance(possibleVarDefElement)
                && varName.equals(possibleVarDefElement.getText())
                && !isVarInsideIFWHILE(possibleVarDefElement);
    return getAllCMakeFiles(varReference).stream()
        .flatMap(cmakeFile -> findChildrenMatched(cmakeFile, filterVarDef).stream())
        .collect(Collectors.toList());
  }

  @NotNull
  private static List<PsiFile> getAllCMakeFiles(@NotNull PsiElement element) {
    Project project = element.getProject();
    PsiFile currentFile = element.getContainingFile();
    List<PsiFile> result =
        FileBasedIndex.getInstance()
            .getContainingFiles(
                FileTypeIndex.NAME,
                CMakePDC.getCmakeFileType(),
                GlobalSearchScope.allScope(project))
            .stream()
            .map(virtualFile -> PsiManager.getInstance(project).findFile(virtualFile))
            .filter(file -> file != currentFile)
            .collect(Collectors.toList());
    result.add(0, currentFile);
    return result;
  }

  /** Copy of {@link PsiTreeUtil#findChildrenOfAnyType(PsiElement, Class[])} */
  @NotNull
  private static <T extends PsiElement> Collection<T> findChildrenMatched(
      @Nullable final PsiElement element, @NotNull final PsiElementFilter filter) {
    if (element == null) return ContainerUtil.emptyList();
    PsiElementProcessor.CollectFilteredElements<T> processor =
        new PsiElementProcessor.CollectFilteredElements<T>(filter);
    PsiTreeUtil.processElements(element, processor);
    return processor.getCollection();
  }

  /**
   * checking ANY reference of Variable in Project scope.
   *
   * @param varDef PsiElement with potential Variable declaration
   * @return True if any reference found, False otherwise
   */
  public static boolean existReferenceTo(@NotNull PsiElement varDef) {
    Predicate<PsiFile> containsVarRefElement =
        cmakeFile -> hasChildMatched(cmakeFile, element -> hasVarRef(element, varDef.getText()));
    return isPossibleVarDefinition(varDef.getText())
        && !isVarInsideIFWHILE(varDef)
        && getAllCMakeFiles(varDef).stream().anyMatch(containsVarRefElement);
  }

  private static boolean hasVarRef(
      @NotNull PsiElement potentialVarRefHolderElement, @NotNull String varDefText) {
    final String elementText = potentialVarRefHolderElement.getText();
    Predicate<TextRange> containsRefToVarDef =
        innerVarRange ->
            elementText
                .substring(innerVarRange.getStartOffset(), innerVarRange.getEndOffset())
                .equals(varDefText);
    return PsiTreeUtil.instanceOf(
            potentialVarRefHolderElement,
            CMakePDC.getUnquotedArgumentClass(),
            CMakePDC.getQuotedArgumentClass())
        && getInnerVars(potentialVarRefHolderElement).stream().anyMatch(containsRefToVarDef);
  }

  /** Copy of {@link PsiTreeUtil#findChildOfType(PsiElement, Class, boolean, Class)} */
  private static <T extends PsiElement> boolean hasChildMatched(
      @Nullable final PsiElement element, @NotNull final PsiElementFilter filter) {
    if (element == null) return false;

    PsiElementProcessor.FindFilteredElement<T> processor =
        new PsiElementProcessor.FindFilteredElement<T>(filter);
    PsiTreeUtil.processElements(element, processor);
    return processor.isFound();
  }

  //  /**
  //   * Copy of {@link PsiTreeUtil#findChildrenOfAnyType(PsiElement, Class[])}
  //   */
  //  @NotNull
  //  private static <T extends PsiElement> Collection<T> findChildrenOfTypeWithText(@Nullable final
  // PsiElement element,
  //                                                                                 @NotNull final
  // String text,
  //                                                                                 @NotNull final
  // Class<? extends T> aClass) {
  //    if (element == null) return ContainerUtil.emptyList();
  //    PsiElementProcessor.CollectElements<T> processor = new
  // PsiElementProcessor.CollectElements<T>() {
  //      @Override
  //      public boolean execute(@NotNull T each) {
  //        if (each == element) return true;
  //        if ( aClass.isInstance(each) && text.equals(each.getText()) ) {
  //          return super.execute(each);
  //        }
  //        return true;
  //      }
  //    };
  //    PsiTreeUtil.processElements(element, processor);
  //    return processor.getCollection();
  //  }

  /*  @NotNull
  private static List<PsiElement> findVarDefsFileScope(@NotNull PsiElement o, String name) {
    List<PsiElement> result = new ArrayList<>();
    PsiElement foundDeclaration = checkUpperSiblings(o.getParent(), name);
    while (foundDeclaration!=null) {
      result.add(foundDeclaration);
      foundDeclaration = (getScopeIfBody(foundDeclaration) == null)
              ? null
              : isSameIfScope(getScopeIfBody(foundDeclaration) , getScopeIfBody(o))
                ? null
                : checkUpperSiblings(foundDeclaration.getParent(), name);
    }
   return result;
  }*/
  /*
  private static boolean isSameIfScope(PsiElement declarationIfBody, PsiElement referenceIfBody){
    while (referenceIfBody != null) {
      if (declarationIfBody == referenceIfBody)
        return true;
      referenceIfBody = getScopeIfBody(referenceIfBody.getParent());
    }
    return false;
  }

  private static PsiElement getScopeIfBody(PsiElement element) {
    while (!(element instanceof PsiFile || element instanceof CMakeFunmacro)) { // stay in current Function scope if any
      if (element instanceof CMakeIfbody)
        return element;
      element = element.getParent();
    }
    return null;
  }

  @Nullable
  private static PsiElement checkUpperSiblings(PsiElement o, String name) {
    PsiElement result=null;
    if (o!=null && !(o instanceof PsiFile)) {
      while (o.getPrevSibling()!=null && !(o instanceof CMakeFunmacro)) { // ignore Function scopes. fixme: nested calls
        result=checkChildrens(o.getPrevSibling(), name);
        if (result!=null)
          break;
        else
          o=o.getPrevSibling();
      }
      result= (result==null) ? checkUpperSiblings(o.getParent(), name): result;
    }
    return result;
  }

  @Nullable
  private static PsiElement checkChildrens(@NotNull PsiElement o, String name) {
    PsiElement result=null, element=o.getLastChild();
    while (element!=null) {
      if (element.getText().equals(name) && element instanceof CMakeUnquotedArgumentContainer) {
        return element;
      } else {
        if (element.getLastChild()!=null) {
          result=checkChildrens(element, name);
          if (result!=null)
            return result;
        }
        element = element.getPrevSibling();
      }
    }
    return result;

  }*/
}
