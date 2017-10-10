cmake_minimum_required(VERSION 2.8.4)

project(SeetaFace)

# Build options
option(BUILD_EXAMPLE  "Set to ON to build example"  ON)

# Use C++11
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O2")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -msse4.1")

# Build shared library
include_directories(FaceDetection/include)
include_directories(FaceAlignment/include)
include_directories(FaceIdentification/include)

aux_source_directory(FaceDetection/src fd_src)
aux_source_directory(FaceDetection/src/classifier fd_src)
aux_source_directory(FaceDetection/src/feat fd_src)
aux_source_directory(FaceDetection/src/io fd_src)
aux_source_directory(FaceDetection/src/util fd_src)
aux_source_directory(FaceAlignment/src fa_src)
aux_source_directory(FaceIdentification/src fr_src)
aux_source_directory(FaceIdentification/src/tools fr_src)

SET(LIBRARY_OUTPUT_PATH ../lib)
add_library(seeta_fd_lib SHARED ${fd_src})
add_library(seeta_fa_lib SHARED ${fa_src})
add_library(seeta_fr_lib SHARED ${fr_src})

# Build example
if (BUILD_EXAMPLE)
    message(STATUS "Build with example.")
    find_package(OpenCV)
    if (NOT OpenCV_FOUND)
        message(WARNING "OpenCV not found. Test will not be built.")
    else()
        include_directories(${OpenCV_INCLUDE_DIRS})
        list(APPEND seeta_fd_lib seeta_fa_lib seeta_fr_lib ${OpenCV_LIBS})
        
        add_executable(seetaface_example seetaface_example.cpp)
        target_link_libraries(seetaface_example seeta_fd_lib seeta_fa_lib seeta_fr_lib ${OpenCV_LIBS})
    endif()
endif()
