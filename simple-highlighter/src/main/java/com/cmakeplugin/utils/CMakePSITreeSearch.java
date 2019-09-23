package com.cmakeplugin.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeAnyChangeAbstractAdapter;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.Nullable;

import static com.cmakeplugin.utils.CMakeIFWHILEcheck.*;
import static com.cmakeplugin.utils.CMakePDC.ARGUMENTS_CLASS;
import static com.cmakeplugin.utils.CMakePDC.ARGUMENT_CLASSES;

public class CMakePSITreeSearch {

  private static Set<Project> projects = ConcurrentHashMap.newKeySet();

  /** Add File Listener to clear caches for file if it was changed. */
  private static void addFileListener(@NotNull final Project project) {
    if (!projects.contains(project)) {
      PsiManager.getInstance(project)
          .addPsiTreeChangeListener(
              new PsiTreeAnyChangeAbstractAdapter() {
                @Override
                protected void onChange(@Nullable PsiFile file) {
                  if (file != null) {
                    mapFilesToAllPossibleVarDefs.remove(file);
                    mapFilesToVarNameToVarDefs.remove(file);
                    mapFilesToAllVarRefs.remove(file);
                    mapFilesToAllCommandDefs.remove(file);
                  }
                }
              });
      projects.add(project);
    }
  }

  /** Copy of {@link PsiTreeUtil#findChildrenOfAnyType(PsiElement, Class[])} */
  @NotNull
  private static Collection<PsiElement> findChildrenByFilter(
      @NotNull final PsiFile cmakeFile, @NotNull final PsiElementFilter filter) {
    PsiElementProcessor.CollectFilteredElements<PsiElement> processor =
        new PsiElementProcessor.CollectFilteredElements<>(filter);
    PsiTreeUtil.processElements(cmakeFile, processor);
    return processor.getCollection();
  }

  @NotNull
  private static Collection<PsiFile> getCmakeFiles(@NotNull PsiElement element) {
    Project project = element.getProject();
    Collection<VirtualFile> virtualFiles =
        FileTypeIndex.getFiles(CMakePDC.getCmakeFileType(), GlobalSearchScope.allScope(project));
    virtualFiles.add(element.getContainingFile().getVirtualFile());
    addFileListener(project);
    return PsiUtilCore.toPsiFiles(PsiManager.getInstance(project), virtualFiles);
    //        virtualFiles.stream().map(psiManager::findFile).collect(Collectors.toSet());
  }

  /**
   * looking ANY definitions of Variable in Project scope including current file.
   *
   * @param varReference PsiElement to start from
   * @param varName Variable name to looking for
   * @return List of PsiElements with Variable definition or empty List
   */
  @NotNull
  public static List<PsiElement> findVariableDefinitions(
      @NotNull PsiElement varReference, final String varName) {
    List<PsiElement> result = new ArrayList<>();
    for (PsiFile cmakeFile : getCmakeFiles(varReference)) {
      result.addAll(findVarDefs(cmakeFile, varName));
    }
    return result;
  }

  private static Map<PsiFile, Map<String, Collection<PsiElement>>> mapFilesToVarNameToVarDefs =
      new ConcurrentHashMap<>();

  @NotNull
  private static Collection<PsiElement> findVarDefs(
      @NotNull final PsiFile cmakeFile, final String varName) {
    return mapFilesToVarNameToVarDefs
        .computeIfAbsent(cmakeFile, keyF -> new ConcurrentHashMap<>())
        .computeIfAbsent(varName, keyN -> doFindVarNameInVarDefs(cmakeFile, keyN));
  }

  private static Map<PsiFile, Collection<PsiElement>> mapFilesToAllPossibleVarDefs =
      new ConcurrentHashMap<>();

  @NotNull
  private static Collection<PsiElement> doFindVarNameInVarDefs(
      @NotNull final PsiFile cmakeFile, final String varName) {
    return mapFilesToAllPossibleVarDefs
        .computeIfAbsent(
            cmakeFile, keyFile -> findChildrenByFilter(keyFile, CMakeIFWHILEcheck::couldBeVarDef))
        .stream()
        .filter(element -> element.textMatches(varName))
        .collect(Collectors.toList());
  }

  /**
   * checking ANY reference of Variable in Project scope including current file.
   *
   * @param varDef PsiElement with potential Variable declaration
   * @return True if any reference found, False otherwise
   */
  public static boolean existReferenceTo(@NotNull PsiElement varDef) {
    if (!couldBeVarDef(varDef)) return false;
    final String varDefText = varDef.getText();
    for (PsiFile cmakeFile : getCmakeFiles(varDef)) {
      if (hasVarRefToVarName(cmakeFile, varDefText)) return true;
    }
    return false;
  }

  private static Map<PsiFile, Map<String, Collection<PsiElement>>> mapFilesToAllVarRefs =
      new ConcurrentHashMap<>();

  private static boolean hasVarRefToVarName(PsiFile psiFile, String varName) {
    return mapFilesToAllVarRefs
        .computeIfAbsent(psiFile, CMakePSITreeSearch::createVarRefsForFileMap)
        .containsKey(varName);
  }

  private static Map<String, Collection<PsiElement>> createVarRefsForFileMap(PsiFile psiFile) {
    Map<String, Collection<PsiElement>> mapVarRefsToElements = new ConcurrentHashMap<>();
    for (PsiElement element : findChildrenByFilter(psiFile, CMakePDC::classCanHoldVarRef)) {
      for (String varRef : getAllVarRefs(element)) {
        mapVarRefsToElements.computeIfAbsent(varRef, keyVar -> new HashSet<>()).add(element);
      }
    }
    return mapVarRefsToElements;
  }

  @NotNull
  private static Collection<String> getAllVarRefs(@NotNull final PsiElement element) {
    return getInnerVars(element).stream()
        .map(range -> element.getText().substring(range.getStartOffset(), range.getEndOffset()))
        .collect(Collectors.toList());
  }

  /**
   * checking ANY definition of Variable in Project scope including current file.
   *
   * @param varRef PsiElement with Variable reference
   * @param varName name of Variable
   * @return True if any definition found, False otherwise
   */
  public static boolean existDefinitionOf(@NotNull PsiElement varRef, String varName) {
    for (PsiFile cmakeFile : getCmakeFiles(varRef)) {
      if (!findVarDefs(cmakeFile, varName).isEmpty()) return true;
    }
    return false;
  }

  /**
   * checking ANY definition of Function in Project scope including current file.
   *
   * @param command PsiElement with Function reference
   * @return True if any definition found, False otherwise
   */
  public static boolean existFunctionDefFor(@NotNull PsiElement command) {
    return existCommandDefFor(command, CMakePDC.FUNCTION_CLASS);
  }

  /**
   * checking ANY definition of Macros in Project scope including current file.
   *
   * @param command PsiElement with Macros reference
   * @return True if any definition found, False otherwise
   */
  public static boolean existMacroDefFor(@NotNull PsiElement command) {
    return existCommandDefFor(command, CMakePDC.MACRO_CLASS);
  }

  private static Map<PsiFile, Map<String, PsiElement>> mapFilesToAllCommandDefs =
      new ConcurrentHashMap<>();

  private static boolean existCommandDefFor(
      @NotNull PsiElement commandRef, final Class<? extends PsiElement> clazz) {
    final String name = commandRef.getText().toLowerCase();
    PsiElement commandDef;
    for (PsiFile cmakeFile : getCmakeFiles(commandRef)) {
      if ((commandDef = getCommandDef(cmakeFile, name)) != null && clazz.isInstance(commandDef))
        return true;
    }
    return false;
  }

  private static PsiElement getCommandDef(PsiFile psiFile, String commandName) {
    return mapFilesToAllCommandDefs
        .computeIfAbsent(psiFile, CMakePSITreeSearch::createCommandDefsForFileMap)
        .get(commandName);
  }

  private static Map<String, PsiElement> createCommandDefsForFileMap(PsiFile psiFile) {
    Map<String, PsiElement> mapCommandDefsToElement = new ConcurrentHashMap<>();
    final PsiElementFilter isFunMacroDefFilter =
        element -> PsiTreeUtil.instanceOf(element, CMakePDC.FUNCTION_CLASS, CMakePDC.MACRO_CLASS);
    for (PsiElement element : findChildrenByFilter(psiFile, isFunMacroDefFilter)) {
      mapCommandDefsToElement.put(getFunMacroName(element).toLowerCase(), element);
    }
    return mapCommandDefsToElement;
  }

  /** ----------------------------------------------------------------------- */
  public static String getFunMacroName(PsiElement element) {
    PsiElement name = getFunMacroNameElement(element);
    return name != null ? name.getText() : element.getText();
  }

  public static NavigatablePsiElement getFunMacroNameElement(PsiElement element) {
    PsiElement arguments = PsiTreeUtil.findChildOfType(element, ARGUMENTS_CLASS);
    PsiElement name = PsiTreeUtil.findChildOfAnyType(arguments, ARGUMENT_CLASSES);
    return (name instanceof NavigatablePsiElement) ? (NavigatablePsiElement) name : null;
  }

  public static String getFunMacroArgs(PsiElement element) {
    PsiElement arguments = PsiTreeUtil.findChildOfType(element, ARGUMENTS_CLASS);
    return PsiTreeUtil.findChildrenOfAnyType(arguments, ARGUMENT_CLASSES).stream()
        .skip(1) // fun/macro name
        .map(PsiElement::getText)
        .collect(Collectors.joining(" "));
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
  }

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
