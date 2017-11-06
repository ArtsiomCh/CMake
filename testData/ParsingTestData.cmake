# Line Comment
#[[This is a bracket comment.
It runs until the close bracket.]]
unknown_command(
	unquoted_argument=${outer_${inner_variable}_variable}/followed/by/path
	ENV{environmental_variable_reference})
set( <-this_is_known_CMake_Command arg1;arg2;arg3
	PUBLIC with_known_CMake_Property
	#[=[with Bracket Comment]=] AND with_known_CMake_Operator
	${CMAKE_CXX_FLAGS} with_known_CMake_Variable
	UnquotedLegacy"fff"ghg -Da="b c"" " -Da=$(v) a" "b"c"f$$$ )
if()
    message( "This is a quoted argument containing multiple lines.
    This is always one argument even though it contains a ; character.
    Both \\-escape sequences and ${variable} references are evaluated.
    The text does not end on an escaped double-quote like \".
    It does end in an unescaped double quote.")
    message( [=[
    This is the first line in a bracket argument with bracket length 1.
    No \-escape sequences or ${variable} references are evaluated.
    This is always one argument even though it contains a ; character.
    The text does not end on a closing bracket of length 0 like ]].
    It does end in a closing bracket of length 1.
    ]=])
endif()