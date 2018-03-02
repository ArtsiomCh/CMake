package com.cmakeplugin.utils;

import com.cmakeplugin.CMakeFileType;
import com.cmakeplugin.psi.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CMakePSITreeSearch {

  /**
   * looking ANY definitions of Variable in Directory scope.
   * @param o PsiElement to start from
   * @param name Variable name to looking for
   * @return List of PsiElements with Variable definition or empty List
   */
  @NotNull
  public static List<PsiElement> findVariableDefinitions(@NotNull PsiElement o, String name) {
    List<PsiElement> result = //findVarDefsFileScope(o, name);
//    result.addAll
            ( findVarDefsAtDirScope(o, name) );
    return result;
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

  @NotNull
  private static List<PsiElement> findVarDefsAtDirScope(@NotNull PsiElement varElement, String varName) {
    Project project = varElement.getProject();
    List<PsiElement> result = new ArrayList<>();
    Collection<VirtualFile> virtualFiles =
            FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, CMakeFileType.INSTANCE,
                    GlobalSearchScope.allScope(project)); // fixme: Directory Scope needed
    for (VirtualFile virtualFile : virtualFiles) {
      CMakeFile cmakeFile = (CMakeFile) PsiManager.getInstance(project).findFile(virtualFile);
      if (cmakeFile != null) {// && varElement.getContainingFile() != cmakeFile) { // Exclude current file
        Collection<PsiElement> unquotedArgs = PsiTreeUtil.findChildrenOfType(cmakeFile, CMakeUnquotedArgumentContainer.class);
        for (PsiElement unquotedArg : unquotedArgs) {
          if (varName.equals(unquotedArg.getText())) {// && PsiTreeUtil.getParentOfType(unquotedArg, CMakeFunmacro.class) == null) { // exclude Function's scopes
            result.add(unquotedArg);
          }
        }
      }
    }
    return result;
  }
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
