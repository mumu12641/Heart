cmake_minimum_required(VERSION 3.10.2)

# Declares and names the project.

project("wave_lib")


include_directories(
        ${CMAKE_SOURCE_DIR}/include/wave)

add_library( # Sets the name of the library.
        wave_lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        wave_lib.cpp)

#add_library(mp3lame STATIC IMPORTED)
#set_target_properties(mp3lame PROPERTIES IMPORTED_LOCATION
#        ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libmp3lame.a)
#set_target_properties(mp3lame PROPERTIES IMPORTED_LOCATION
#        ${CMAKE_SOURCE_DIR}/../jniLibs/arm64-v8a/libmp3lame.a)

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log )

#target_link_libraries(lame_lib
#        mp3lame)
