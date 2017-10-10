package com.cmakeplugin;
import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static com.cmakeplugin.psi.CMakeTypes.*;
import java.util.LinkedList;

%%

%{
  // Stolen from Mathematica support plugin. This adds support for nested states.
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

// Place CMake keywords in this file
%include CMake_keywords.txt

ESCAPE_SEQUENCE=  {ESCAPE_IDENTITY} | {ESCAPE_ENCODED} | {ESCAPE_SEMICOLON}
ESCAPE_IDENTITY= \\[^A-Za-z0-9;]
ESCAPE_ENCODED= \\t | \\r | \\n
ESCAPE_SEMICOLON= \\;

EOL= (\r|\n|\r\n|\f)
LINE_WS=[\ \t]
WHITE_SPACE=({LINE_WS}|{EOL})+
ARG_SEPARATOR={WHITE_SPACE}|";"

BRACKET_COMMENT=\#{BRACKET_ARGUMENT}

// TODO infinite {lenght} (through states?)
BRACKET_ARGUMENT=(\[\[)~(\]\])          |
                 (\[=\[)~(\]=\])        |
                 (\[={2}\[)~(\]={2}\])  |
                 (\[={3}\[)~(\]={3}\])  |
                 (\[={4}\[)~(\]={4}\])  |
                 (\[={5}\[)~(\]={5}\])

LINE_COMMENT= # ( [^\[\r\n] | \[=*[^\[=\r\n] ).* | #\[=* | #

QUOTED_ARGUMENT=( [^\"$] | \\\n | \\\" | \\\$ )+

UNQUOTED_ELEMENT= [^()#\"\\;\s/$] | {ESCAPE_SEQUENCE}
UNQUOTED_ARGUMENT=({UNQUOTED_ELEMENT})+

UNQUOTED_LEGACY_ELEMENT={UNQUOTED_ELEMENT}|[/$]
UNQUOTED_LEGACY=(((({UNQUOTED_LEGACY_ELEMENT}+ | ("$("{UNQUOTED_LEGACY_ELEMENT}*")")+)
                   (\"({QUOTED_ARGUMENT}|[/$])*\")+
                  )| ({UNQUOTED_LEGACY_ELEMENT}* ("$("{UNQUOTED_LEGACY_ELEMENT}*")")+)
                 ) {UNQUOTED_LEGACY_ELEMENT}*
                )+
UNQUOTED_PATH_URL=(({UNQUOTED_ARGUMENT}[$]*)?\/({UNQUOTED_ARGUMENT})?)+

IDENTIFIER=[A-Za-z_][A-Za-z0-9_]*

VAR_REF_BEGIN= \$ ( \{ | ENV\{ )
VARIABLE_NAME=([A-Za-z0-9/_.+-]|{ESCAPE_SEQUENCE})+
VAR_REF_END="}"

%state IN_ARGLIST
%state IN_QUOTED_ARG
%state IN_VAR_REF

%%

<YYINITIAL> {
  {ARG_SEPARATOR}          { return com.intellij.psi.TokenType.WHITE_SPACE; }

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
  {LINE_COMMENT}/{EOL}     { return LINE_COMMENT; }
  {CMAKE_Commands}         { return CMAKE_COMMAND; }
  {IDENTIFIER}             { return IDENTIFIER; }
}

<IN_ARGLIST> {
  {ARG_SEPARATOR}          { yybegin(IN_ARGLIST); return com.intellij.psi.TokenType.WHITE_SPACE; }

  "("                      { yypushstate(IN_ARGLIST);return LPAR; }
  ")"                      { yypopstate();return RPAR; }

  \"                       { yybegin(IN_QUOTED_ARG); return BRACE; }
  ( {VAR_REF_BEGIN} | ENV\{ )
        / (({VARIABLE_NAME}|{VAR_REF_BEGIN})+({VARIABLE_NAME}|{VAR_REF_END})+)
                           { yypushstate(IN_VAR_REF); return VAR_REF_BEGIN; }

  {CMAKE_Variables}        { return CMAKE_VARIABLE; }

  {BRACKET_COMMENT}        { return BRACKET_COMMENT; }
  {LINE_COMMENT}/{EOL}     { return LINE_COMMENT; }

  {BRACKET_ARGUMENT}       { return BRACKET_ARGUMENT; }

  {CMAKE_Property}         { return CMAKE_PROPERTY;}
  {CMAKE_Operator}         { return CMAKE_OPERATOR;}

  {UNQUOTED_PATH_URL} / {VAR_REF_BEGIN}?                     { return PATH_URL; }
  {UNQUOTED_PATH_URL}[$]+ / [()#\";\s]|{VAR_REF_BEGIN}       { return PATH_URL; }

  {UNQUOTED_ARGUMENT} / {VAR_REF_BEGIN}?                     {  return UNQUOTED_ARGUMENT;}
  {UNQUOTED_ARGUMENT}?[$]+ / [()#\";\s]|{VAR_REF_BEGIN}      {  return UNQUOTED_ARGUMENT;}

// TODO fix some bugs with corner cases with [$] in PATH_URL and $<CMAKE_Var or Commands>
  ({UNQUOTED_ARGUMENT} | {UNQUOTED_PATH_URL})?[$]+           { }

  {UNQUOTED_LEGACY}        { return UNQUOTED_LEGACY; }
}

<IN_QUOTED_ARG> {
  \"                       { yybegin(IN_ARGLIST); return BRACE; }
  {VAR_REF_BEGIN}          { yypushstate(IN_VAR_REF); return VAR_REF_BEGIN; }
  {QUOTED_ARGUMENT} | {QUOTED_ARGUMENT}?[$]+
        / (\" | {VAR_REF_BEGIN})      { return QUOTED_ARGUMENT; }
  {QUOTED_ARGUMENT}?[$]+              { }
}

<IN_VAR_REF> {
  {VAR_REF_BEGIN}          { yypushstate(IN_VAR_REF); return VAR_REF_BEGIN; }
  {VAR_REF_END}            { yypopstate(); return VAR_REF_END; }

  {CMAKE_Variables}        { return CMAKE_VARIABLE; }
  {VARIABLE_NAME}          { return VARIABLE; }
}

[^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
