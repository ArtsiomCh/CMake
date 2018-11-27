# Examples from cmake.org/cmake/help/latest/manual/cmake-language.7.html

<info descr="null">add_executable</info>(hello world.c)
<info descr="null">set</info>(((())))

if(<info descr="null">FALSE</info> <info descr="null">AND</info> (<info descr="null">FALSE</info> <info descr="null">OR</info> <info descr="null">TRUE</info>)) # evaluates to FALSE
    <info descr="null">set</info>(((())))
    <info descr="null">set</info>(first(<info descr="null">ON</info>) <info descr="null">OFF</info>)
endif()

<info descr="null">message</info>( [=[jhgjhgj]=] [=[
This is the first line in a bracket argument with bracket length 1.
No \-escape sequences or ${variable} references are evaluated.
This is always one argument even though it contains a ; character.
The text does not end on a closing bracket of length 0 like ]].
It does end in a closing bracket of length 1.
]=]
	ggggg [[#]]	[=====[ ="" #ghghgh ]====] ]=====]
)

<info descr="null">message</info>("This is a quoted argument containing multiple lines.
This is always one argument even though it contains a ; character.
Both \\-escape sequences and $$$<info descr="null">${<weak_warning descr="Possibly not defined Variable: variable">variable</weak_warning>}</info>$$$$ $references are evaluated.
The text does not end on an escaped double-quote like \".
It does end in an unescaped double quote." "\\" aaa "")

<info descr="null">message</info>("\
This is the first line of a quoted argument. \
In fact it is the only line but since it is long \
the source code uses line continuation.\
")

<warning descr="Deprecated command">build_name</warning>()
<info descr="null">break</info>(IntellijIdeaRulezzz)

<info descr="null">message</info>( fff=ENV{not_variable} #BadChar=\d\g\j
    fff=<info descr="null">${<weak_warning descr="Possibly not defined Variable: variable_name">variable_name</weak_warning>}</info><info descr="null">${<weak_warning descr="Possibly not defined Variable: YYY">YYY</weak_warning>}</info> <info descr="null">${R${<weak_warning descr="Possibly not defined Variable: f">f</weak_warning>}}</info>
    <info descr="null">$ENV{VAR}</info> <info descr="null">ENV{VAR}</info> fdddf=<info descr="null">$ENV{VAR}</info> <info descr="null">ENV{d${f${<weak_warning descr="Possibly not defined Variable: t">t</weak_warning>}f}f}</info>}not_var}<info descr="null">${<weak_warning descr="Possibly not defined Variable: ff">ff</weak_warning>}</info>
    <info descr="null">${outer_${<weak_warning descr="Possibly not defined Variable: inner_variable">inner_variable</weak_warning>}_variable}</info>==<info descr="null">${<info descr="null">CMAKE_AR</info>}</info>==<info descr="null">${<info descr="null">CMAKE_MATCH_4</info>}</info>==<info descr="null">${<info descr="null">CMAKE_ASM_COMPILER_AR</info>}</info>
    <info descr="null">${outer_${<info descr="null">APPLE</info>}_${variable}}</info> <info descr="null">${<weak_warning descr="Possibly not defined Variable: CMAKE_CONFIG_TYPE">CMAKE_CONFIG_TYPE</weak_warning>}</info> <info descr="null">ENV{<info descr="null">CMAKE_CONFIG_TYPE</info>}</info> <info descr="null">${ddd_$ENV{<info descr="null">CMAKE_CONFIG_TYPE</info>}}</info>
    E $ <info descr="null">$$$/hhh</info> <info descr="null">hhh$$/dd</info> <info descr="null">$/$f</info> <info descr="null">ALLOW_DUPLICATE_CUSTOM_TARGETS</info> <warning descr="Deprecated property">TEST_INCLUDE_FILE</warning> <warning descr="Deprecated property">COMPILE_DEFINITIONS_hh</warning>
)
<info descr="null">set</info>(<info descr="null">ENV{<info descr="null">CFLAGS</info>}</info> <info descr="null">ENV{varEnv}</info>)
if (<info descr="null">ENV{varEnv}</info> "<info descr="null">$ENV{varEnv}</info>string" <info descr="null">$ENV{<info descr="null">CFLAGS</info>}</info>) endif()
#[ [
foreach(<info descr="null">arg</info>
    NoSpace
    <info descr="null">UnquotedLegacy""</info> \"fff\"Not_Legacy <info descr="null">/g$$E${h}h""ghg</info>
    <info descr="null">$/$//-Da="b c"</info> <info descr="null">d" "</info> <info descr="null">-Da=$(v)</info> <info descr="null">a" "b"c"f$$${not_var}</info>
        $(<info descr="null">b$(b)</info>) <info descr="null">/f" "$(f)" "</info> "jhj"jhj <info descr="null">/tmp/ME-NOTFOUND</info>
    Escaped\ Space  space$$$ $$spacE s$$pacEN spac$ENV $$$$ $$$
    # line comment
    <info descr="null">APPLE</info> "APPLE" <info descr="null">cENV<info descr="null">${x}</info>$/$d</info>   This;Divides;Into;Five;Arguments   Escaped\;Semicolon
    ${not_variable${^£%$&*}} <info descr="null">${<weak_warning descr="Possibly not defined Variable: \;\&\n">\;\&\n</weak_warning>}</info> \$ENV{not_var} \${not_var} "\${not_var}"
    ${<error descr="'\"', CMakeTokenType.(, CMakeTokenType.bracket_argument, CMakeTokenType.bracket_comment, CMakeTokenType.line_comment, CMakeTokenType.unquoted_argument or CMakeTokenType.unquoted_argument_maybe_var_def expected, got '\'">\</error>y\u\i} ${var_not_seen} ON set(())
    )
  <info descr="null">message</info>("<info descr="null">${arg}</info>")
endforeach()

#[[This is a bracket comment.
It runs until the close bracket.]]
#[==
#
<info descr="null">message</info>("First Argument\n" #[===[Bracket Comment]===] "Second Argument")

# This is a line comment.
<info descr="null">message</info>("First Argument\n" # This is a line comment :)
        "Second Argument") # This is a line comment.

<info descr="null">set</info>(srcs a.c b.c c.c) # sets "srcs" to "a.c;b.c;c.c"
<info descr="null">set</info>(<info descr="null">x</info> a "b;c") # sets "x" to "a;b;c", not "a;b\;c"

<info descr="null">set</info>(<info descr="null">_base</info> fff)
macro(temp_name <info descr="null">fname</info>)
  <info descr="null">set</info>(<info descr="null">_base</info> ggg)
  if(<info descr="null">${<info descr="null">ARGC</info>}</info> <info descr="null">GREATER</info> 1) # Have to escape ARGC to correctly compare
    <info descr="null">set</info>(<info descr="null">_base</info> <info descr="null">${<info descr="null">ARGV1</info>}</info> "<info descr="null">${<info descr="null">ARGV2</info>}</info>")
  elseif(<info descr="null">${<info descr="null">ARGC</info>}</info> <info descr="null">GREATER</info> 1)
    <info descr="null">set</info>(<info descr="null">_base</info> ".cmake-tmp")
  else(<info descr="null">${<info descr="null">ARGC</info>}</info> <info descr="null">GREATER</info> 1)
    <info descr="null">set</info>(<info descr="null">_base</info> ".cmake-tmp1111")
    bbbbb(<info descr="null">${_base}</info>)
    if()
        ccc("<info descr="null">${_base}</info>")
        <info descr="null">set</info>(<info descr="null">_base</info> jjjj)
    endif()
  endif(<info descr="null">${<info descr="null">ARGC</info>}</info> <info descr="null">GREATER</info> 1)
  <info descr="null">set</info>(<info descr="null">_counter</info> 0)
  while(<info descr="null">EXISTS</info> "<info descr="null">${_base}</info>1<info descr="null">${_counter}</info>")
    <info descr="null">math</info>(EXPR <info descr="null">${_base}</info> <info descr="null">_counter</info> "<info descr="null">${_counter}</info> + 1")
  endwhile(<info descr="null">EXISTS</info> "<info descr="null">${_base}</info>2<info descr="null">${_counter}</info>")
  <info descr="null">set</info>(<info descr="null">${fname}</info> <info descr="null">${_base}</info>3<info descr="null">${_counter}</info>)
endmacro(temp_name)

<info descr="null">set</info>(<info descr="null">var3</info> 3)
<info descr="null">set</info>(<info descr="null">CMAKE_CXX_FLAGS</info> "<info descr="null">${<info descr="null">CMAKE_CXX_FLAGS</info>}</info> bla bla bla")
if (<info descr="null">NOT</info> <info descr="null">var1</info> <info descr="null"><info descr="null">CMAKE_CXX_FLAGS</info></info>
    <info descr="null">${var1}</info> #${var3}
    "<info descr="null">${var1}</info>"
    "var1"
    <info descr="null">var3</info> )
    <info descr="null">set</info>(<info descr="null">var1</info> 1)
else()
    <info descr="null">set</info>(<info descr="null">var1</info> 3)
endif()
<info descr="null">set</info>(<info descr="null">var2</info> 2)
#set(var2 2)
<info descr="null">message</info>(<info descr="null">${var1}</info>
        "<info descr="null">${var1}</info>"
        <info descr="null">${var2}</info> <info descr="null">${tensorflow_demo_sources}</info> <info descr="null">${<weak_warning descr="Possibly not defined Variable: tensorflow_source_dir">tensorflow_source_dir</weak_warning>}</info>
        "<info descr="null">${var2}</info>")
<info descr="null">set</info>(<info descr="null">tensorflow_demo_sources</info> test)

FAKE_COMMAND_NAME_FOR_VAR_DECLARATION_CREATION_1234567890(ffff)

function(fun1)
  <info descr="null">set</info>(<info descr="null">var_fun1</info> 1)
  fun2()
endfunction(fun1)

function(fun2)
  <info descr="null">message</info>(2<info descr="null">${var_fun1}</info>2)
endfunction(fun2)

# Evaluate expression
# Suggestion from the Wiki: http://cmake.org/Wiki/CMake/Language_Syntax
# Unfortunately, no built-in stuff for this: http://public.kitware.com/Bug/view.php?id=4034
macro(eval <info descr="null">expr</info>)
  temp_name(<info descr="null">_fname</info>)
  <info descr="null">set</info>(<info descr="null">_fname</info> <info descr="null">${_fname}</info>+1<info descr="null">${expr}</info>)
  <info descr="null">file</info>(<info descr="null">WRITE</info> <info descr="null">${_fname}</info> "<info descr="null">${expr}</info>")
  <info descr="null">include</info>(<info descr="null">${_fname}</info>)
  <info descr="null">file</info>(<info descr="null">REMOVE</info> trr$ <info descr="null">${_fname}</info>)
endmacro(eval)

eval("message(\"Hai\")")

<info descr="null">set</info>(<info descr="null">funcs</info> a;b)
macro(test_a <info descr="null">arg</info>)
  <info descr="null">message</info>("A: <info descr="null">${arg}</info>")
endmacro(test_a)
macro(test_b <info descr="null">arg</info>)
  <info descr="null">message</info>("B: <info descr="null">${arg}</info>")
endmacro(test_b)

foreach(<info descr="null">func</info> <info descr="null">${funcs}</info>)
  <info descr="null">set</info>(<info descr="null">func_name</info> test_<info descr="null">${func}</info>)
  eval("<info descr="null">${func_name}</info>(\"Test\")")
endforeach(<info descr="null">func</info>)