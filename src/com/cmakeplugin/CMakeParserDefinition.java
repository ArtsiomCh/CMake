package com.cmakeplugin;

import com.cmakeplugin.psi.CMakeFile;
import com.cmakeplugin.CMakeLanguage;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import com.cmakeplugin.psi.CMakeTypes;
import com.cmakeplugin.parsing.CMakeParser;

/**
 * Created by alex on 12/21/14.
 */
public class CMakeParserDefinition implements ParserDefinition {
  public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
  public static final TokenSet COMMENTS = TokenSet.create(CMakeTypes.LINE_COMMENT,
          CMakeTypes.BRACKET_COMMENT);
  public static final TokenSet STRINGS = TokenSet.create(CMakeTypes.QUOTED_ARGUMENT);
  public static final IFileElementType FILE = new IFileElementType(Language.<CMakeLanguage>findInstance(CMakeLanguage.class));
  @NotNull
  @Override
  public Lexer createLexer(Project project) {
    return new CMakeLexerAdapter();
  }

  @Override
  public PsiParser createParser(Project project) {
    return new CMakeParser();
  }

  @Override
  public IFileElementType getFileNodeType() {
    return FILE;
  }

  @NotNull
  @Override
  public TokenSet getWhitespaceTokens() {
    return WHITE_SPACES;
  }

  @NotNull
  @Override
  public TokenSet getCommentTokens() {
    return COMMENTS;
  }
  @NotNull
  @Override
  public TokenSet getStringLiteralElements() { return STRINGS; }

  @NotNull
  @Override
  public PsiElement createElement(ASTNode astNode) {
    return CMakeTypes.Factory.createElement(astNode);
  }

  @Override
  public PsiFile createFile(FileViewProvider fileViewProvider) {
    return new CMakeFile(fileViewProvider);
  }

  @Override
  public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode astNode, ASTNode astNode1) {
    // Tune the separator behavior between the tokens
    if( (astNode.getElementType()  == CMakeTypes.FILE_ELEMENT
            ||astNode.getElementType() == CMakeTypes.COMPOUND_EXPR
            ||astNode.getElementType() == CMakeTypes.COMMAND_EXPR
            ||astNode.getElementType() == CMakeTypes.LINE_COMMENT)
            && !getCommentTokens().contains(astNode1.getElementType())  )
      return SpaceRequirements.MUST_LINE_BREAK;

    if( (astNode.getElementType() == CMakeTypes.ARGUMENT
            && astNode1.getElementType() == CMakeTypes.ARGUMENT))
      return SpaceRequirements.MUST;
    if( astNode.getElementType() == CMakeTypes.UNQUOTED_ARGUMENT )
      return SpaceRequirements.MUST;
    if( (astNode.getElementType() == CMakeTypes.COMMAND_NAME))
      return SpaceRequirements.MUST_NOT;
    return SpaceRequirements.MAY;
  }
}
