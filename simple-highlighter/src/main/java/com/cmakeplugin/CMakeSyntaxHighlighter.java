package com.cmakeplugin;

import com.cmakeplugin.utils.CMakePDC;
import com.cmakeplugin.utils.CMakeProxyToJB;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import com.cmakeplugin.psi.CMakeTypes;

import java.awt.*;
import java.util.Map;

/**
 * This is a simple highlighter based on the lexer output. Annotator provides the psi-aware
 * highlights.
 */
public class CMakeSyntaxHighlighter extends SyntaxHighlighterBase {
  private static final Map<IElementType, TextAttributesKey> keys1;
  private static final Map<IElementType, TextAttributesKey> keys2;

  @NotNull
  @Override
  public Lexer getHighlightingLexer() {
    return CMakePDC.getCMakeLexer();
  }

  // Highlighting styles
  public static final TextAttributesKey COMMENT =
      TextAttributesKey.createTextAttributesKey(
          "Line comment", DefaultLanguageHighlighterColors.LINE_COMMENT);

  public static final TextAttributesKey STRING =
      TextAttributesKey.createTextAttributesKey(
          "String literal", DefaultLanguageHighlighterColors.STRING);

  public static final TextAttributesKey BRACES =
      TextAttributesKey.createTextAttributesKey(
          "CMAKE.BRACES", DefaultLanguageHighlighterColors.BRACES);

  public static final TextAttributesKey SEPARATOR =
      TextAttributesKey.createTextAttributesKey(
          "Separator", DefaultLanguageHighlighterColors.KEYWORD);

  public static final TextAttributesKey BADCHAR =
      TextAttributesKey.createTextAttributesKey(
          "CMAKE.BADCHAR", DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE);

  public static final TextAttributesKey KEYWORD =
      TextAttributesKey.createTextAttributesKey(
          "Keyword", DefaultLanguageHighlighterColors.KEYWORD);

  public static final TextAttributesKey CMAKE_COMMAND =
      TextAttributesKey.createTextAttributesKey(
          "CMAKE.COMMAND", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);

  public static final TextAttributesKey FUNCTION =
      createTextAttributesKey(
          "CMAKE.FUNCTION", Font.BOLD, DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);

  public static final TextAttributesKey MACROS =
      createTextAttributesKey(
          "CMAKE.MACROS", Font.BOLD, DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);

  public static final TextAttributesKey UNQUOTED_LEGACY =
      TextAttributesKey.createTextAttributesKey(
          "CMAKE.UNQUOTED_LEGACY", DefaultLanguageHighlighterColors.DOC_COMMENT_TAG);

  public static final TextAttributesKey CMAKE_VAR_REF =
      TextAttributesKey.createTextAttributesKey(
          "CMAKE.CMAKE_VAR_REF", DefaultLanguageHighlighterColors.CONSTANT);

  public static final TextAttributesKey CMAKE_VAR_DEF =
      createTextAttributesKey(
          "CMAKE.CMAKE_VAR_DEF", Font.BOLD, DefaultLanguageHighlighterColors.CONSTANT);

  public static final TextAttributesKey VAR_REF =
      TextAttributesKey.createTextAttributesKey(
          "CMAKE.VAR_REF", DefaultLanguageHighlighterColors.INSTANCE_FIELD);

  public static final TextAttributesKey VAR_DEF =
      createTextAttributesKey(
          "CMAKE.VAR_DEF", Font.BOLD, DefaultLanguageHighlighterColors.INSTANCE_FIELD);

  public static final TextAttributesKey BRACKET_ARGUMENT =
      TextAttributesKey.createTextAttributesKey(
          "BRACKET_ARGUMENT", DefaultLanguageHighlighterColors.STRING);

  public static final TextAttributesKey CMAKE_OPERATOR =
      TextAttributesKey.createTextAttributesKey(
          "CMAKE.OPERATOR", DefaultLanguageHighlighterColors.METADATA);

  public static final TextAttributesKey CMAKE_PATH_URL =
      TextAttributesKey.createTextAttributesKey(
          "CMAKE.PATH_URL", DefaultLanguageHighlighterColors.DOC_COMMENT_TAG_VALUE);

  public static final TextAttributesKey CMAKE_PROPERTY =
      TextAttributesKey.createTextAttributesKey(
          "CMAKE.PROPERTY", DefaultLanguageHighlighterColors.NUMBER);

  public static final TextAttributesKey CMAKE_BOOLEAN =
      TextAttributesKey.createTextAttributesKey(
          "CMAKE.BOOLEAN", DefaultLanguageHighlighterColors.NUMBER);

  @NotNull
  private static TextAttributesKey createTextAttributesKey(
      String externalName, int newFontType, TextAttributesKey baseTextAttributesKey) {
    TextAttributes textAttributes = baseTextAttributesKey.getDefaultAttributes().clone();
    int fontType = textAttributes.getFontType() + newFontType;
    if (fontType >= 0 && fontType <= 3) {
      textAttributes.setFontType(fontType);
    }
    return TextAttributesKey.createTextAttributesKey(externalName, textAttributes);
  }

  //  private static TextAttributesKey createBoldTextAttributesKey (String externalName,
  // TextAttributesKey baseTextAttributesKey) {
  //    TextAttributes textAttributes = baseTextAttributesKey.getDefaultAttributes().clone();
  //    textAttributes.setFontType(Font.BOLD);
  //    return TextAttributesKey.createTextAttributesKey( externalName, textAttributes);
  //  }

  static {
    keys1 = new THashMap<IElementType, TextAttributesKey>();
    keys2 = new THashMap<IElementType, TextAttributesKey>();
    keys1.put(TokenType.BAD_CHARACTER, BADCHAR);
    keys1.put(TokenType.WHITE_SPACE, SEPARATOR);
    // TODO: Populate maps here
    if (CMakePDC.isCLION) {
      for (IElementType keyword: CMakeProxyToJB.JB_KEYWORDS.getTypes()) {
        keys1.put(keyword, DefaultLanguageHighlighterColors.KEYWORD);
      }
      keys1.put(CMakeProxyToJB.JB_COMMENT, DefaultLanguageHighlighterColors.LINE_COMMENT);
      keys1.put(CMakeProxyToJB.JB_LITERAL, DefaultLanguageHighlighterColors.STRING);
    } else {
      keys1.put(CMakeTypes.LINE_COMMENT, COMMENT);
      keys1.put(CMakeTypes.BRACKET_COMMENT, COMMENT);
      keys1.put(CMakeTypes.QUOTED_ARGUMENT, STRING);
      keys1.put(CMakeTypes.LPAR, BRACES);
      keys1.put(CMakeTypes.RPAR, BRACES);
      keys1.put(CMakeTypes.CMAKE_COMMAND, CMAKE_COMMAND);
      keys1.put(CMakeTypes.BRACKET_ARGUMENT, BRACKET_ARGUMENT);
      // IF keywords highlight
      keys1.put(CMakeTypes.IF, KEYWORD);
      keys1.put(CMakeTypes.ELSEIF, KEYWORD);
      keys1.put(CMakeTypes.ENDIF, KEYWORD);
      keys1.put(CMakeTypes.ELSE, KEYWORD);
      // FOR keywords highlight
      keys1.put(CMakeTypes.FOREACH, KEYWORD);
      keys1.put(CMakeTypes.ENDFOREACH, KEYWORD);
      // WHILE keywords highlight
      keys1.put(CMakeTypes.WHILE, KEYWORD);
      keys1.put(CMakeTypes.ENDWHILE, KEYWORD);
      // MACRO keywords highlight
      keys1.put(CMakeTypes.MACRO, KEYWORD);
      keys1.put(CMakeTypes.ENDMACRO, KEYWORD);
      // FUNCTION keywords highlight
      keys1.put(CMakeTypes.FUNCTION, KEYWORD);
      keys1.put(CMakeTypes.ENDFUNCTION, KEYWORD);
    }
  }

  @NotNull
  @Override
  public TextAttributesKey[] getTokenHighlights(IElementType iElementType) {
    return SyntaxHighlighterBase.pack(keys1.get(iElementType), keys2.get(iElementType));
  }
}
