//
// Created by Administrator on 2024/6/7.
//

#include "objrem_pre_post_processing.h"


cv::Rect objrem_pre_post_processing::BoxClip(cv::Rect box, int w, int h) {
    cv::Rect out(box);
    out.x = (out.x < 0) ? 0 : out.x;
    out.y = (out.y < 0) ? 0 : out.y;
    out.width = (out.width > w) ? w : out.width;
    out.height = (out.height > h) ? h : out.height;

    return out;
}

std::vector<cv::Rect> objrem_pre_post_processing::CalcBoxesByMask(cv::Mat mask) {
    int h = mask.rows;
    int w = mask.cols;
    std::vector<std::vector<cv::Point>> contours;
    findContours(mask, contours, cv::RETR_EXTERNAL, cv::CHAIN_APPROX_SIMPLE);
    if (contours.empty()) {
        return {};
    }

    std::vector<cv::Point> total_points;
    std::vector<cv::Rect> boxes;
    for (auto cnt: contours) {
        total_points.insert(total_points.end(), cnt.begin(), cnt.end());
        cv::Rect box = boundingRect(cnt);
        boxes.push_back(BoxClip(box, w, h));
    }
    cv::Rect bigBox = boundingRect(total_points);
    boxes.push_back(bigBox);
    return boxes;
}

cv::Rect
objrem_pre_post_processing::ExpandBox(cv::Rect box, int imgH, int imgW, int runSize, int margin) {
    int cx = box.x + box.width / 2;
    int cy = box.y + box.height / 2;

    int w = box.width + margin * 2;
    int h = box.height + margin * 2;
    if (w < runSize) w = runSize;
    if (h < runSize) h = runSize;
    w = std::max(w, h);
    h = std::max(w, h);

    int left_ = cx - w / 2;
    int right_ = cx + w / 2;
    int top_ = cy - h / 2;
    int bottom_ = cy + h / 2;

    int left = std::max(left_, 0);
    int right = std::min(right_, imgW);
    int top = std::max(top_, 0);
    int bottom = std::min(bottom_, imgH);

    //try to get more context when crop around image edge
    right = left_ < 0 ? right + abs(left_) : right;
    left = right_ > imgW ? left - (right_ - imgW) : left;
    bottom = top_ < 0 ? bottom + abs(top_) : bottom;
    top = bottom_ > imgH ? top - (bottom_ - imgH) : top;

    left = std::max(left, 0);
    right = std::min(right, imgW);
    top = std::max(top, 0);
    bottom = std::min(bottom, imgH);

    cv::Rect box_new(cv::Point(left, top), cv::Point(right, bottom));

    return box_new;
}

cv::Mat objrem_pre_post_processing::DownsamplingImageByMax(const cv::Mat &input, int maxLimit) {
    int h = input.rows;
    int w = input.cols;
    if (std::max(h, w) > maxLimit) {
        float ratio = maxLimit * 1.0 / std::max(h, w);
        int new_h = int(h * ratio + 0.5);
        int new_w = int(w * ratio + 0.5);
        cv::Mat dst;
        resize(input, dst, cv::Size(new_w, new_h), 0, 0, cv::INTER_CUBIC);
        return dst;
    } else {
        return input;
    }
}

cv::Mat
objrem_pre_post_processing::PaddingImage(const cv::Mat &input, int squareSize, cv::Rect &rect) {
    int h = input.rows;
    int w = input.cols;
    int h_pad = squareSize - h;
    int w_pad = squareSize - w;

    rect.x = 0;
    rect.y = 0;
    rect.width = w;
    rect.height = h;

    if (h_pad == 0 && w_pad == 0) {
        return input;
    }
    cv::Mat dst;
//    cv::copyMakeBorder(input, dst, 0, h_pad, 0, w_pad, cv::BORDER_CONSTANT,
//                       cv::Scalar(0, 0, 0, 255));
    cv::copyMakeBorder(input, dst, 0, h_pad, 0, w_pad, cv::BORDER_REPLICATE);
//    cv::resize(input, dst, cv::Size(squareSize, squareSize));
    return dst;
}


extern "C"
{

JNIEXPORT jobject
JNICALL
Java_com_hitpaw_imagehandler_ImageNativeUtils_objremPreProcessingPy(JNIEnv *env, jobject thiz,
                                                                    jobject bitmapIn,
                                                                    jobject bitmapMask,
                                                                    jobject bitmapConfig) {
    cv::Mat img = bitmapToMat(env, bitmapIn);
    cv::Mat mask = bitmapToMat(env, bitmapMask);

    if (cv::sum(mask)[0] == 0) {
        return nullptr;
    }
    //转灰度
    cvtColor(mask, mask, cv::COLOR_RGBA2GRAY);

    // 膨胀掩膜
    cv::Mat kernel = cv::getStructuringElement(cv::MORPH_RECT, cv::Size(21, 21));
    cv::dilate(mask, mask, kernel, cv::Point(-1, -1), 1);

    cv::Rect bbox;
    int x_min, y_min, x_max, y_max;
    std::tie(x_min, y_min, x_max, y_max) = getSquareByMask(mask);
    bbox.x = x_min;
    bbox.y = y_min;
    bbox.width = x_max - x_min;
    bbox.height = y_max - y_min;
    //裁
    cv::Mat img_cut = img(bbox);
    cv::Mat mask_cut = mask(bbox);

    //resize
    int model_size = 512;
    cv::resize(img_cut, img_cut, cv::Size(model_size, model_size));
    cv::resize(mask_cut, mask_cut, cv::Size(model_size, model_size), cv::INTER_NEAREST);


    jobject bitmapOutImg = matToBitmap(env, img_cut, bitmapConfig);
    jobject bitmapOutMask = matToBitmap(env, mask_cut, bitmapConfig);


    jclass objremClass = env->FindClass("com/hitpaw/imagehandler/bean/ObjremPreProcessingBean");
    jmethodID objremConstructor = env->GetMethodID(objremClass, "<init>",
                                                   "(Landroid/graphics/Bitmap;Landroid/graphics/Bitmap;IIIIIIII)V");

    return env->NewObject(objremClass, objremConstructor, bitmapOutImg, bitmapOutMask,
                          (int32_t) bbox.x,
                          (int32_t) bbox.y,
                          (int32_t) bbox.width,
                          (int32_t) bbox.height,
                          (int32_t) 0,
                          (int32_t) 0,
                          (int32_t) model_size,
                          (int32_t) model_size);
}


JNIEXPORT jobject
JNICALL
Java_com_hitpaw_imagehandler_ImageNativeUtils_objremPreProcessingCpp(JNIEnv *env, jobject thiz,
                                                                     jobject bitmapIn,
                                                                     jobject bitmapMask,
                                                                     jobject bitmapConfig) {
    cv::Mat img = bitmapToMat(env, bitmapIn);
    cv::Mat mask = bitmapToMat(env, bitmapMask);
    if (cv::sum(mask)[0] == 0) {
        return nullptr;
    }
    //转灰度
    cvtColor(mask, mask, cv::COLOR_RGBA2GRAY);

    int h = mask.rows;
    int w = mask.cols;
    cv::threshold(mask, mask, 127, 255, cv::THRESH_BINARY);

    int runSize = 512;
    cv::Mat maskDilate;
    int iterations = std::max(h, w) / runSize + 1;

    cv::dilate(mask, maskDilate,
               cv::getStructuringElement(cv::MORPH_CROSS, cv::Size(5, 5)),
               cv::Point(-1, -1), iterations);

    if (maskDilate.channels() == 3) {
        cv::cvtColor(maskDilate, maskDilate, cv::COLOR_RGB2GRAY);
    }
    cv::threshold(maskDilate, maskDilate, 0, 255, cv::THRESH_BINARY);

    int iterations2 = std::max(h, w) / 512 + 1;
    cv::dilate(maskDilate, maskDilate, getStructuringElement(cv::MORPH_CROSS, cv::Size(3, 3)),
               cv::Point(-1, -1), iterations2);

    // find big box
    objrem_pre_post_processing processor;
    std::vector<cv::Rect> boxes = processor.CalcBoxesByMask(maskDilate);
    if (boxes.empty())return nullptr;

    cv::Rect cropBox = processor.ExpandBox(boxes.back(), h, w, 512, 64);
    cv::Mat cropImg = img(cropBox);
    cv::Mat cropMask = maskDilate(cropBox);
    int cropImgH = cropImg.rows;
    int cropImgW = cropImg.cols;
    cv::Mat downsize_img = cropImg.clone();
    cv::Mat downsize_mask = cropMask.clone();
    if (cropImgH > runSize || cropImgW > runSize) {
        downsize_img = processor.DownsamplingImageByMax(cropImg, runSize);
        downsize_mask = processor.DownsamplingImageByMax(cropMask, runSize);
    }
    cv::threshold(downsize_mask, downsize_mask, 127, 255, cv::THRESH_BINARY);

    //输出给后端的文件
    cv::Rect imgInBorder;
    cv::Mat pad_img = processor.PaddingImage(downsize_img, runSize, imgInBorder);
    cv::Mat pad_mask = processor.PaddingImage(downsize_mask, runSize, imgInBorder);

    __android_log_print(ANDROID_LOG_DEBUG, "objremPreProcessingCpp",
                        "imgInBorder x %d y %d w %d h %d", imgInBorder.x, imgInBorder.y,
                        imgInBorder.width, imgInBorder.height);


    jobject bitmapOutImg = matToBitmap(env, pad_img, bitmapConfig);
    jobject bitmapOutMask = matToBitmap(env, pad_mask, bitmapConfig);


    jclass objremClass = env->FindClass("com/hitpaw/imagehandler/bean/ObjremPreProcessingBean");
    jmethodID objremConstructor = env->GetMethodID(objremClass, "<init>",
                                                   "(Landroid/graphics/Bitmap;Landroid/graphics/Bitmap;IIIIIIII)V");

    return env->NewObject(objremClass, objremConstructor, bitmapOutImg, bitmapOutMask,
                          (int32_t) cropBox.x,
                          (int32_t) cropBox.y,
                          (int32_t) cropBox.width,
                          (int32_t) cropBox.height,
                          (int32_t) imgInBorder.x,
                          (int32_t) imgInBorder.y,
                          (int32_t) imgInBorder.width,
                          (int32_t) imgInBorder.height
    );
}

JNIEXPORT jobject
JNICALL
Java_com_hitpaw_imagehandler_ImageNativeUtils_objremPreProcessingMerge(JNIEnv *env, jobject thiz,
                                                                       jobject bitmapIn,
                                                                       jobject bitmapMask,
                                                                       jobject bitmapConfig) {

    cv::Mat img = bitmapToMat(env, bitmapIn);
    cv::Mat mask = bitmapToMat(env, bitmapMask);
    if (img.empty() || mask.empty()) {
        return nullptr;
    }
    if (img.rows != mask.rows) {
        return nullptr;
    }

    cv::Mat result(img.rows, img.cols + mask.cols, img.type());
    img.copyTo(result(cv::Rect(0, 0, img.cols, img.rows)));
    mask.copyTo(result(cv::Rect(img.cols, 0, mask.cols, img.rows)));
    jobject bitmapOutImg = matToBitmap(env, result, bitmapConfig);
    return bitmapOutImg;
}

JNIEXPORT jobject
JNICALL
Java_com_hitpaw_imagehandler_ImageNativeUtils_objremAfterProcessing(JNIEnv *env, jobject thiz,
                                                                    jobject origin_image,
                                                                    jobject lite_image,
                                                                    jobject lite_mask,
                                                                    jobject bitmapConfig,
                                                                    jint x, jint y, jint w,
                                                                    jint h,
                                                                    jint mask_x,
                                                                    jint mask_y,
                                                                    jint mask_w,
                                                                    jint mask_h) {
    cv::Mat originImage = bitmapToMat(env, origin_image);
    cv::Mat liteImage = bitmapToMat(env, lite_image);
    cv::Mat liteMask = bitmapToMat(env, lite_mask);

    if (liteImage.size() != liteMask.size()) {
        return nullptr;
    }
    __android_log_print(ANDROID_LOG_DEBUG, "objremAfterProcessing", "mask w %d h %d", liteMask.rows,
                        liteMask.cols);
    __android_log_print(ANDROID_LOG_DEBUG, "objremAfterProcessing", "crop w %d h %d", mask_w,
                        mask_h);

    liteMask = liteMask(cv::Rect(mask_x, mask_y, mask_w, mask_h));
    liteImage = liteImage(cv::Rect(mask_x, mask_y, mask_w, mask_h));

    if (x < 0 || y < 0 || x + w > originImage.cols || y + h > originImage.rows) {
        return nullptr;
    }

    cv::Mat mask;
    cv::threshold(liteMask, mask, 127, 255, cv::THRESH_BINARY);

    cv::Mat resizedMask;
    cv::resize(mask, resizedMask, cv::Size(w, h));

    if (resizedMask.channels() > 1) {
        cv::cvtColor(resizedMask, resizedMask, cv::COLOR_BGR2GRAY);
    }

    cv::Mat roi = originImage(cv::Rect(x, y, w, h));

    cv::Mat resizedLiteImage;
    cv::resize(liteImage, resizedLiteImage, cv::Size(w, h));

    resizedLiteImage.copyTo(roi, resizedMask);

    jobject resultBitmap = matToBitmap(env, originImage, bitmapConfig);

    return resultBitmap;
}
}
