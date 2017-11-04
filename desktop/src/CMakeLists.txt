set(SRCS 
    main.cpp
    MusicEncoder.hpp
    MusicEncoder.cpp)

if (GUI_BUILD)
    add_definitions(-DGUI_BUILD_VAR)

    set(LIBS ${Qt5Core_LIBRARIES} ${Qt5Widgets_LIBRARIES} ${Qt5Gui_LIBRARIES})
    set(QT_SRCS
                ${SRCS}
                EncoderWindow.h
                EncoderWindow.cpp
                AboutWindow.h
                AboutWindow.cpp)
    set(MOC_SRCS 
                EncoderWindow.h
                AboutWindow.h)
    set(UIC_SRCS 
                EncoderWindow.ui
                AboutWindow.ui)
    set(RCC_SRCS vkencodemusic.qrc)

    include_directories(${Qt5Core_INCLUDE_DIRS}
                        ${Qt5Gui_INCLUDE_DIRS}
                        ${Qt5Widgets_INCLUDE_DIRS}
                        ${CMAKE_CURRENT_BINARY_DIR})

    qt5_wrap_ui(UICS ${UIC_SRCS})
    qt5_wrap_cpp(MOCS ${MOC_SRCS})
    qt5_add_resources(RCCS ${RCC_SRCS})

    add_executable(VKEncodeMusic ${QT_SRCS} ${MOCS} ${UICS} ${RCCS})
    target_link_libraries(VKEncodeMusic ${LIBS})
else()
    add_executable(VKEncodeMusic ${SRCS})
endif() 
