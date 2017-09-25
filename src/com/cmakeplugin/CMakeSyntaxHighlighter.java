package com.cmakeplugin;

import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import com.cmakeplugin.psi.CMakeTypes;
//import com.cmakeplugin.parsing.CMakeLexerAdapter;

import java.util.Map;

/**
 * This is a simple highlighter based on the lexer output.
 * Annotator provides  the psi-aware highlights.
 */
public class CMakeSyntaxHighlighter extends SyntaxHighlighterBase {
  private static final Map<IElementType, TextAttributesKey> keys1;
  private static final Map<IElementType, TextAttributesKey> keys2;

  @NotNull
  @Override
  public Lexer getHighlightingLexer() {
    return new CMakeLexerAdapter();
  }

  // TODO: Add text highlighting attributes
  // TODO: Add mapping between token and its highlighting properties
  // Highlighting styles
  public static final TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey(
          "Keyword",
          DefaultLanguageHighlighterColors.KEYWORD
  );

  public static final TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey(
          "Line comment",
          DefaultLanguageHighlighterColors.LINE_COMMENT
  );

  public static final TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey(
          "Srting literal",
          DefaultLanguageHighlighterColors.STRING
  );

  public static final TextAttributesKey BRACES = TextAttributesKey.createTextAttributesKey(
          "CMAKE.BRACES",
          DefaultLanguageHighlighterColors.BRACES
  );

  public static final TextAttributesKey BADCHAR = TextAttributesKey.createTextAttributesKey(
          "CMAKE.BADCHAR",
          DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE
  );

  public static final TextAttributesKey VAREXP = TextAttributesKey.createTextAttributesKey(
          "CMAKE.VAREXP",
          DefaultLanguageHighlighterColors.IDENTIFIER
  );

  public static final TextAttributesKey ESCAPED_CHAR = TextAttributesKey.createTextAttributesKey(
          "CMAKE.ESCAPED_CHAR",
          DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE
  );

  public static final TextAttributesKey BLOCK_COMMENT = TextAttributesKey.createTextAttributesKey(
          "CMAKE.BLOCK_COMMENT",
          DefaultLanguageHighlighterColors.BLOCK_COMMENT
  );

  public static final TextAttributesKey NUMBER = TextAttributesKey.createTextAttributesKey(
          "CMAKE.NUMBER",
          DefaultLanguageHighlighterColors.NUMBER
  );

  public static final TextAttributesKey IDENTIFIER = TextAttributesKey.createTextAttributesKey(
          "CMAKE.IDENTIFIER",
          DefaultLanguageHighlighterColors.IDENTIFIER
  );

  static {
    keys1 = new THashMap<IElementType, TextAttributesKey>();
    keys2 = new THashMap<IElementType, TextAttributesKey>();
    // TODO: Populate maps here
    keys1.put(CMakeTypes.LINE_COMMENT, COMMENT);
    keys1.put(CMakeTypes.BRACKET_COMMENT, COMMENT);
    keys1.put(CMakeTypes.QUOTED_ARGUMENT, STRING);
    keys1.put(CMakeTypes.LPAR, BRACES);
    keys1.put(CMakeTypes.RPAR, BRACES);
    keys1.put(TokenType.BAD_CHARACTER, BADCHAR);
    //keys1.put(CMakeTypes.ESCAPED_CHAR,ESCAPED_CHAR);
    keys1.put(CMakeTypes.BRACKET_COMMENT,BLOCK_COMMENT);
    // Keywords moved to the annotator
    keys1.put(CMakeTypes.CMAKE_COMMAND, DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);
    keys1.put(CMakeTypes.BRACKET_ARGUMENT, STRING);
    keys1.put(CMakeTypes.CMAKE_VARIABLE, DefaultLanguageHighlighterColors.CONSTANT);

    keys1.put(CMakeTypes.VARIABLE, DefaultLanguageHighlighterColors.INSTANCE_FIELD);

    // If condition highlight
    keys1.put(CMakeTypes.IF, KEYWORD);
    keys1.put(CMakeTypes.ELSEIF, KEYWORD);
    keys1.put(CMakeTypes.ENDIF, KEYWORD);
    keys1.put(CMakeTypes.ELSE, KEYWORD);

  }
  @NotNull
  @Override
  public TextAttributesKey[] getTokenHighlights(IElementType iElementType) {
    return SyntaxHighlighterBase.pack(keys1.get(iElementType), keys2.get(iElementType));
  }

  //TODO: Fill the map to use it in the ColorsPage
/*  public static final Map<TextAttributesKey, Pair<String, HighlightSeverity>> DISPLAY_NAMES = new THashMap<>(6);

  static {
    DISPLAY_NAMES.put(KEYWORD, new Pair<String, HighlightSeverity>("Keyword",null));
    DISPLAY_NAMES.put(BRACES, new Pair<String, HighlightSeverity>("Braces", null));
    DISPLAY_NAMES.put(STRING, new Pair<String, HighlightSeverity>("String", null));
    DISPLAY_NAMES.put(COMMENT, new Pair<String, HighlightSeverity>("Comment", null));
    DISPLAY_NAMES.put(IDENTIFIER, new Pair<String, HighlightSeverity>("Identifier", null));
    DISPLAY_NAMES.put(BADCHAR, Pair.create("Bad Character", HighlightSeverity.WARNING));
  }*/
}
