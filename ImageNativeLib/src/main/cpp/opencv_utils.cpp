//
// Created by Administrator on 2024/6/3.
//

#include "opencv_utils.h"


// 辅助函数：Bitmap 转 cv::Mat
cv::Mat bitmapToMat(JNIEnv *env, jobject bitmap) {
    AndroidBitmapInfo info;
    void *pixels;
    cv::Mat mat;

    AndroidBitmap_getInfo(env, bitmap, &info);
    AndroidBitmap_lockPixels(env, bitmap, &pixels);
    if (info.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
        mat = cv::Mat(info.height, info.width, CV_8UC4, pixels);
    } else if (info.format == ANDROID_BITMAP_FORMAT_RGB_565) {
        mat = cv::Mat(info.height, info.width, CV_8UC2, pixels);
    }
    AndroidBitmap_unlockPixels(env, bitmap);
    return mat;
}

// 辅助函数：cv::Mat 转 Bitmap
jobject matToBitmap(JNIEnv *env, const cv::Mat &mat, jobject bitmapConfig) {
    jclass bitmapClass = env->FindClass("android/graphics/Bitmap");
    jmethodID createBitmapMethod = env->GetStaticMethodID(bitmapClass, "createBitmap",
                                                          "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");

    jobject bitmap = env->CallStaticObjectMethod(bitmapClass, createBitmapMethod, mat.cols,
                                                 mat.rows, bitmapConfig);

    void *pixels;
    AndroidBitmap_lockPixels(env, bitmap, &pixels);
    cv::Mat tmp(mat.rows, mat.cols, CV_8UC4, pixels);

    if (mat.type() == CV_8UC4) {
        mat.copyTo(tmp);
    } else if (mat.type() == CV_8UC3) {
        cv::cvtColor(mat, tmp, cv::COLOR_RGB2RGBA);
    } else if (mat.type() == CV_8UC1) {
        cv::cvtColor(mat, tmp, cv::COLOR_GRAY2RGBA);
    }

    AndroidBitmap_unlockPixels(env, bitmap);
    return bitmap;
}

std::tuple<int, int, int, int>
getSquareByMask(const cv::Mat &mask, double rate, int ref_size) {
    int h = mask.rows;
    int w = mask.cols;

    std::vector<int> y_coords, x_coords;
    for (int y = 0; y < h; ++y) {
        for (int x = 0; x < w; ++x) {
            if (mask.at<uchar>(y, x) > 0) {
                y_coords.push_back(y);
                x_coords.push_back(x);
            }
        }
    }

    int x_min = *std::min_element(x_coords.begin(), x_coords.end());
    int x_max = *std::max_element(x_coords.begin(), x_coords.end());
    int y_min = *std::min_element(y_coords.begin(), y_coords.end());
    int y_max = *std::max_element(y_coords.begin(), y_coords.end());

    int enlarge_x = static_cast<int>(rate * (x_max - x_min));
    int enlarge_h = static_cast<int>(rate * (y_max - y_min));

    x_min = std::max(0, x_min - enlarge_x);
    x_max = std::min(w, x_max + enlarge_x);
    y_min = std::max(0, y_min - enlarge_h);
    y_max = std::min(h, y_max + enlarge_h);

    int box_w = x_max - x_min;
    int box_h = y_max - y_min;

    int c_x = (x_max + x_min) / 2;
    int c_y = (y_max + y_min) / 2;

    int cut_size = std::max(box_h, box_w);
    int half_size = (cut_size > ref_size) ? cut_size / 2 : ref_size / 2;

    x_min = c_x - half_size;
    x_max = c_x + half_size;
    y_min = c_y - half_size;
    y_max = c_y + half_size;

    x_min = (x_max > w) ? (x_min - (x_max - w)) : x_min;
    x_max = (x_min < 0) ? (x_max - x_min) : x_max;
    y_min = (y_max > h) ? (y_min - (y_max - h)) : y_min;
    y_max = (y_min < 0) ? (y_max - y_min) : y_max;

    x_min = std::max(0, x_min);
    x_max = std::min(w, x_max);
    y_min = std::max(0, y_min);
    y_max = std::min(h, y_max);

    return std::make_tuple(x_min, y_min, x_max, y_max);
}