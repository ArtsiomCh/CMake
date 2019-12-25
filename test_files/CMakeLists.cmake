cmake_version(a 54544)

#jgjjhgjgjgjgjghjgjgjggjjggj
[=[
    lklknllkn
]=]

set_config_specific_property("OUTPUT_DIRECTORY" "${CMAKE_CURRENT_SOURCE_DIR}$<$<NOT:$<STREQUAL:${CMAKE_VS_PLATFORM_NAME},Win32>>:/${CMAKE_VS_PLATFORM_NAME}>/${PROPS_CONFIG}")