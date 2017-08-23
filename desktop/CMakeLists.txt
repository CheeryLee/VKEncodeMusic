cmake_minimum_required(VERSION 2.8) 

project(VKEncodeMusic)

# find Qt
find_package(Qt5 REQUIRED COMPONENTS Core Widgets Gui)

if (Qt5Core_FOUND AND Qt5Widgets_FOUND AND Qt5Gui_FOUND)
    message(STATUS "Looking for Qt - found")
    option(GUI_BUILD "Build project with GUI" ON)
else()
    message(STATUS "Looking for Qt - not found")
endif()

# set build type
if (NOT CMAKE_BUILD_TYPE)
    set(CMAKE_BUILD_TYPE Release)
endif()

subdirs(src)
