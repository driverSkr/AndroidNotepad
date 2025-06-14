//
// Created by Administrator on 2024/6/7.
//

#ifndef LINKZOOM_OBJREM_PRE_POST_PROCESSING_H
#define LINKZOOM_OBJREM_PRE_POST_PROCESSING_H

#include <jni.h>
#include <string>
#include <cstdint>
#include <android/bitmap.h>
#include <android/log.h>
#include <vector>
#include <stack>
#include <algorithm>
#include <cmath>
#include <opencv2/opencv.hpp>
#include "opencv_utils.h"
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>


class objrem_pre_post_processing {
public:
    std::vector<cv::Rect> CalcBoxesByMask(cv::Mat mask);

    cv::Rect BoxClip(cv::Rect box, int w, int h);

    cv::Rect ExpandBox(cv::Rect box, int imgH, int imgW, int runSize, int margin);

    cv::Mat DownsamplingImageByMax(const cv::Mat &input, int maxLimit);

    cv::Mat PaddingImage(const cv::Mat &input, int squareSize,cv::Rect &rect);

};


#endif //LINKZOOM_OBJREM_PRE_POST_PROCESSING_H
