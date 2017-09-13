package com.cmakeplugin;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import com.cmakeplugin.psi.CMakeTypes;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Set;
import java.lang.String;

%%

%class _CMakeLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%debug
%eof{ return;
%eof}

%{
    // Stolen from Mathematica support plugin
    // This adds support for nested states. I'm no JFlex pro, so maybe this is overkill, but it works quite well.
    private final LinkedList<Integer> states = new LinkedList();

    // Scope names that needs to be tracked when entering block
    private final LinkedList<String> names = new LinkedList();

    // Known ids (used before or set)
    private final Set<String> known_ids = new HashSet<String>();

    private void yypushstate(int state) {
        states.addFirst(yystate());
        yybegin(state);
    }
    private void yypopstate() {
        final int state = states.removeFirst();
        yybegin(state);
    }

    private void yyaddsymbol(String s) {
        known_ids.add(s);
    }

    private boolean yyisknown(String s) {
        return known_ids.contains(s);
    }
%}

ABEGIN="("
AEND=")"
IDENTIFIER= [A-Za-z_][A-Za-z0-9_.]*
NUMBER=[0-9]+ (\.[0-9]*)+
SPACE=[ \t\f]+
NEWLINE={SPACE}* (\n | \r\n | \r)
VAR_REF="\$\{" [A-Za-z_][A-Za-z0-9_.]*  "\}"

BLOCK_IDENTIFIER="if"|"function"|"foreach"|"while"
BLOCK_IDENTIFIER_END="endif"|"endfunction"|"endforeach"|"endwhile"

QUOTED_ARGUMENT=  [\"] {QUOTED_ELEMENT}* [\"]
QUOTED_ELEMENT=  [^\"] | {ESCAPE_SEQUENCE} | {QUOTED_CONTINUATION}
QUOTED_CONTINUATION="\\" {NEWLINE}

UNQUOTED_ARGUMENT={UNQUOTED_ELEMENT}+
UNQUOTED_ELEMENT= [^ \(\)\#\"\\] | {ESCAPE_SEQUENCE}

ESCAPE_SEQUENCE=  {ESCAPE_IDENTITY} | {ESCAPE_ENCODED} | {ESCAPE_SEMICOLON}
ESCAPE_IDENTITY="\\\(" | "\\\)" | "\\\#" | "\\\"" | "\\\ " | "\\\\" | "\\\$" | "\\\@" | "\\\^"
ESCAPE_ENCODED="\\t" | "\\r" | "\\n"
ESCAPE_SEMICOLON="\\;"

LINE_COMMENT="#" [^\[\=\n\r]* {NEWLINE}
BLOCK_ARGUMENT_BEGIN="[" "="* "["
BLOCK_ARGUMENT_END="]" "="* "]"

KEYWORD="add_custom_command"|
        "add_custom_target"|
        "add_definitions"|
        "add_dependencies"|
        "add_executable"|
        "add_library"|
        "add_subdirectory"|
        "add_test"|
        "aux_source_directory"|
        "break"|
        "build_command"|
        "cmake_minimum_required"|
        "cmake_policy"|
        "configure_file"|
        "create_test_sourcelist"|
        "define_property"|
        "enable_language"|
        "enable_testing"|
        "execute_process"|
        "export"|
        "file"|
        "find_file"|
        "find_library"|
        "find_package"|
        "find_path"|
        "find_program"|
        "fltk_wrap_ui"|
        "get_cmake_property"|
        "get_directory_property"|
        "get_filename_component"|
        "get_property"|
        "get_source_file_property"|
        "get_target_property"|
        "get_test_property"|
        "include"|
        "include_directories"|
        "include_external_msproject"|
        "include_regular_expression"|
        "install"|
        "link_directories"|
        "list"|
        "load_cache"|
        "load_command"|
        "mark_as_advanced"|
        "math"|
        "message"|
        "option"|
        "project"|
        "qt_wrap_cpp"|
        "qt_wrap_ui"|
        "remove_definitions"|
        "return"|
        "separate_arguments"|
        "set"|
        "set_directory_properties"|
        "set_property"|
        "set_source_files_properties"|
        "set_target_properties"|
        "set_tests_properties"|
        "site_name"|
        "source_group"|
        "string"|
        "target_link_libraries"|
        "try_compile"|
        "try_run"|
        "unset"|
        "variable_watch"

%state IN_FUNCTION
%state IN_FOR
%state IN_WHILE
%state IN_MACRO
%state IN_IF
%state IN_COMMAND
%state IN_BLOCK_ARGUMENT
%state IN_ARGLIST
%state IN_BLOCK_COMMAND
%state IN_BLOCK_COMMAND_END
%state IN_BODY

%%
<YYINITIAL> {
    {BLOCK_ARGUMENT_BEGIN} {yypushstate(IN_BLOCK_ARGUMENT);return CMakeTypes.BRACKET_ARGUMENT;}
    {LINE_COMMENT}   {return CMakeTypes.LINE_COMMENT;}
    {SPACE}* {BLOCK_IDENTIFIER} {SPACE}* {
        yypushstate(IN_BLOCK_COMMAND);
        if(yytext().toString().matches("function"))
            return CMakeTypes.FUNCTION;
        else if(yytext().toString().matches("macro"))
            return CMakeTypes.MACRO;
        else if(yytext().toString().matches("while"))
                    return CMakeTypes.WHILE;
        else if(yytext().toString().matches("foreach"))
                    return CMakeTypes.FOREACH;
        else return CMakeTypes.KEYWORD;
    }
    {SPACE}* {KEYWORD} {SPACE}* {yypushstate(IN_COMMAND);return CMakeTypes.KEYWORD;}
    {SPACE}* {IDENTIFIER} {SPACE}* {yypushstate(IN_COMMAND);return CMakeTypes.IDENTIFIER;}
    {SPACE}+ {return CMakeTypes.SPACE;}
    {NEWLINE} {return CMakeTypes.NEWLINE;}
    .  {return TokenType.BAD_CHARACTER;}
}

<IN_BLOCK_COMMAND> {
    {BLOCK_ARGUMENT_BEGIN} {yypushstate(IN_BLOCK_ARGUMENT);return CMakeTypes.BRACKET_ARGUMENT;}
    {LINE_COMMENT}   {return CMakeTypes.LINE_COMMENT;}
    {NEWLINE} {return CMakeTypes.NEWLINE;}
    {SPACE}* {BLOCK_IDENTIFIER_END} {SPACE}* {
        yypopstate();
        yypushstate(IN_BLOCK_COMMAND_END);
        if(yytext().toString().matches("endfunction"))
            return CMakeTypes.ENDFUNCTION;
        else if(yytext().toString().matches("endmacro"))
            return CMakeTypes.ENDMACRO;
        else if(yytext().toString().matches("endwhile"))
            return CMakeTypes.ENDWHILE;
        else if(yytext().toString().matches("endforeach"))
            return CMakeTypes.ENDFOREACH;
        else return CMakeTypes.KEYWORD;
    }
    {SPACE}* {BLOCK_IDENTIFIER} {SPACE}* {
        yypushstate(IN_BLOCK_COMMAND);
        if(yytext().toString().matches("function"))
            return CMakeTypes.FUNCTION;
        else if(yytext().toString().matches("macro"))
            return CMakeTypes.MACRO;
        else if(yytext().toString().matches("while"))
                    return CMakeTypes.WHILE;
        else if(yytext().toString().matches("foreach"))
                    return CMakeTypes.FOREACH;
        else return CMakeTypes.KEYWORD;
    }
    {SPACE}* {KEYWORD} {SPACE}* {yypushstate(IN_COMMAND);return CMakeTypes.KEYWORD;}
    {SPACE}* {IDENTIFIER} {SPACE}* {
        yypushstate(IN_COMMAND);
        return CMakeTypes.IDENTIFIER;
    }
    {ABEGIN} {yypushstate(IN_ARGLIST); return CMakeTypes.ABEGIN;}
    .  {return TokenType.BAD_CHARACTER;}
 }

<IN_COMMAND,IN_BLOCK_COMMAND_END> {
    {SPACE}* {BLOCK_ARGUMENT_BEGIN} {yypushstate(IN_BLOCK_ARGUMENT);return CMakeTypes.BRACKET_ARGUMENT;}
    {SPACE}* {LINE_COMMENT}   {return CMakeTypes.LINE_COMMENT;}
    {ABEGIN} {yypushstate(IN_ARGLIST); return CMakeTypes.ABEGIN;}
    {NEWLINE} {yypopstate(); return CMakeTypes.NEWLINE;}
    . {return TokenType.BAD_CHARACTER;}
}

<IN_ARGLIST> {
    {SPACE}* {BLOCK_ARGUMENT_BEGIN} {yypushstate(IN_BLOCK_ARGUMENT);return CMakeTypes.BRACKET_ARGUMENT;}
    {QUOTED_ARGUMENT} {return CMakeTypes.QUOTED_ARGUMENT;}
    {UNQUOTED_ARGUMENT} {return CMakeTypes.UNQUOTED_ARGUMENT;}
    {VAR_REF} {return CMakeTypes.VAR_REF;}
    {AEND} {yypopstate(); return CMakeTypes.AEND;}
    {SPACE}+ {return CMakeTypes.SPACE;}
    {NEWLINE} {return CMakeTypes.NEWLINE;}
    . {return TokenType.BAD_CHARACTER;}
}

<IN_BLOCK_ARGUMENT> {
    {BLOCK_ARGUMENT_END} { yypopstate(); return CMakeTypes.BRACKET_ARGUMENT; }
    {SPACE}* {NEWLINE}* {return CMakeTypes.BRACKET_ARGUMENT;}
    .+ {return CMakeTypes.BRACKET_ARGUMENT; }
}
