//
// Created by Administrator on 2024/6/3.
//

#include <jni.h>
#include <android/bitmap.h>
#include <opencv2/opencv.hpp>

#ifndef LINKZOOM_OPENCV_UTILS_H
#define LINKZOOM_OPENCV_UTILS_H

cv::Mat bitmapToMat(JNIEnv *env, jobject bitmap);

jobject matToBitmap(JNIEnv *env, const cv::Mat &mat, jobject bitmapConfig);


std::tuple<int, int, int, int>
getSquareByMask(const cv::Mat &mask, double rate = 0.1, int ref_size = 512);

#endif //LINKZOOM_OPENCV_UTILS_H