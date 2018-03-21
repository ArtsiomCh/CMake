package com.cmakeplugin.utils;

import com.cmakeplugin.CMakeFileType;
import com.cmakeplugin.psi.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.cmakeplugin.psi.CMakePsiElementFactory.createVariableDeclarationFromText;
import static com.cmakeplugin.utils.CMakePlatformIndependentProxy.*;

public class CMakePSITreeSearch {

  /**
   * looking ANY definitions of Variable in Directory scope.
   * @param varReference PsiElement to start from
   * @param varName Variable name to looking for
   * @return List of PsiElements with Variable definition or empty List
   */
  @NotNull
  public static List<PsiElement> findVariableDefinitions(@NotNull PsiElement varReference, String varName) {
    Project project = varReference.getProject();
    List<PsiElement> result = new ArrayList<>();
    Collection<VirtualFile> virtualFiles =
            FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, CMakeFileType.INSTANCE,
                    GlobalSearchScope.allScope(project));
    for (VirtualFile virtualFile : virtualFiles) {
      CMakeFile cmakeFile = (CMakeFile) PsiManager.getInstance(project).findFile(virtualFile);
      if (cmakeFile != null // && varReference.getContainingFile() != cmakeFile) // Exclude current file
             // && cmakeFile.getContainingDirectory()==varReference.getContainingFile().getContainingDirectory()// Directory Scope
         ) {
        for (CMakeUnquotedArgumentContainer varDefinition :
                findChildrenOfTypeWithText(cmakeFile, varName, CMakeUnquotedArgumentContainer.class)) {
// && PsiTreeUtil.getParentOfType(varDefinition, CMakeFunmacro.class) == null) { // exclude Function's scopes
          if (!isVarInsideIFWHILE(PLATFORM.IDEA, varDefinition)) {

            ApplicationManager.getApplication().invokeLater(() -> {
              WriteCommandAction.runWriteCommandAction(project, () -> {
                varDefinition.replace(createVariableDeclarationFromText(project, varName));
              });
            });

          }
        }
        result.addAll( findChildrenOfTypeWithText(cmakeFile, varName, CMakeVariableDeclaration.class) );
      }
    }
    return result;
  }

  /**
   * Copy of {@link PsiTreeUtil#findChildrenOfAnyType(PsiElement, Class[])}
   */
  @NotNull
  private static <T extends PsiElement> Collection<T> findChildrenOfTypeWithText(@Nullable final PsiElement element,
                                                                                 @NotNull final String text,
                                                                                 @NotNull final Class<? extends T> aClass) {
    if (element == null) return ContainerUtil.emptyList();
    PsiElementProcessor.CollectElements<T> processor = new PsiElementProcessor.CollectElements<T>() {
      @Override
      public boolean execute(@NotNull T each) {
        if (each == element) return true;
        if ( aClass.isInstance(each) && text.equals(each.getText()) ) {
          return super.execute(each);
        }
        return true;
      }
    };
    PsiTreeUtil.processElements(element, processor);
    return processor.getCollection();
  }

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
