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

EOL="\r"|"\n"|"\r\n"
LINE_WS=[\ \t\f]
WHITE_SPACE=({LINE_WS}|{EOL})+

ENDFUNCTION=endfunction|ENDFUNCTION
FUNCTION=function|FUNCTION
ELSEIF=elseif|ELSIF
ELSE=else|ELSE
ENDIF=endif|ENDIF
IF=if|IF
ENDMACRO=endmacro|ENDMACRO
MACRO=macro|MACRO
ENDFOREACH=endforeach|ENDFOREACH
FOREACH=foreach|FOREACH
ENDWHILE=endwhile|ENDWHILE
WHILE=while|ENDWHILE
BRACKET_COMMENT=(\#\[=*\[)([^\]]|\n)*?(\]=*\])
BRACKET_ARGUMENT=(\[=*\[)([^\]]|\n)*?(\]=*\])
LINE_COMMENT=\#.*
QUOTED_ARGUMENT=(\") ( [^\"]\\\n | [^\"] | \\\" )* (\")
UNQUOTED_ARGUMENT=([^\(\)\#\"\\ ]|\\\( | \\\) | \\\# | \\\" | (\\ ) | \\\\ | \\\$ | \\\@ | \\\^ | \\t | \\r | \\n| \\;)*
IDENTIFIER=[A-Za-z_][A-Za-z0-9_]*

CMAKE_KEYWORD="add_custom_command"|
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


%state IN_ARGLIST
%%
<YYINITIAL> {
  {WHITE_SPACE}            { return com.intellij.psi.TokenType.WHITE_SPACE; }

  "("                      { yypushstate(IN_ARGLIST);return LPAR; }


  {ENDFUNCTION}            { return ENDFUNCTION; }
  {FUNCTION}               { return FUNCTION; }
  {ELSEIF}                 { return ELSEIF; }
  {ELSE}                   { return ELSE; }
  {ENDIF}                  { return ENDIF; }
  {IF}                     { return IF; }
  {ENDMACRO}               { return ENDMACRO; }
  {MACRO}                  { return MACRO; }
  {ENDFOREACH}             { return ENDFOREACH; }
  {FOREACH}                { return FOREACH; }
  {ENDWHILE}               { return ENDWHILE; }
  {WHILE}                  { return WHILE; }
  {BRACKET_COMMENT}        { return BRACKET_COMMENT; }
  {BRACKET_ARGUMENT}       { return BRACKET_ARGUMENT; }
  {LINE_COMMENT}           { return LINE_COMMENT; }
  {QUOTED_ARGUMENT}        { return QUOTED_ARGUMENT; }

  {CMAKE_KEYWORD}          { return CMAKE_KEYWORD; }
  {IDENTIFIER}             { return IDENTIFIER; }

  [^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}
<IN_ARGLIST> {
  {WHITE_SPACE}            { return com.intellij.psi.TokenType.WHITE_SPACE; }
  "("                      { yypushstate(IN_ARGLIST);return LPAR; }
  ")"                      { yypopstate();return RPAR; }
  {BRACKET_COMMENT}        { return BRACKET_COMMENT; }
  {BRACKET_ARGUMENT}       { return BRACKET_ARGUMENT; }
  {LINE_COMMENT}           { return LINE_COMMENT; }
  {QUOTED_ARGUMENT}        { return QUOTED_ARGUMENT; }
  {UNQUOTED_ARGUMENT}      { return UNQUOTED_ARGUMENT; }
  [^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}