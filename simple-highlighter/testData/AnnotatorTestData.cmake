# Examples from cmake.org/cmake/help/latest/manual/cmake-language.7.html

<info descr="CMAKE.COMMAND">add_executable</info>(hello world.c)
<info descr="CMAKE.COMMAND">set</info>(((())))

<info descr="CMAKE.COMMAND">set</info> ( <info descr="CMAKE.VAR_DEF">varif</info> varifVal 55)
if (<info descr="CMAKE.VAR_REF">varif</info> varifVal notVar)
endif()
if(<info descr="CMAKE.BOOLEAN">FALSE</info> <info descr="CMAKE.OPERATOR">AND</info> (<info descr="CMAKE.BOOLEAN">FALSE</info> <info descr="CMAKE.OPERATOR">OR</info> <info descr="CMAKE.BOOLEAN">TRUE</info>)) # evaluates to FALSE
    <info descr="CMAKE.COMMAND">set</info>(((())))
    <info descr="CMAKE.COMMAND">set</info>(first(<info descr="CMAKE.BOOLEAN">ON</info>) <info descr="CMAKE.BOOLEAN">OFF</info>)
endif()

<info descr="CMAKE.COMMAND">message</info>( [=[jhgjhgj]=] [=[
This is the first line in a bracket argument with bracket length 1.
No \-escape sequences or ${variable} references are evaluated.
This is always one argument even though it contains a ; character.
The text does not end on a closing bracket of length 0 like ]].
It does end in a closing bracket of length 1.
]=]
	ggggg [[#]]	[=====[ ="" #ghghgh ]====] ]=====]
)

<info descr="CMAKE.COMMAND">message</info>("This is a quoted argument containing multiple lines.
This is always one argument even though it contains a ; character.
Both \\-escape sequences and $$$<info descr="CMAKE.VAR_REF">${<weak_warning descr="Possibly not defined Variable: variable">variable</weak_warning>}</info>$$$$ $references are evaluated.
The text does not end on an escaped double-quote like \".
It does end in an unescaped double quote." "\\" aaa "")

<info descr="CMAKE.COMMAND">message</info>("\
This is the first line of a quoted argument. \
In fact it is the only line but since it is long \
the source code uses line continuation.\
")

<warning descr="Deprecated command">build_name</warning>()
<info descr="CMAKE.COMMAND">break</info>(IntellijIdeaRulezzz)

<info descr="CMAKE.COMMAND">message</info>( fff=ENV{not_variable} #BadChar=\d\g\j
    fff=<info descr="CMAKE.VAR_REF">${<weak_warning descr="Possibly not defined Variable: variable_name">variable_name</weak_warning>}</info><info descr="CMAKE.VAR_REF">${<weak_warning descr="Possibly not defined Variable: YYY">YYY</weak_warning>}</info> <info descr="CMAKE.VAR_REF">${R${<weak_warning descr="Possibly not defined Variable: f">f</weak_warning>}}</info>
    <info descr="CMAKE.VAR_REF">$ENV{VAR}</info> <info descr="CMAKE.VAR_REF">ENV{VAR}</info> fdddf=<info descr="CMAKE.VAR_REF">$ENV{VAR}</info> <info descr="CMAKE.VAR_REF">ENV{d${f${<weak_warning descr="Possibly not defined Variable: t">t</weak_warning>}f}f}</info>}not_var}<info descr="CMAKE.VAR_REF">${<weak_warning descr="Possibly not defined Variable: ff">ff</weak_warning>}</info>
    <info descr="CMAKE.VAR_REF">${outer_${<weak_warning descr="Possibly not defined Variable: inner_variable">inner_variable</weak_warning>}_variable}</info>==<info descr="CMAKE.VAR_REF">${<info descr="CMAKE.CMAKE_VAR_REF">CMAKE_AR</info>}</info>==<info descr="CMAKE.VAR_REF">${<info descr="CMAKE.CMAKE_VAR_REF">CMAKE_MATCH_4</info>}</info>==<info descr="CMAKE.VAR_REF">${<info descr="CMAKE.CMAKE_VAR_REF">CMAKE_ASM_COMPILER_AR</info>}</info>
    <info descr="CMAKE.VAR_REF">${outer_${<info descr="CMAKE.CMAKE_VAR_REF">APPLE</info>}_${<weak_warning descr="Possibly not defined Variable: variable">variable</weak_warning>}}</info> <info descr="CMAKE.VAR_REF">${<weak_warning descr="Possibly not defined Variable: CMAKE_CONFIG_TYPE">CMAKE_CONFIG_TYPE</weak_warning>}</info> <info descr="CMAKE.VAR_REF">ENV{<info descr="CMAKE.CMAKE_VAR_REF">CMAKE_CONFIG_TYPE</info>}</info> <info descr="CMAKE.VAR_REF">${ddd_$ENV{<info descr="CMAKE.CMAKE_VAR_REF">CMAKE_CONFIG_TYPE</info>}}</info>
    E $ <info descr="CMAKE.PATH_URL">$$$/hhh</info> <info descr="CMAKE.PATH_URL">hhh$$/dd</info> <info descr="CMAKE.PATH_URL">$/$f</info> <info descr="CMAKE.PROPERTY">ALLOW_DUPLICATE_CUSTOM_TARGETS</info> <warning descr="Deprecated property">TEST_INCLUDE_FILE</warning> <warning descr="Deprecated property">COMPILE_DEFINITIONS_hh</warning>
)
<info descr="CMAKE.COMMAND">set</info>(<info descr="CMAKE.VAR_REF">ENV{<info descr="CMAKE.CMAKE_VAR_REF">CFLAGS</info>}</info> <info descr="CMAKE.VAR_REF">ENV{varEnv}</info>)
if (<info descr="CMAKE.VAR_REF">ENV{varEnv}</info> "<info descr="CMAKE.VAR_REF">$ENV{varEnv}</info>string" <info descr="CMAKE.VAR_REF">$ENV{<info descr="CMAKE.CMAKE_VAR_REF">CFLAGS</info>}</info>) endif()
#[ [
foreach(<info descr="CMAKE.VAR_DEF">arg</info>
    NoSpace
    <info descr="CMAKE.UNQUOTED_LEGACY">UnquotedLegacy""</info> \"fff\"Not_Legacy <info descr="CMAKE.UNQUOTED_LEGACY">/g$$E${h}h""ghg</info>
    <info descr="CMAKE.UNQUOTED_LEGACY">$/$//-Da="b c"</info> <info descr="CMAKE.UNQUOTED_LEGACY">d" "</info> <info descr="CMAKE.UNQUOTED_LEGACY">-Da=$(v)</info> <info descr="CMAKE.UNQUOTED_LEGACY">a" "b"c"f$$${not_var}</info>
        $(<info descr="CMAKE.UNQUOTED_LEGACY">b$(b)</info>) <info descr="CMAKE.UNQUOTED_LEGACY">/f" "$(f)" "</info> "jhj"jhj <info descr="CMAKE.BOOLEAN">/tmp/ME-NOTFOUND</info>
    Escaped\ Space  space$$$ $$spacE s$$pacEN spac$ENV $$$$ $$$
    # line comment
    <info descr="CMAKE.CMAKE_VAR_DEF">APPLE</info> "APPLE" <info descr="CMAKE.PATH_URL">cENV<info descr="CMAKE.VAR_REF">${x}</info>$/$d</info>   This;Divides;Into;Five;Arguments   Escaped\;Semicolon
    ${not_variable${^£%$&*}} <info descr="CMAKE.VAR_REF">${<weak_warning descr="Possibly not defined Variable: \;\&\n">\;\&\n</weak_warning>}</info> \$ENV{not_var} \${not_var} "\${not_var}"
    ${<error descr="'\"', CMakeTokenType.(, CMakeTokenType.bracket_argument, CMakeTokenType.bracket_comment, CMakeTokenType.line_comment, CMakeTokenType.unquoted_argument or CMakeTokenType.unquoted_argument_maybe_var_def expected, got '\'">\</error>y\u\i} ${var_not_seen} ON set(())
    )
  <info descr="CMAKE.COMMAND">message</info>("<info descr="CMAKE.VAR_REF">${arg}</info>")
endforeach()

#[[This is a bracket comment.
It runs until the close bracket.]]
#[==
#
<info descr="CMAKE.COMMAND">message</info>("First Argument\n" #[===[Bracket Comment]===] "Second Argument")

# This is a line comment.
<info descr="CMAKE.COMMAND">message</info>("First Argument\n" # This is a line comment :)
        "Second Argument") # This is a line comment.

<info descr="CMAKE.COMMAND">set</info>(srcs a.c b.c c.c) # sets "srcs" to "a.c;b.c;c.c"
<info descr="CMAKE.COMMAND">set</info>(<info descr="CMAKE.VAR_DEF">x</info> a "b;c") # sets "x" to "a;b;c", not "a;b\;c"

<info descr="CMAKE.COMMAND">set</info>(<info descr="CMAKE.VAR_DEF">_base</info> fff)
macro(<info descr="CMAKE.MACROS">temp_name</info> <info descr="CMAKE.VAR_DEF">fname</info>)
  <info descr="CMAKE.COMMAND">set</info>(<info descr="CMAKE.VAR_DEF">_base</info> ggg)
  if(<info descr="CMAKE.VAR_REF">${<info descr="CMAKE.CMAKE_VAR_REF">ARGC</info>}</info> <info descr="CMAKE.OPERATOR">GREATER</info> 1) # Have to escape ARGC to correctly compare
    <info descr="CMAKE.COMMAND">set</info>(<info descr="CMAKE.VAR_DEF">_base</info> <info descr="CMAKE.VAR_REF">${<info descr="CMAKE.CMAKE_VAR_REF">ARGV1</info>}</info> "<info descr="CMAKE.VAR_REF">${<info descr="CMAKE.CMAKE_VAR_REF">ARGV2</info>}</info>")
  elseif(<info descr="CMAKE.VAR_REF">${<info descr="CMAKE.CMAKE_VAR_REF">ARGC</info>}</info> <info descr="CMAKE.OPERATOR">GREATER</info> 1)
    <info descr="CMAKE.COMMAND">set</info>(<info descr="CMAKE.VAR_DEF">_base</info> ".cmake-tmp")
  else(<info descr="CMAKE.VAR_REF">${<info descr="CMAKE.CMAKE_VAR_REF">ARGC</info>}</info> <info descr="CMAKE.OPERATOR">GREATER</info> 1)
    <info descr="CMAKE.COMMAND">set</info>(<info descr="CMAKE.VAR_DEF">_base</info> ".cmake-tmp1111")
    bbbbb(<info descr="CMAKE.VAR_REF">${_base}</info>)
    if()
        ccc("<info descr="CMAKE.VAR_REF">${_base}</info>")
        <info descr="CMAKE.COMMAND">set</info>(<info descr="CMAKE.VAR_DEF">_base</info> jjjj)
    endif()
  endif(<info descr="CMAKE.VAR_REF">${<info descr="CMAKE.CMAKE_VAR_REF">ARGC</info>}</info> <info descr="CMAKE.OPERATOR">GREATER</info> 1)
  <info descr="CMAKE.COMMAND">set</info>(<info descr="CMAKE.VAR_DEF">_counter</info> 0)
  while(<info descr="CMAKE.OPERATOR">EXISTS</info> "<info descr="CMAKE.VAR_REF">${_base}</info>1<info descr="CMAKE.VAR_REF">${_counter}</info>")
    <info descr="CMAKE.COMMAND">math</info>(EXPR <info descr="CMAKE.VAR_REF">${_base}</info> <info descr="CMAKE.VAR_DEF">_counter</info> "<info descr="CMAKE.VAR_REF">${_counter}</info> + 1")
  endwhile(<info descr="CMAKE.OPERATOR">EXISTS</info> "<info descr="CMAKE.VAR_REF">${_base}</info>2<info descr="CMAKE.VAR_REF">${_counter}</info>")
  <info descr="CMAKE.COMMAND">set</info>(<info descr="CMAKE.VAR_REF">${fname}</info> <info descr="CMAKE.VAR_REF">${_base}</info>3<info descr="CMAKE.VAR_REF">${_counter}</info>)
endmacro(temp_name)

<info descr="CMAKE.COMMAND">set</info>(<info descr="CMAKE.VAR_DEF">var3</info> 3)
<info descr="CMAKE.COMMAND">set</info>(<info descr="CMAKE.CMAKE_VAR_DEF">CMAKE_CXX_FLAGS</info> "<info descr="CMAKE.VAR_REF">${<info descr="CMAKE.CMAKE_VAR_REF">CMAKE_CXX_FLAGS</info>}</info> bla bla bla")
if #[[nnn]] (<info descr="CMAKE.OPERATOR">NOT</info> <info descr="CMAKE.VAR_REF">var1</info> <info descr="CMAKE.CMAKE_VAR_REF"><info descr="CMAKE.VAR_REF">CMAKE_CXX_FLAGS</info></info>
    <info descr="CMAKE.VAR_REF">${var1}</info> #${var3}
    "<info descr="CMAKE.VAR_REF">${var1}</info>"
    "var1"
    <info descr="CMAKE.VAR_REF">var3</info> )
    <info descr="CMAKE.COMMAND">set</info>(<info descr="CMAKE.VAR_DEF">var1</info> 1)
else()
    <info descr="CMAKE.COMMAND">set</info>(<info descr="CMAKE.VAR_DEF">var1</info> 3)
endif()
<info descr="CMAKE.COMMAND">set</info>(<info descr="CMAKE.VAR_DEF">var2</info> 2)
#set(var2 2)
<info descr="CMAKE.COMMAND">message</info>(<info descr="CMAKE.VAR_REF">${var1}</info>
        "<info descr="CMAKE.VAR_REF">${var1}</info>"
        <info descr="CMAKE.VAR_REF">${var2}</info> <info descr="CMAKE.VAR_REF">${tensorflow_demo_sources}</info> <info descr="CMAKE.VAR_REF">${<weak_warning descr="Possibly not defined Variable: tensorflow_source_dir">tensorflow_source_dir</weak_warning>}</info>
        "<info descr="CMAKE.VAR_REF">${var2}</info>")
<info descr="CMAKE.COMMAND">set</info>(<info descr="CMAKE.VAR_DEF">tensorflow_demo_sources</info> test)

FAKE_COMMAND_NAME_FOR_VAR_DECLARATION_CREATION_1234567890(ffff)

function(<info descr="CMAKE.FUNCTION">fun1</info>)
  <info descr="CMAKE.COMMAND">set</info>(<info descr="CMAKE.VAR_DEF">var_fun1</info> 1)
  <info descr="CMAKE.FUNCTION">Fun2</info>()
endfunction(fun1)

function(<info descr="CMAKE.FUNCTION">fun2</info>)
  <info descr="CMAKE.COMMAND">message</info>(2<info descr="CMAKE.VAR_REF">${var_fun1}</info>2)
endfunction(fun2)

# Evaluate expression
# Suggestion from the Wiki: <info descr="Open in browser (Ctrl+Click, Ctrl+B)">http://cmake.org/Wiki/CMake/Language_Syntax</info>
# Unfortunately, no built-in stuff for this: <info descr="Open in browser (Ctrl+Click, Ctrl+B)">http://public.kitware.com/Bug/view.php?id=4034</info>
macro(<info descr="CMAKE.MACROS">eval</info> <info descr="CMAKE.VAR_DEF">expr</info>)
  <info descr="CMAKE.MACROS">temp_name</info>(<info descr="CMAKE.VAR_DEF">_fname</info>)
  <info descr="CMAKE.COMMAND">set</info>(<info descr="CMAKE.VAR_DEF">_fname</info> <info descr="CMAKE.VAR_REF">${_fname}</info>+1<info descr="CMAKE.VAR_REF">${expr}</info>)
  <info descr="CMAKE.COMMAND">file</info>(<info descr="CMAKE.OPERATOR">WRITE</info> <info descr="CMAKE.VAR_REF">${_fname}</info> "<info descr="CMAKE.VAR_REF">${expr}</info>")
  <info descr="CMAKE.COMMAND">include</info>(<info descr="CMAKE.VAR_REF">${_fname}</info>)
  <info descr="CMAKE.COMMAND">file</info>(<info descr="CMAKE.OPERATOR">REMOVE</info> trr$ <info descr="CMAKE.VAR_REF">${_fname}</info>)
endmacro(eval)

<info descr="CMAKE.MACROS">Eval</info>("message(\"Hai\")")

<info descr="CMAKE.COMMAND">set</info>(<info descr="CMAKE.VAR_DEF">funcs</info> a;b)
macro(<info descr="CMAKE.MACROS">test_a</info> <info descr="CMAKE.VAR_DEF">arg</info>)
  <info descr="CMAKE.COMMAND">message</info>("A: <info descr="CMAKE.VAR_REF">${arg}</info>")
endmacro(test_a)
macro(<info descr="CMAKE.MACROS">test_b</info> <info descr="CMAKE.VAR_DEF">arg</info>)
  <info descr="CMAKE.COMMAND">message</info>("B: <info descr="CMAKE.VAR_REF">${arg}</info>")
endmacro(test_b)

foreach(<info descr="CMAKE.VAR_DEF">func</info> <info descr="CMAKE.VAR_REF">${funcs}</info>)
  <info descr="CMAKE.COMMAND">set</info>(<info descr="CMAKE.VAR_DEF">func_name</info> test_<info descr="CMAKE.VAR_REF">${func}</info>)
  <info descr="CMAKE.MACROS">eval</info>("<info descr="CMAKE.VAR_REF">${func_name}</info>(\"Test\")")
endforeach(<info descr="CMAKE.VAR_DEF">func</info>)