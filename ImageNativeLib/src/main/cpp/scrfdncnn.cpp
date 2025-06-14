// Tencent is pleased to support the open source community by making ncnn available.
//
// Copyright (C) 2021 THL A29 Limited, a Tencent company. All rights reserved.
//
// Licensed under the BSD 3-Clause License (the "License"); you may not use this file except
// in compliance with the License. You may obtain a copy of the License at
//
// https://opensource.org/licenses/BSD-3-Clause
//
// Unless required by applicable law or agreed to in writing, software distributed
// under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
// CONDITIONS OF ANY KIND, either express or implied. See the License for the
// specific language governing permissions and limitations under the License.

#include <android/asset_manager_jni.h>
#include <android/native_window_jni.h>
#include <android/native_window.h>

#include <android/log.h>

#include <jni.h>

#include <string>
#include <vector>

#include <platform.h>
#include <benchmark.h>

#include "scrfd.h"


#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <jni.h>

#if __ARM_NEON
#include <arm_neon.h>
#endif // __ARM_NEON

static SCRFD *g_scrfd = 0;
static ncnn::Mutex lock;


extern "C" {

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "JNI_OnLoad");


    return JNI_VERSION_1_4;
}

JNIEXPORT void JNI_OnUnload(JavaVM *vm, void *reserved) {
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "JNI_OnUnload");

    {
        ncnn::MutexLockGuard g(lock);

        delete g_scrfd;
        g_scrfd = 0;
    }
}

// public native boolean loadModel(AssetManager mgr, int modelid, int cpugpu);
JNIEXPORT jboolean JNICALL
Java_com_hitpaw_imagehandler_NCNNFaceDetectUtils_loadModel(JNIEnv *env, jobject thiz,
                                                           jobject assetManager, jint modelid,
                                                           jint cpugpu) {
    if (modelid < 0 || modelid > 7 || cpugpu < 0 || cpugpu > 1) {
        return JNI_FALSE;
    }

    AAssetManager *mgr = AAssetManager_fromJava(env, assetManager);

    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "loadModel %p", mgr);

    const char *modeltypes[] =
            {
                    "500m",
                    "500m_kps",
                    "1g",
                    "2.5g",
                    "2.5g_kps",
                    "10g",
                    "10g_kps",
                    "34g"
            };

    const char *modeltype = modeltypes[(int) modelid];
    bool use_gpu = (int) cpugpu == 1;

    // reload
    {
        ncnn::MutexLockGuard g(lock);

        if (use_gpu && ncnn::get_gpu_count() == 0) {
            // no gpu
            delete g_scrfd;
            g_scrfd = 0;
        } else {
            if (!g_scrfd)
                g_scrfd = new SCRFD;
            g_scrfd->load(mgr, modeltype, use_gpu);
        }
    }

    return JNI_TRUE;
}

bool checkJNIException(JNIEnv *env) {
    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
        return true;
    }
    return false;
}

JNIEXPORT jboolean JNICALL
Java_com_hitpaw_imagehandler_NCNNFaceDetectUtils_detailFaceByBitmapNDK(JNIEnv *env, jobject thiz,
                                                                       jobject bitmap,
                                                                       jobject face_list,
                                                                       jboolean need_thumb) {
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "detailFaceByBitmap");

    // Convert Java Bitmap to OpenCV Mat
    AndroidBitmapInfo bitmapInfo;
    void *bitmapPixels;
    if (AndroidBitmap_getInfo(env, bitmap, &bitmapInfo) < 0) {
        return JNI_FALSE;
    }
    if (AndroidBitmap_lockPixels(env, bitmap, &bitmapPixels) < 0) {
        return JNI_FALSE;
    }
    cv::Mat rgba((int) bitmapInfo.height, (int) bitmapInfo.width, CV_8UC4, bitmapPixels);
    cv::Mat rgb;
    cv::cvtColor(rgba, rgb, cv::COLOR_RGBA2RGB);
    AndroidBitmap_unlockPixels(env, bitmap);

    std::vector<FaceObject> faceobjects;

    // Get the class
    jclass faceBeanClass = env->FindClass("com/hitpaw/imagehandler/bean/FaceBean");
    if (faceBeanClass == nullptr) {
        return JNI_FALSE;
    }
    // Get the constructor
    jmethodID faceBeanConstructor = env->GetMethodID(faceBeanClass, "<init>",
                                                     "(FFFFFLandroid/graphics/Bitmap;)V");
    if (faceBeanConstructor == nullptr) {
        return JNI_FALSE;
    }
    // Get the ArrayList class
    jclass arrayListClass = env->GetObjectClass(face_list);
    if (arrayListClass == nullptr) {
        return JNI_FALSE;
    }
    // Get the ArrayList's add method
    jmethodID arrayListAdd = env->GetMethodID(arrayListClass, "add", "(Ljava/lang/Object;)Z");
    if (arrayListAdd == nullptr) {
        return JNI_FALSE;
    }

    g_scrfd->detect(rgb, faceobjects);
    for (const auto &item: faceobjects) {
        jobject thumbBitmap = nullptr;
        if (need_thumb) {
            // Crop the face from the image
            cv::Rect faceRect(item.rect.x, item.rect.y, item.rect.width, item.rect.height);
            cv::Mat face = rgb(faceRect);

            // Resize the face to a maximum width or height of 100 pixels
            int maxDim = std::max(face.cols, face.rows);
            float scale = 100.0 / maxDim;
            cv::Mat resizedFace;
            cv::resize(face, resizedFace, cv::Size(), scale, scale);

            // Convert the resized face to a Bitmap
            jobject config = env->CallStaticObjectMethod(
                    env->FindClass("android/graphics/Bitmap$Config"),
                    env->GetStaticMethodID(env->FindClass("android/graphics/Bitmap$Config"),
                                           "valueOf",
                                           "(Ljava/lang/String;)Landroid/graphics/Bitmap$Config;"),
                    env->NewStringUTF("ARGB_8888"));
            jobject newBitmap = env->CallStaticObjectMethod(
                    env->FindClass("android/graphics/Bitmap"),
                    env->GetStaticMethodID(env->FindClass("android/graphics/Bitmap"),
                                           "createBitmap",
                                           "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;"),
                    resizedFace.cols, resizedFace.rows, config);

            AndroidBitmap_lockPixels(env, newBitmap, &bitmapPixels);
            cv::Mat newBitmapMat(resizedFace.rows, resizedFace.cols, CV_8UC4, bitmapPixels);
            cv::cvtColor(resizedFace, newBitmapMat, cv::COLOR_RGB2RGBA);
            AndroidBitmap_unlockPixels(env, newBitmap);
            thumbBitmap = newBitmap;
        }

        jobject faceBeanObj = env->NewObject(faceBeanClass, faceBeanConstructor,
                                             item.rect.x, item.rect.y,
                                             item.rect.width, item.rect.height,
                                             item.prob, thumbBitmap);
        env->CallBooleanMethod(face_list, arrayListAdd, faceBeanObj);
        env->DeleteLocalRef(faceBeanObj);
        if (thumbBitmap != nullptr) {
            env->DeleteLocalRef(thumbBitmap);
        }
    }
    checkJNIException(env);
    return JNI_TRUE;
}
}
