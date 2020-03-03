package com.cmakeplugin.utils;

import static com.cmakeplugin.utils.CMakePDC.isCLION;

import com.cmakeplugin.CMakeLanguage;
import com.cmakeplugin.psi.*;
import com.cmakeplugin.psi.impl.*;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.PlatformIcons;
import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/** Provide Platform Dependent Code (IDEA/CLion) encapsulation into API */
public class CMakePlusPDC {

  @NotNull
  @SuppressWarnings("unchecked")
  public static final Class<? extends PsiElement>[] FOLDABLE_BODIES =
      (isCLION)
          ? new Class[] {
            com.jetbrains.cmake.psi.CMakeBodyBlock.class,
            com.jetbrains.cmake.psi.CMakeCommandArguments.class,
            PsiComment.class
          }
          : new Class[] {
            CMakeFunbody.class,
            CMakeMacrobody.class,
            CMakeIfbody.class,
            CMakeForbody.class,
            CMakeWhilebody.class,
            CMakeArguments.class,
            PsiComment.class
          };

  public static boolean isLineComment(PsiComment comment) {
    return (isCLION)
        ? !isCLionBracketComment(comment.getText())
        : comment.getNode().getElementType() == CMakeTypes.LINE_COMMENT;
  }

  private static boolean isCLionBracketComment(String commentText) {
    return (commentText.startsWith("#[=") && commentText.endsWith("=]"))
        || (commentText.startsWith("#[[") && commentText.endsWith("]]"));
    //            commentText.matches("(#\\[=*\\[)(.|\n|\r)*(]=*])");
  }

  public static boolean isSubsequentLineCommentsGlueElement(PsiElement element) {
    return (isCLION)
        ? element instanceof PsiWhiteSpace || (isEOL(element) && !isEOL(element.getNextSibling()))
        : element instanceof PsiWhiteSpace
            // accept only one caret-return inside subsequent comments
            && element.getText().split("\n", 3).length == 2;
  }

  private static boolean isEOL(PsiElement element) {
    return element != null
        && element.getNode().getElementType() == com.jetbrains.cmake.psi.CMakeTokenTypes.EOL;
  }

  public static TextRange getBodyBlockRangeToFold(PsiElement element) {
    TextRange range = element.getTextRange();
    if (isCLION
        && !range.isEmpty()
        && isEOL(element.getContainingFile().findElementAt(range.getEndOffset() - 1))) {
      // exclude EOL with '\n' that belongs to BodyBlock
      range = new TextRange(range.getStartOffset(), range.getEndOffset() - 1);
    }
    return range;
  }

  public static final Class<? extends PsiFile> CMAKE_FILE_CLASS =
      (isCLION) ? com.jetbrains.cmake.psi.CMakeFile.class : CMakeFile.class;

  public static final Class<? extends NavigatablePsiElement> VARDEF_CLASS =
      (isCLION)
          ? com.jetbrains.cmake.psi.CMakeLiteralImpl.class
          : CMakeUnquotedArgumentMaybeVariableContainerImpl.class;

  public static final TokenSet VARDEF_ELEMENT_TYPES =
      (isCLION)
          ? TokenSet.create(
              com.jetbrains.cmake.psi.CMakeTokenTypes.LITERAL,
              com.jetbrains.cmake.psi.CMakeTokenTypes.C_MAKE_COMMAND_ARGUMENTS)
          : TokenSet.create(
              CMakeTypes.UNQUOTED_ARGUMENT_MAYBE_VAR_DEF, CMakeTypes.UNQUOTED_ARGUMENT);

  public static final TokenSet VARREF_ELEMENT_TYPES =
      (isCLION)
          ? TokenSet.create(
              com.jetbrains.cmake.psi.CMakeTokenTypes.LITERAL,
              com.jetbrains.cmake.psi.CMakeTokenTypes.C_MAKE_COMMAND_ARGUMENTS)
          : TokenSet.create(CMakeTypes.UNQUOTED_ARGUMENT, CMakeTypes.QUOTED_ARGUMENT);

  public static final TokenSet COMMAND_KEYWORD_ELEMENT_TYPES =
      (isCLION)
          ? TokenSet.create(
              com.jetbrains.cmake.psi.CMakeTokenTypes.C_MAKE_IF_COMMAND_CALL_NAME,
              com.jetbrains.cmake.psi.CMakeTokenTypes.C_MAKE_ELSE_IF_COMMAND_CALL_NAME,
              com.jetbrains.cmake.psi.CMakeTokenTypes.C_MAKE_ELSE_COMMAND_CALL_NAME,
              com.jetbrains.cmake.psi.CMakeTokenTypes.C_MAKE_END_IF_COMMAND_CALL_NAME,
              com.jetbrains.cmake.psi.CMakeTokenTypes.C_MAKE_FOREACH_COMMAND_CALL_NAME,
              com.jetbrains.cmake.psi.CMakeTokenTypes.C_MAKE_END_FOREACH_COMMAND_CALL_NAME,
              com.jetbrains.cmake.psi.CMakeTokenTypes.C_MAKE_WHILE_COMMAND_CALL_NAME,
              com.jetbrains.cmake.psi.CMakeTokenTypes.C_MAKE_END_WHILE_COMMAND_CALL_NAME,
              com.jetbrains.cmake.psi.CMakeTokenTypes.C_MAKE_FUNCTION_COMMAND_CALL_NAME,
              com.jetbrains.cmake.psi.CMakeTokenTypes.C_MAKE_END_FUNCTION_COMMAND_CALL_NAME,
              com.jetbrains.cmake.psi.CMakeTokenTypes.C_MAKE_MACRO_COMMAND_CALL_NAME,
              com.jetbrains.cmake.psi.CMakeTokenTypes.C_MAKE_END_MACRO_COMMAND_CALL_NAME)
          : TokenSet.create(
              CMakeTypes.IF,
              CMakeTypes.ELSE,
              CMakeTypes.ELSEIF,
              CMakeTypes.ENDIF,
              CMakeTypes.FOREACH,
              CMakeTypes.ENDFOREACH,
              CMakeTypes.WHILE,
              CMakeTypes.ENDWHILE,
              CMakeTypes.FUNCTION,
              CMakeTypes.ENDFUNCTION,
              CMakeTypes.MACRO,
              CMakeTypes.ENDMACRO);

  public static final TokenSet END_OF_COMMAND_KEYWORD_ELEMENT_TYPES =
      (isCLION)
          ? TokenSet.create(
              com.jetbrains.cmake.psi.CMakeTokenTypes.C_MAKE_ELSE_COMMAND,
              com.jetbrains.cmake.psi.CMakeTokenTypes.C_MAKE_END_IF_COMMAND,
              com.jetbrains.cmake.psi.CMakeTokenTypes.C_MAKE_END_FOREACH_COMMAND,
              com.jetbrains.cmake.psi.CMakeTokenTypes.C_MAKE_END_WHILE_COMMAND,
              com.jetbrains.cmake.psi.CMakeTokenTypes.C_MAKE_END_FUNCTION_COMMAND,
              com.jetbrains.cmake.psi.CMakeTokenTypes.C_MAKE_END_MACRO_COMMAND)
          : TokenSet.create(
              CMakeTypes.ELSE_EXPR,
              CMakeTypes.ENDIF_EXPR,
              CMakeTypes.FOREND,
              CMakeTypes.WHILEEND,
              CMakeTypes.FEND,
              CMakeTypes.MEND);

  public static Language getLanguageInstance() {
    try {
      return (isCLION)
          ? (Language)
              Class.forName("com.jetbrains.cmake.CMakeLanguage")
                  .getDeclaredField("INSTANCE")
                  .get(null)
          : CMakeLanguage.INSTANCE;
    } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
      throw new java.lang.RuntimeException("CMakeLanguage.INSTANCE not found: ", e);
    }
  }

  public static final Icon ICON_CMAKE_MACRO =
      (isCLION) ? icons.CMakeIcons.CMake_Macro : PlatformIcons.METHOD_ICON;

  public static final Icon ICON_CMAKE_FUNCTION =
      (isCLION) ? icons.CMakeIcons.CMake_Function : PlatformIcons.FUNCTION_ICON;

  public static final Icon ICON_VAR = PlatformIcons.VARIABLE_ICON;

  @NotNull
  public static PsiElement createCommandName(
      @NotNull Project project, @NotNull String newCommandName) {
    return (isCLION)
        ? com.jetbrains.cmake.psi.CMakeElementFactory.createCommandName(project, newCommandName)
        : CMakePsiElementFactory.createCommandName(project, newCommandName);
  }

  @NotNull
  public static PsiElement createEmptyArguments(@NotNull Project project) {
    PsiElement result;
    String text = "if() \n endif()";
    PsiFile tempFile =
        (isCLION)
            ? com.jetbrains.cmake.psi.CMakeElementFactory.createFile(project, text)
            : CMakePsiElementFactory.createFile(project, text);
    result = PsiTreeUtil.findChildOfType(tempFile, CMakePDC.ARGUMENTS_CLASS);
    return Objects.requireNonNull(result);
  }
}
