package com.cmakeplugin;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import com.cmakeplugin.psi.CMakeTypes;

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

  // Highlighting styles
  public static final TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey("Line comment",
          DefaultLanguageHighlighterColors.LINE_COMMENT  );

  public static final TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey("String literal",
          DefaultLanguageHighlighterColors.STRING  );

  public static final TextAttributesKey BRACES = TextAttributesKey.createTextAttributesKey("CMAKE.BRACES",
          DefaultLanguageHighlighterColors.BRACES  );

  public static final TextAttributesKey SEPARATOR = TextAttributesKey.createTextAttributesKey("Separator",
          DefaultLanguageHighlighterColors.KEYWORD  );

  public static final TextAttributesKey BADCHAR = TextAttributesKey.createTextAttributesKey("CMAKE.BADCHAR",
          DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE  );

  public static final TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey("Keyword",
          DefaultLanguageHighlighterColors.KEYWORD  );

  public static final TextAttributesKey CMAKE_COMMAND = TextAttributesKey.createTextAttributesKey("CMAKE.COMMAND",
          DefaultLanguageHighlighterColors.FUNCTION_DECLARATION  );

  public static final TextAttributesKey UNQUOTED_LEGACY = TextAttributesKey.createTextAttributesKey("CMAKE.UNQUOTED_LEGACY",
          DefaultLanguageHighlighterColors.DOC_COMMENT_TAG  );

  public static final TextAttributesKey CMAKE_VARIABLE = TextAttributesKey.createTextAttributesKey("CMAKE.CMAKE_VARIABLE",
          DefaultLanguageHighlighterColors.CONSTANT  );

  public static final TextAttributesKey VARIABLE = TextAttributesKey.createTextAttributesKey("CMAKE.VARIABLE",
          DefaultLanguageHighlighterColors.INSTANCE_FIELD  );

  public static final TextAttributesKey VAR_REF = TextAttributesKey.createTextAttributesKey("VARIABLE REFS",
          DefaultLanguageHighlighterColors.LINE_COMMENT  );

  public static final TextAttributesKey BRACKET_ARGUMENT = TextAttributesKey.createTextAttributesKey("BRACKET_ARGUMENT",
          DefaultLanguageHighlighterColors.STRING  );

  public static final TextAttributesKey CMAKE_OPERATOR = TextAttributesKey.createTextAttributesKey("CMAKE.OPERATOR",
          DefaultLanguageHighlighterColors.METADATA  );

  public static final TextAttributesKey CMAKE_PATH_URL = TextAttributesKey.createTextAttributesKey("CMAKE.PATH_URL",
          DefaultLanguageHighlighterColors.DOC_COMMENT_TAG_VALUE  );

  public static final TextAttributesKey CMAKE_PROPERTY = TextAttributesKey.createTextAttributesKey("CMAKE.PROPERTY",
          DefaultLanguageHighlighterColors.NUMBER  );

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
    keys1.put(CMakeTypes.BRACKET_COMMENT, COMMENT);

    keys1.put(com.intellij.psi.TokenType.WHITE_SPACE, SEPARATOR);
    keys1.put(CMakeTypes.CMAKE_COMMAND, CMAKE_COMMAND);
    keys1.put(CMakeTypes.BRACKET_ARGUMENT, BRACKET_ARGUMENT);
//    keys1.put(CMakeTypes.UNQUOTED_LEGACY, UNQUOTED_LEGACY);
//
//    keys1.put(CMakeTypes.CMAKE_VARIABLE, CMAKE_VARIABLE);
//    keys1.put(CMakeTypes.VARIABLE, VARIABLE);
//    keys1.put(CMakeTypes.VAR_REF_BEGIN, VAR_REF);
//    keys1.put(CMakeTypes.VAR_REF_END, VAR_REF);
//
//    keys1.put(CMakeTypes.CMAKE_PROPERTY, CMAKE_PROPERTY);
//    keys1.put(CMakeTypes.CMAKE_OPERATOR, CMAKE_OPERATOR);
//    keys1.put(CMakeTypes.PATH_URL, CMAKE_PATH_URL);

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
  @NotNull
  @Override
  public TextAttributesKey[] getTokenHighlights(IElementType iElementType) {
    return SyntaxHighlighterBase.pack(keys1.get(iElementType), keys2.get(iElementType));
  }
}
