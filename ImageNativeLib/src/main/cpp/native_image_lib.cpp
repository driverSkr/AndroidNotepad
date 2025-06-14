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
#include <set>

//转换rgb值
#define RGB8888_A(p) (p & (0xff<<24))
#define RGB8888_R(p) (p & (0xff<<16))
#define RGB8888_G(p) (p & (0xff<<8))
#define RGB8888_B(p) (p & (0xff))

#define  LOG_TAG    "native-dev"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C"
JNIEXPORT jobject JNICALL
Java_com_hitpaw_imagehandler_ImageNativeUtils_getHasPixelRectSize(JNIEnv *env, jobject thiz,
                                                                  jobject bitmap) {

    //rect java类
    jclass rectClass = env->FindClass("android/graphics/Rect");
    //无参ID
    jmethodID id = env->GetMethodID(rectClass, "<init>", "()V");
    //Rect对象
    jobject rect = env->NewObject(rectClass, id);

    AndroidBitmapInfo info;
    memset(&info, 0, sizeof(info));
    //获得bitmap的信息
    AndroidBitmap_getInfo(env, bitmap, &info);
    //位图在这里↓
    void *bitmapPixels;
    uint32_t width = info.width;
    uint32_t height = info.height;
    //🔒内存
    AndroidBitmap_lockPixels(env, bitmap, &bitmapPixels);
    int left = (int32_t) width;
    int top = (int32_t) height;
    int right = 0;
    int bottom = 0;
    //根据图片类型不同使用不同的处理方式
    if (info.format == ANDROID_BITMAP_FORMAT_A_8) {
        //透明通道图
        for (int y = 0; y < height; ++y) { //遍历顺序为，从上到下，从左到右
            for (int x = 0; x < width; ++x) {
                void *pixel = nullptr;
                pixel = ((uint8_t *) bitmapPixels) + y * width + x;
                if (*((uint8_t *) pixel) > 0) {
                    left = std::min(left, x);
                    top = std::min(top, y);
                    right = std::max(right, x);
                    bottom = std::max(bottom, y);
                }
            }
        }
    } else if (info.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
        //png图
        for (int y = 0; y < height; ++y) { //遍历顺序为，从上到下，从左到右
            for (int x = 0; x < width; ++x) {
                void *pixel = nullptr;
                pixel = ((uint32_t *) bitmapPixels) + y * width + x;
                uint32_t v = *((uint32_t *) pixel);
                if (RGB8888_A(v) > 0) {
                    left = std::min(left, x);
                    top = std::min(top, y);
                    right = std::max(right, x);
                    bottom = std::max(bottom, y);
                }
            }
        }
    } else {
        //不带透明通道的,直接返回原大小
        left = 0;
        top = 0;
        right = (int32_t) width;
        bottom = (int32_t) height;
    }

    //判断是否是全透明图,全透明就返回0000
    if (left == (int32_t) width & top == (int32_t) height & right == 0 & bottom == 0) {
        left = 0;
        top = 0;
        right = 0;
        bottom = 0;
    }

    LOGI("left%d,top%d,right%d,bottom%d", left, top, right, bottom);
    //解🔒内存
    AndroidBitmap_unlockPixels(env, bitmap);

    env->CallVoidMethod(rect, env->GetMethodID(rectClass, "set", "(IIII)V"), left, top, right,
                        bottom);
    return rect;
}

/**
 * fsd洪水
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_hitpaw_imagehandler_ImageNativeUtils_getConnectedArea(JNIEnv *env, jobject thiz,
                                                               jobject bitmap,
                                                               jobject rect_list) {
    AndroidBitmapInfo bitmapInfo;
    memset(&bitmapInfo, 0, sizeof(bitmapInfo));
    // 获取bitmap的信息
    AndroidBitmap_getInfo(env, bitmap, &bitmapInfo);
    // 位图像素数据
    void *bitmapPixels;
    // 锁定内存
    AndroidBitmap_lockPixels(env, bitmap, &bitmapPixels);

    if (bitmapInfo.format == ANDROID_BITMAP_FORMAT_A_8) { // 透明通道图
        uint8_t *pixelsMarks = new uint8_t[bitmapInfo.width * bitmapInfo.height];
        memset(pixelsMarks, 0, bitmapInfo.width * bitmapInfo.height);

        // 存储矩形区域列表
        // 获取 ArrayList 对象的类和方法
        jclass arrayListClass = env->GetObjectClass(rect_list);
        jmethodID add_method = env->GetMethodID(arrayListClass, "add", "(Ljava/lang/Object;)Z");


        for (int y = 0; y < bitmapInfo.height; ++y) {
            for (int x = 0; x < bitmapInfo.width; ++x) {
                int index = y * bitmapInfo.width + x;
                if (pixelsMarks[index]) {
                    continue;
                }

                uint8_t *a8Value = ((uint8_t *) bitmapPixels) + index;
                if (*a8Value > 0) {
                    // 找到一个透明通道值大于0的像素，进行洪水填充
                    std::stack<std::pair<int, int>> pixelStack;
                    pixelStack.push({x, y});

                    int minX = x;
                    int maxX = x;
                    int minY = y;
                    int maxY = y;

                    while (!pixelStack.empty()) {
                        auto current = pixelStack.top();
                        pixelStack.pop();

                        int currentX = current.first;
                        int currentY = current.second;
                        int currentIndex = currentY * bitmapInfo.width + currentX;

                        if (pixelsMarks[currentIndex]) {
                            continue;
                        }

                        pixelsMarks[currentIndex] = 1;

                        // Update bounding box coordinates
                        minX = std::min(minX, currentX);
                        minY = std::min(minY, currentY);
                        maxX = std::max(maxX, currentX);
                        maxY = std::max(maxY, currentY);

                        // Check surrounding pixels
                        int offsets[4][2] = {{-1, 0},
                                             {1,  0},
                                             {0,  -1},
                                             {0,  1}}; // left, right, top, bottom
                        for (const auto &offset: offsets) {//遍历偏移向量
                            int newX = currentX + offset[0]; //偏移X
                            int newY = currentY + offset[1]; //偏移Y
                            if (newX >= 0 && newX < bitmapInfo.width && newY >= 0 &&
                                newY < bitmapInfo.height) {
                                int newIndex = newY * bitmapInfo.width + newX;
                                if (!pixelsMarks[newIndex]) {
                                    uint8_t alpha = *((uint8_t *) bitmapPixels + newIndex);
                                    if (alpha > 0) {
                                        pixelStack.push({newX, newY});
                                    }
                                }
                            }
                        }
                    }

                    // Create Rect object
                    jclass rectClass = env->FindClass("android/graphics/Rect");
                    jmethodID rectConstructor = env->GetMethodID(rectClass, "<init>", "(IIII)V");
                    jobject rectObject = env->NewObject(rectClass, rectConstructor,
                                                        minX, minY,
                                                        maxX + 1, maxY + 1);
                    env->CallBooleanMethod(rect_list, add_method, rectObject);
                }
            }
        }

        // 释放内存
        delete[] pixelsMarks;
    } else {
        LOGE("for performance reasons, only transparent channel images are supported");
    }

    // 解锁内存
    AndroidBitmap_unlockPixels(env, bitmap);
}


// 计算两个点之间的欧几里得距离
double distance(const std::pair<int, int> &point1, const std::pair<int, int> &point2) {
    int dx = point1.first - point2.first;
    int dy = point1.second - point2.second;
    return std::sqrt(dx * dx + dy * dy);
}


/**
 * openCV获取边缘路径
 *         cv::Mat edges;
 *         cv::Canny(img, edges, 150, 100);
 */
extern "C"
JNIEXPORT jobject JNICALL
Java_com_hitpaw_imagehandler_ImageNativeUtils_getEdgePathByCv(JNIEnv *env, jobject thiz,
                                                              jobject bitmap,
                                                              jintArray contoursPoint) {
    // 获取Bitmap信息
    AndroidBitmapInfo info;
    void *pixels;
    if (AndroidBitmap_getInfo(env, bitmap, &info) < 0) {
        return nullptr;  // 错误处理
    }

    // 获取Bitmap像素数据
    if (AndroidBitmap_lockPixels(env, bitmap, &pixels) < 0) {
        return nullptr;  // 错误处理
    }


    cv::Mat img(info.height, info.width, CV_8UC4, pixels);
    cv::cvtColor(img, img, cv::COLOR_BGR2GRAY);

    cv::Mat compareMask;
    cv::compare(img, 255, compareMask, cv::CMP_EQ);
    int maskPixelCount = cv::countNonZero(compareMask);
    LOGI("MASK PIXEL COUNT %d", maskPixelCount);
    //二值化
//    cv::threshold(img, img, 128, 255, cv::THRESH_BINARY);

    // 使用Canny边缘检测
    cv::Mat edges;
    cv::Canny(img, edges, 100, 150);

    // 查找边缘路径的坐标
    std::vector<std::vector<cv::Point>> externalContours;
    cv::findContours(edges, externalContours, cv::RETR_LIST, cv::CHAIN_APPROX_SIMPLE);
    // 确保至少有一个轮廓
    if (!externalContours.empty()) {
        // 找到具有最大面积的轮廓的索引
        double maxArea = cv::contourArea(externalContours[0]);
        int maxAreaIndex = 0;

        for (int i = 1; i < externalContours.size(); ++i) {
            double area = cv::contourArea(externalContours[i]);
            LOGI("CONTOURS %d AREA %f", i, area);
            if (area > maxArea) {
                maxArea = area;
                maxAreaIndex = i;
            }
        }

        // 使用最大轮廓的索引计算中心点坐标
        cv::Moments moment = cv::moments(externalContours[maxAreaIndex], false);
        cv::Point midPoint = cv::Point(int(moment.m10 / moment.m00), int(moment.m01 / moment.m00));

        // 现在 midPoint 包含了最大轮廓的中心点坐标
        jint *points = env->GetIntArrayElements(contoursPoint, nullptr);
        points[0] = midPoint.x;
        points[1] = midPoint.y;
        points[2] = maskPixelCount;
        env->ReleaseIntArrayElements(contoursPoint, points, 0);
    }


    // 将坐标转换为Java对象（ArrayList）
    // 构建Path对象
    jclass pathClass = env->FindClass("android/graphics/Path");
    jmethodID pathConstructor = env->GetMethodID(pathClass, "<init>", "()V");
    jmethodID pathMoveTo = env->GetMethodID(pathClass, "moveTo", "(FF)V");
    jmethodID pathLineTo = env->GetMethodID(pathClass, "lineTo", "(FF)V");
    jmethodID closePath = env->GetMethodID(pathClass, "close", "()V");

    jobject resultPath = env->NewObject(pathClass, pathConstructor);

    std::vector<std::vector<cv::Point>> contours;
    cv::findContours(img, contours, cv::RETR_EXTERNAL, cv::CHAIN_APPROX_SIMPLE);

    for (const auto &contour: contours) {
        // 用于存储简化后的轮廓
        std::vector<cv::Point> approxContour;
        // 使用多边形逼近算法简化轮廓，参数2是轮廓点到近似轮廓的最大距离，值越大简化越明显
        cv::approxPolyDP(contour, approxContour, 10.0, true);

        for (size_t i = 0; i < approxContour.size(); i++) {
            const auto &point = approxContour[i];
            if (i == 0) {
                env->CallVoidMethod(resultPath, pathMoveTo, static_cast<jfloat>(point.x),
                                    static_cast<jfloat>(point.y));
            } else {
                env->CallVoidMethod(resultPath, pathLineTo, static_cast<jfloat>(point.x),
                                    static_cast<jfloat>(point.y));
            }
        }
        env->CallVoidMethod(resultPath, closePath);
    }
    // 解锁Bitmap像素数据
    AndroidBitmap_unlockPixels(env, bitmap);
    return resultPath;
}


/**
 * 辅助函数：将cv::Mat转换为Android Bitmap
 */
jobject createBitmap(JNIEnv *env, const cv::Mat &mat) {
    jclass bitmapConfigClass = env->FindClass("android/graphics/Bitmap$Config");
    jmethodID createBitmapMethod = env->GetStaticMethodID(bitmapConfigClass, "valueOf",
                                                          "(Ljava/lang/String;)Landroid/graphics/Bitmap$Config;");
    jobject bitmapConfig = env->CallStaticObjectMethod(bitmapConfigClass, createBitmapMethod,
                                                       env->NewStringUTF("ARGB_8888"));

    jclass bitmapClass = env->FindClass("android/graphics/Bitmap");
    jmethodID createBitmap = env->GetStaticMethodID(bitmapClass, "createBitmap",
                                                    "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
    jobject resultBitmap = env->CallStaticObjectMethod(bitmapClass, createBitmap, mat.cols,
                                                       mat.rows, bitmapConfig);

    AndroidBitmapInfo bitmapInfo;
    void *pixels;
    if (AndroidBitmap_getInfo(env, resultBitmap, &bitmapInfo) < 0) return nullptr;
    if (AndroidBitmap_lockPixels(env, resultBitmap, &pixels) < 0) return nullptr;

    if (mat.type() == CV_8UC4) {
        // 不需要转换，直接复制
        mat.copyTo(cv::Mat(bitmapInfo.height, bitmapInfo.width, CV_8UC4, pixels));
    } else {
        // 如果图像不是CV_8UC4类型，将其转换为CV_8UC4
        cv::cvtColor(mat, cv::Mat(bitmapInfo.height, bitmapInfo.width, CV_8UC4, pixels),
                     cv::COLOR_BGR2BGRA);
    }

    AndroidBitmap_unlockPixels(env, resultBitmap);
    return resultBitmap;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_hitpaw_imagehandler_ImageNativeUtils_getEdgeBitmapByCv(JNIEnv *env, jobject thiz,
                                                                jobject srcBitmap,
                                                                jobject maskBitmap,
                                                                jintArray img_rect) {
    // 获取Java中传递的Bitmap对象
    AndroidBitmapInfo srcInfo, maskInfo;
    void *srcPixels, *maskPixels;

    if (AndroidBitmap_getInfo(env, srcBitmap, &srcInfo) < 0) return nullptr;
    if (AndroidBitmap_getInfo(env, maskBitmap, &maskInfo) < 0) return nullptr;

    if (AndroidBitmap_lockPixels(env, srcBitmap, &srcPixels) < 0) return nullptr;
    if (AndroidBitmap_lockPixels(env, maskBitmap, &maskPixels) < 0) return nullptr;

    // 将Bitmap转换为cv::Mat
    // 将 Bitmap 转换为 cv::Mat
    cv::Mat srcMat(srcInfo.height, srcInfo.width, CV_8UC4, srcPixels);
    cv::Mat maskMat(maskInfo.height, maskInfo.width, CV_8UC4, maskPixels);
    // 转换 mask 为灰度图像
    cv::cvtColor(maskMat, maskMat, cv::COLOR_BGR2GRAY);

    if (maskMat.size() != srcMat.size()) {
        // 调整掩码的大小以匹配源图像
        cv::resize(maskMat, maskMat, srcMat.size(), 0, 0, cv::INTER_LINEAR);
    }

    // 创建透明图像，确保它与原图相同大小和类型
    cv::Mat resultMat(srcMat.size(), CV_8UC4, cv::Scalar(0, 0, 0, 0));
    // 将原图中 mask 部分保留，其他部分设置为透明
    srcMat.copyTo(resultMat, maskMat);

    // 解锁Bitmap像素
    AndroidBitmap_unlockPixels(env, srcBitmap);
    AndroidBitmap_unlockPixels(env, maskBitmap);

    // 获取矩形坐标
    jint *rectArray = env->GetIntArrayElements(img_rect, nullptr);
    int x = rectArray[0];
    int y = rectArray[1];
    int width = rectArray[2];
    int height = rectArray[3];
    env->ReleaseIntArrayElements(img_rect, rectArray, 0);

    // 使用矩形区域进行进一步裁剪
    cv::Rect boundingBox(x, y, width, height);
    cv::Mat finalCroppedImage = resultMat(boundingBox);

    // 创建新的Bitmap对象
    jobject outputBitmap = createBitmap(env, finalCroppedImage);

    return outputBitmap;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_hitpaw_imagehandler_ImageNativeUtils_getRgba2GrayByCv(JNIEnv *env, jobject thiz,
                                                               jobject maskBitmap) {

    AndroidBitmapInfo info;
    void *pixels;

    // 获取 Bitmap 信息
    if (AndroidBitmap_getInfo(env, maskBitmap, &info) < 0) {
        // 错误处理
        return NULL;
    }

    // 锁定 Bitmap 并获取像素数据
    if (AndroidBitmap_lockPixels(env, maskBitmap, &pixels) < 0) {
        // 错误处理
        return NULL;
    }

    // 创建 OpenCV Mat 对象
    cv::Mat inputImage(info.height, info.width, CV_8UC4, pixels);

    // 创建 ALPHA_8 格式的输出图像
    cv::Mat outputImage;
    cvtColor(inputImage, outputImage, cv::COLOR_RGBA2GRAY);

    // 解锁 Bitmap
    AndroidBitmap_unlockPixels(env, maskBitmap);

    // 创建一个新的 Bitmap 对象
    jclass bitmapConfigClass = env->FindClass("android/graphics/Bitmap$Config");
    jmethodID valueOfMid = env->GetStaticMethodID(bitmapConfigClass, "valueOf",
                                                  "(Ljava/lang/String;)Landroid/graphics/Bitmap$Config;");

    jstring ALPHA_8 = env->NewStringUTF("ALPHA_8");
    jobject config = env->CallStaticObjectMethod(bitmapConfigClass, valueOfMid, ALPHA_8);

    jclass bitmapClass = env->FindClass("android/graphics/Bitmap");
    jmethodID createBitmapMid = env->GetStaticMethodID(bitmapClass, "createBitmap",
                                                       "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");

    jobject resultBitmap = env->CallStaticObjectMethod(bitmapClass, createBitmapMid,
                                                       outputImage.cols, outputImage.rows,
                                                       config);
    if (resultBitmap == NULL) {
        // 错误处理
        return NULL;
    }

    // 将输出图像数据复制到新的 Bitmap 对象
    if (AndroidBitmap_lockPixels(env, resultBitmap, &pixels) < 0) {
        // 错误处理
        return NULL;
    }

    memcpy(pixels, outputImage.data, outputImage.total() * outputImage.elemSize());

    // 解锁新的 Bitmap
    AndroidBitmap_unlockPixels(env, resultBitmap);

    return resultBitmap;
}



extern "C"
JNIEXPORT jboolean JNICALL
Java_com_hitpaw_imagehandler_ImageNativeUtils_getMixSrcAndAlpha(JNIEnv *env, jobject thiz,
                                                                jobject srcBitmap,
                                                                jobject maskBitmapAlpha,
                                                                jstring filepath, jint resize) {
    AndroidBitmapInfo srcInfo;
    void *srcPixels;
    AndroidBitmap_getInfo(env, srcBitmap, &srcInfo);
    AndroidBitmap_lockPixels(env, srcBitmap, &srcPixels);
    cv::Mat src((int) srcInfo.height, (int) srcInfo.width, CV_8UC4, srcPixels);

    AndroidBitmapInfo maskInfo;
    void *maskPixels;
    AndroidBitmap_getInfo(env, maskBitmapAlpha, &maskInfo);
    AndroidBitmap_lockPixels(env, maskBitmapAlpha, &maskPixels);
    cv::Mat mask((int) maskInfo.height, (int) maskInfo.width, CV_8UC1, maskPixels);

    int originalWidth = src.cols;
    int originalHeight = src.rows;
    float scale = (float) resize / (float) std::max(originalWidth, originalHeight);

    // 计算新的宽度和高度
    int newWidth = static_cast<int>((float) originalWidth * scale);
    int newHeight = static_cast<int>((float) originalHeight * scale);
    cv::resize(src, src, cv::Size(newWidth, newHeight));
    cv::resize(mask, mask, cv::Size(newWidth, newHeight));


    cv::Mat alphaMask(src.size(), CV_8UC1, cv::Scalar(255));
    alphaMask.setTo(cv::Scalar(0), mask);


    // 分离源图像的 RGB 通道和 Alpha 通道
    std::vector<cv::Mat> srcChannels;
    cv::split(src, srcChannels);

    // 替换 Alpha 通道
    if (srcChannels.size() == 4) {
        srcChannels[3] = alphaMask;
    } else {
        srcChannels.push_back(alphaMask);
    }

    // 合并通道以创建 RGBA 图像
    cv::Mat result;
    cv::merge(srcChannels, result);
    cv::cvtColor(result, result, cv::COLOR_BGRA2RGBA);

    // 解锁像素
    AndroidBitmap_unlockPixels(env, srcBitmap);
    AndroidBitmap_unlockPixels(env, maskBitmapAlpha);

    // 将 Mat 对象保存到指定路径
    const char *nativeString = env->GetStringUTFChars(filepath, nullptr);
    std::string cppString(nativeString);
    env->ReleaseStringUTFChars(filepath, nativeString);

    return cv::imwrite(cppString, result);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_hitpaw_imagehandler_ImageNativeUtils_redrawPostProcessing(JNIEnv *env, jobject thiz,
                                                                   jstring origin_image,
                                                                   jstring upload_image,
                                                                   jstring result_image,
                                                                   jstring save_path,
                                                                   jint pixel_count) {

    const char *originImage = env->GetStringUTFChars(origin_image, nullptr);
    const char *uploadImage = env->GetStringUTFChars(upload_image, nullptr);
    const char *resultImage = env->GetStringUTFChars(result_image, nullptr);
    const char *savePath = env->GetStringUTFChars(save_path, nullptr);

    cv::Mat originMat = cv::imread(originImage, cv::IMREAD_COLOR);
    cv::Mat uploadMat = cv::imread(uploadImage, cv::IMREAD_UNCHANGED);
    cv::Mat resultMat = cv::imread(resultImage, cv::IMREAD_COLOR);

    if (uploadMat.channels() == 4) {
        LOGI("uploadMat.channels() == 4");
        std::vector<cv::Mat> channels;
        cv::split(uploadMat, channels);
        cv::Mat alphaChannel = channels[3];
        uploadMat = alphaChannel;
    }
    cv::bitwise_not(uploadMat, uploadMat);

    cv::Mat kernel = cv::getStructuringElement(cv::MORPH_ELLIPSE, cv::Size(2 * 2 * pixel_count + 1,
                                                                           2 * 2 * pixel_count +
                                                                           1));
    cv::dilate(uploadMat, uploadMat, kernel);
    cv::GaussianBlur(uploadMat, uploadMat,
                     cv::Size(2 * 2 * pixel_count + 1, 2 * 2 * pixel_count + 1), 2 * pixel_count);

    LOGI("image uploadMat w %d h %d", uploadMat.size().width, uploadMat.size().height);
    LOGI("image resultMat w %d h %d", uploadMat.size().width, uploadMat.size().height);
    LOGI("resize to w %d h %d", originMat.size().width, originMat.size().height);
    cv::resize(uploadMat, uploadMat, originMat.size());
    cv::resize(resultMat, resultMat, originMat.size());

//    这个是给底图预加一个，
//    cv::Mat greenOverlay = cv::Mat::zeros(originMat.size(), originMat.type());
//    greenOverlay.setTo(cv::Scalar(0, 255, 0)); // 绿色
//    cv::addWeighted(originMat, 1.0, greenOverlay, 0.1, 0, originMat); // 10% 透明度

    // 使用 uploadMat 作为遮罩，将 resultMat 的内容替换到 originMat 中
    resultMat.copyTo(originMat, uploadMat);

    cv::imwrite(savePath, originMat);

    env->ReleaseStringUTFChars(origin_image, originImage);
    env->ReleaseStringUTFChars(upload_image, uploadImage);
    env->ReleaseStringUTFChars(result_image, resultImage);
    env->ReleaseStringUTFChars(save_path, savePath);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_hitpaw_imagehandler_ImageNativeUtils_horizontalPuzzle(JNIEnv *env, jobject thiz,
                                                               jobject img1, jobject img2,
                                                               jstring path) {
    cv::Mat img1Mat = bitmapToMat(env, img1);
    cv::Mat img2Mat = bitmapToMat(env, img2);

    // 将 ARGB8888 转换为 BGR
    cv::cvtColor(img1Mat, img1Mat, cv::COLOR_RGBA2BGR);
    cv::cvtColor(img2Mat, img2Mat, cv::COLOR_RGBA2BGR);

    cv::Mat mergedImage;
    cv::hconcat(img1Mat, img2Mat, mergedImage);

//    int featherWidth = img1Mat.cols / 8;  // 羽化宽度，可以根据需要调整
//
//    // 创建最终的合并图像
//    cv::Mat mergedImage(img1Mat.rows, img1Mat.cols + img2Mat.cols - featherWidth, img1Mat.type());
//    img1Mat.copyTo(mergedImage(cv::Rect(0, 0, img1Mat.cols, img1Mat.rows)));
//    img2Mat(cv::Rect(featherWidth, 0, img2Mat.cols - featherWidth, img2Mat.rows))
//            .copyTo(mergedImage(cv::Rect(img1Mat.cols, 0, img2Mat.cols - featherWidth, img2Mat.rows)));
//
//    // 使用 addWeighted 实现羽化过渡
//    for (int i = 0; i < featherWidth; ++i) {
//        double alpha = static_cast<double>(i) / featherWidth;
//        cv::addWeighted(img1Mat.col(img1Mat.cols - featherWidth + i), 1.0 - alpha,
//                        img2Mat.col(i), alpha, 0.0,
//                        mergedImage.col(img1Mat.cols - featherWidth + i));
//    }

    const char *pathStr = env->GetStringUTFChars(path, nullptr);
    bool success = cv::imwrite(pathStr, mergedImage);
    env->ReleaseStringUTFChars(path, pathStr);

    return success ? JNI_TRUE : JNI_FALSE;
}
extern "C"
JNIEXPORT void JNICALL
Java_com_hitpaw_imagehandler_ImageNativeUtils_getMaskByDepth(JNIEnv *env, jobject thiz,
                                                             jstring imagePath, jstring savePath,
                                                             jobject skin_list) {
    const char *nativeImagePath = env->GetStringUTFChars(imagePath, JNI_FALSE);
    std::string image_path(nativeImagePath);
    env->ReleaseStringUTFChars(imagePath, nativeImagePath);

    const char *nativeSavePath = env->GetStringUTFChars(savePath, JNI_FALSE);
    std::string save_path(nativeSavePath);
    env->ReleaseStringUTFChars(savePath, nativeSavePath);

    jclass skinSegBeanClass = env->FindClass("com/hitpaw/imagehandler/bean/SkinSegBean");
    jmethodID constructor = env->GetMethodID(skinSegBeanClass, "<init>", "(Ljava/lang/String;I)V");

    jclass listClass = env->GetObjectClass(skin_list);
    jmethodID addMethod = env->GetMethodID(listClass, "add", "(Ljava/lang/Object;)Z");

    cv::Mat image = cv::imread(image_path, cv::IMREAD_GRAYSCALE);
    if (image.empty()) {
        return;
    }
    std::set<int> unique_values;
    for (int row = 0; row < image.rows; ++row) {
        for (int col = 0; col < image.cols; ++col) {
            unique_values.insert(image.at<uchar>(row, col));
        }
    }
    for (int value: unique_values) {
        if (value == 0) continue;
        cv::Mat mask;
        cv::inRange(image, value, value, mask);

        std::string mask_filename = save_path + "/mask_" + std::to_string(value) + ".png";
        cv::imwrite(mask_filename, mask);

        jstring maskPathString = env->NewStringUTF(mask_filename.c_str());
        jobject skinSegBean = env->NewObject(skinSegBeanClass, constructor, maskPathString, value);

        env->CallBooleanMethod(skin_list, addMethod, skinSegBean);

        env->DeleteLocalRef(skinSegBean);
        env->DeleteLocalRef(maskPathString);
    }
}




extern "C"

JNIEXPORT jobject JNICALL
Java_com_hitpaw_imagehandler_ImageNativeUtils_nativeGaussianBlur(JNIEnv *env, jobject thiz, jstring imagePath,   jint kernelSize) {
    // 1. 读取图片
    const char *path = env->GetStringUTFChars(imagePath, nullptr);
    cv::Mat src = cv::imread(path);
    env->ReleaseStringUTFChars(imagePath, path);
    if (src.empty()) {
        return nullptr;
    }

    // 2. 高斯模糊处理
    cv::Mat dst;
    int ksize = (kernelSize % 2 == 0) ? kernelSize + 1 : kernelSize;
    GaussianBlur(src, dst, cv::Size(ksize, ksize), 0);

    // 3. 转换为RGBA格式
    cv::Mat rgba;
    cvtColor(dst, rgba, cv::COLOR_BGR2RGBA);

    // 4. 创建Bitmap
    jclass bitmapClass = env->FindClass("android/graphics/Bitmap");
    jmethodID createBitmapMethodID = env->GetStaticMethodID(bitmapClass, "createBitmap","(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
    jclass bitmapConfigClass = env->FindClass("android/graphics/Bitmap$Config");
    jfieldID argb8888FieldID = env->GetStaticFieldID(bitmapConfigClass, "ARGB_8888","Landroid/graphics/Bitmap$Config;");
    jobject argb8888Obj = env->GetStaticObjectField(bitmapConfigClass, argb8888FieldID);
    jobject bitmap = env->CallStaticObjectMethod(bitmapClass, createBitmapMethodID,rgba.cols, rgba.rows, argb8888Obj);

    // 5. 复制数据到Bitmap
    AndroidBitmapInfo info;
    void *pixels;
    AndroidBitmap_getInfo(env, bitmap, &info);
    AndroidBitmap_lockPixels(env, bitmap, &pixels);
    memcpy(pixels, rgba.data, rgba.cols * rgba.rows * 4);
    AndroidBitmap_unlockPixels(env, bitmap);

    return bitmap;
}


