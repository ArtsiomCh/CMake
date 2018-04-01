package com.cmakeplugin;
import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static com.cmakeplugin.psi.CMakeTypes.*;
import java.util.LinkedList;

%%

%{
  boolean isConditionExpression = false;
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
// %include CMake_keywords.txt

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

QUOTED_ARGUMENT=( [^\"\\] | {ESCAPE_SEQUENCE} | \\{EOL} )+

UNQUOTED_ELEMENT= [^()#\"\\;\s] | {ESCAPE_SEQUENCE}
UNQUOTED_ARGUMENT=({UNQUOTED_ELEMENT})+

UNQUOTED_LEGACY_ELEMENT={UNQUOTED_ELEMENT}
UNQUOTED_LEGACY=(((({UNQUOTED_LEGACY_ELEMENT}+ | ("$("{UNQUOTED_LEGACY_ELEMENT}*")")+)
                   (\"({QUOTED_ARGUMENT})*\")+
                  )| ({UNQUOTED_LEGACY_ELEMENT}* ("$("{UNQUOTED_LEGACY_ELEMENT}*")")+)
                 ) {UNQUOTED_LEGACY_ELEMENT}*
                )+

IDENTIFIER=[A-Za-z_][A-Za-z0-9_]*

UNQUOTED_ARGUMENT_MAYBE_VAR_DEF=([A-Za-z0-9/_.+-]|{ESCAPE_SEQUENCE})+

%state IN_ARGLIST
%state IN_QUOTED_ARG
//%state IN_COND_EXPRESSION

%%

<YYINITIAL> {
  {WHITE_SPACE}          { return com.intellij.psi.TokenType.WHITE_SPACE; }

  "("                      { yypushstate(IN_ARGLIST); return LPAR; }

  "ENDFUNCTION"            { return ENDFUNCTION; }
  "FUNCTION"               { return FUNCTION; }
  "ENDMACRO"               { return ENDMACRO; }
  "MACRO"                  { return MACRO; }
  "ENDFOREACH"             { return ENDFOREACH; }
  "FOREACH"                { return FOREACH; }
  "ELSEIF"                 { isConditionExpression = true; return ELSEIF; }
  "ELSE"                   { isConditionExpression = true; return ELSE; }
  "ENDIF"                  { isConditionExpression = true; return ENDIF; }
  "if"                     { isConditionExpression = true; return IF; }
  "ENDWHILE"               { isConditionExpression = true; return ENDWHILE; }
  "WHILE"                  { isConditionExpression = true; return WHILE; }
  {BRACKET_COMMENT}        { return BRACKET_COMMENT; }
  {LINE_COMMENT}/{EOL}     { return LINE_COMMENT; }
  {IDENTIFIER}             { return IDENTIFIER; }
}

<IN_ARGLIST> {
  {ARG_SEPARATOR}          { yybegin(IN_ARGLIST); return com.intellij.psi.TokenType.WHITE_SPACE; }

  "("                      { yypushstate(IN_ARGLIST); return LPAR; }
  ")"                      { yypopstate(); if (yystate()==YYINITIAL) isConditionExpression = false; return RPAR; }

  \"                       { yybegin(IN_QUOTED_ARG); return BRACE; }

  {BRACKET_COMMENT}        { return BRACKET_COMMENT; }
  {LINE_COMMENT}/{EOL}     { return LINE_COMMENT; }

  {BRACKET_ARGUMENT}       { return BRACKET_ARGUMENT; }

  {UNQUOTED_ARGUMENT_MAYBE_VAR_DEF}       { if (!isConditionExpression) return UNQUOTED_ARGUMENT_MAYBE_VAR_DEF; else return UNQUOTED_ARGUMENT;}
  {UNQUOTED_ARGUMENT} | {UNQUOTED_LEGACY}  {  return UNQUOTED_ARGUMENT;}
}

<IN_QUOTED_ARG> {
  \"                       { yybegin(IN_ARGLIST); return BRACE; }
  {QUOTED_ARGUMENT}    { return QUOTED_ARGUMENT; }
}

[^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
