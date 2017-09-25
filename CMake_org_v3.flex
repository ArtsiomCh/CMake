package com.cmakeplugin;
import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static com.cmakeplugin.psi.CMakeTypes.*;
import java.util.LinkedList;

%%

%{
  public _CMakeLexer() {
    this((java.io.Reader)null);
  }

  // Stolen from Mathematica support plugin
  // This adds support for nested states. I'm no JFlex pro, so maybe this is overkill, but it works quite well.
  private final LinkedList<Integer> states = new LinkedList();

  private void yypushstate(int state) {
      states.addFirst(yystate());
      yybegin(state);
  }
  private void yypopstate() {
      final int state = states.removeFirst();
      yybegin(state);
  }
%}

%public
%class _CMakeLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%ignorecase
%include CMake_keywords.txt

ESCAPE_SEQUENCE=  {ESCAPE_IDENTITY} | {ESCAPE_ENCODED} | {ESCAPE_SEMICOLON}
ESCAPE_IDENTITY=\\[^A-Za-z0-9;]
ESCAPE_ENCODED= \t | \r | \n
ESCAPE_SEMICOLON= \;

EOL= \r | \n | \r\n
LINE_WS=[\ \t\f]
WHITE_SPACE=({LINE_WS}|{EOL})+

BRACKET_COMMENT=(\#\[=*\[)([^\]]|\n)*?(\]=*\])
BRACKET_ARGUMENT=(\[=*\[)([^\]]|\n)*?(\]=*\])
LINE_COMMENT=\#.*
QUOTED_ARGUMENT=(\") ( [^\"]\\\n | [^\"] | \\\" )* (\")
UNQUOTED_ARGUMENT=([^\(\)\#\"\;\ \$\r\n\t])*
IDENTIFIER=[A-Za-z_][A-Za-z0-9_]*

VAR_REF_BEGIN= "${" | "$ENV{"
VARIABLE_NAME=([A-Za-z0-9/_.+-]|{ESCAPE_SEQUENCE})*
VAR_REF_END="}"

%state IN_ARGLIST
%%
<YYINITIAL> {
  {WHITE_SPACE}            { return com.intellij.psi.TokenType.WHITE_SPACE; }

  "("                      { yypushstate(IN_ARGLIST);return LPAR; }

  "ENDFUNCTION"            { return ENDFUNCTION; }
  "FUNCTION"               { return FUNCTION; }
  "ELSEIF"                 { return ELSEIF; }
  "ELSE"                   { return ELSE; }
  "ENDIF"                  { return ENDIF; }
  "if"                     { return IF; }
  "ENDMACRO"               { return ENDMACRO; }
  "MACRO"                  { return MACRO; }
  "ENDFOREACH"             { return ENDFOREACH; }
  "FOREACH"                { return FOREACH; }
  "ENDWHILE"               { return ENDWHILE; }
  "WHILE"                  { return WHILE; }
  {BRACKET_COMMENT}        { return BRACKET_COMMENT; }
  {LINE_COMMENT}           { return LINE_COMMENT; }

  {CMAKE_Commands_Scripting}|
  {CMAKE_Commands_Project}|
  {CMAKE_Commands_CTest}|
  {CMAKE_Commands_Deprecated}   { return CMAKE_COMMAND; }

  {IDENTIFIER}             { return IDENTIFIER; }

  [^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}

<IN_ARGLIST> {
  {WHITE_SPACE}|";"            { return com.intellij.psi.TokenType.WHITE_SPACE; }

  "("                      { yypushstate(IN_ARGLIST);return LPAR; }
  ")"                      { yypopstate();return RPAR; }

  {CMAKE_Variables}        { return CMAKE_VARIABLE; }
  {VAR_REF_BEGIN} {CMAKE_Variables} {VAR_REF_END}        { return CMAKE_VARIABLE; }
  {VAR_REF_BEGIN} {VARIABLE_NAME} {VAR_REF_END}               { return VARIABLE; }

  {BRACKET_COMMENT}        { return BRACKET_COMMENT; }
  {LINE_COMMENT}           { return LINE_COMMENT; }
  {BRACKET_ARGUMENT}       { return BRACKET_ARGUMENT; }
  {QUOTED_ARGUMENT}        { return QUOTED_ARGUMENT; }
  {UNQUOTED_ARGUMENT}      { return UNQUOTED_ARGUMENT; }
  [^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}

//<IN_VAR_REF> {
//  "}"                      { yypopstate(); return VAR_REF_END; }
//  {CMAKE_Variables}        { return CMAKE_VARIABLE; }
//  {IDENTIFIER}             { return IDENTIFIER; }
//  [^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
//}