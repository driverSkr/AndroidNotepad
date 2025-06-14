cmake_minimum_required(VERSION 3.22.1)


project("native_image_lib")

set(ncnn_DIR ${CMAKE_SOURCE_DIR}/libs/ncnn-20240410-android-vulkan/${ANDROID_ABI}/lib/cmake/ncnn)
find_package(ncnn REQUIRED)

set(OpenCV_DIR ${CMAKE_SOURCE_DIR}/libs/opencv-mobile-4.9.0-android/sdk/native/jni)
find_package(OpenCV REQUIRED)

include_directories(${OpenCV_INCLUDE_DIRS})

add_library(
        native_image_lib
        SHARED
        native_image_lib.cpp
        objrem_pre_post_processing.cpp
        opencv_utils.cpp
        scrfdncnn.cpp
        scrfd.cpp
)

find_library(
        log-lib
        log)


target_link_libraries(
        native_image_lib
        jnigraphics
        ${log-lib}
        ncnn
        mediandk
        ${OpenCV_LIBS})
