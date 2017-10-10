# Examples from cmake.org/cmake/help/latest/manual/cmake-language.7.html

add_executable(hello world.c)

if(FALSE AND (FALSE OR TRUE)) # evaluates to FALSE
    set()
endif()

message( [=[jhgjhgj]=] [=[
This is the first line in a bracket argument with bracket length 1.
No \-escape sequences or ${variable} references are evaluated.
This is always one argument even though it contains a ; character.
The text does not end on a closing bracket of length 0 like ]].
It does end in a closing bracket of length 1.
]=]
	ggggg$[[#]]	[=====[ ="" #ghghgh ]====] ]=====]
)

message("This is a quoted argument containing multiple lines.
This is always one argument even though it contains a ; character.
Both \\-escape sequences and $$$${variable}$$$$ $references are evaluated.
The text does not end on an escaped double-quote like \".
It does end in an unescaped double quote.
")

message("\
This is the first line of a quoted argument. \
In fact it is the only line but since it is long \
the source code uses line continuation.\
")

message( fff=ENV{not_variable} BadChar=\d\g\j
    fff=${variable_name} hh$$${R${f}}
    $ENV{VAR} ENV{VAR} fdddf=$ENV{VAR} ENV{d${f${t}f}f}
    ${outer_${inner_variable}_variable}
    ${outer_${APPLE}_${variable}}
    E $ $$$/hhh hhh$$/dd $/$f
)

foreach(arg
    NoSpace
    UnquotedLegacy"fff"ghg/g$$E${h}h""ghg
    $/$//-Da="b c"" " -Da=$(v) a" "b"c"f$$$
        $(bb)/f" "$(f)" "
    Escaped\ Space  space$$$ $$spacE s$$pacEN spac$ENV $$$$ $$$APPLE cENV${x}$/$d
    This;Divides;Into;Five;Arguments
    Escaped\;Semicolon
    )
  message("${arg}")
endforeach()

#[[This is a bracket comment.
It runs until the close bracket.]]
#[==
#
message("First Argument\n" #[===[Bracket Comment]===] "Second Argument")

# This is a line comment.
message("First Argument\n" # This is a line comment :)
        "Second Argument") # This is a line comment.

set(srcs a.c b.c c.c) # sets "srcs" to "a.c;b.c;c.c"
set(x a "b;c") # sets "x" to "a;b;c", not "a;b\;c"




macro(temp_name fname)
  if(${ARGC} GREATER 1) # Have to escape ARGC to correctly compare
    set(_base ${ARGV1})
  else(${ARGC} GREATER 1)
    set(_base ".cmake-tmp")
  endif(${ARGC} GREATER 1)
  set(_counter 0)
  while(EXISTS "${_base}${_counter}")
    math(EXPR _counter "${_counter} + 1")
  endwhile(EXISTS "${_base}${_counter}")
  set(${fname} "${_base}${_counter}")
endmacro(temp_name)

# Evaluate expression
# Suggestion from the Wiki: http://cmake.org/Wiki/CMake/Language_Syntax
# Unfortunately, no built-in stuff for this: http://public.kitware.com/Bug/view.php?id=4034
macro(eval expr)
  temp_name(_fname)
  file(WRITE ${_fname} "${expr}")
  include(${_fname})
  file(REMOVE trr$ ${_fname})
endmacro(eval)

eval("message(\"Hai\")")

set(funcs a;b)
macro(test_a arg)
  message("A: ${arg}")
endmacro(test_a)
macro(test_b arg)
  message("B: ${arg}")
endmacro(test_b)

foreach(func ${funcs})
  set(func_name test_${func})
  eval("${func_name}(\"Test\")")
endforeach(func)
