package com.ethan.android.notepad.utils

import android.Manifest
import android.os.Build
import androidx.compose.runtime.MutableState
import com.blankj.utilcode.util.PermissionUtils
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object MyPermissionUtils {

    suspend fun checkPhotoPermission(
        onlyCheck: Boolean = true,
        jump2Setting: Boolean = false,
    ) = suspendCoroutine { suspendCoroutine ->
        val photoPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }.toTypedArray()
        val isA14 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
        if (isA14) {
            if (PermissionUtils.isGranted(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)) {
                suspendCoroutine.resume(true)
                return@suspendCoroutine
            }
        }
        val granted = PermissionUtils.isGranted(*photoPermission)
        if (granted) {
            suspendCoroutine.resume(true)
        } else {
            if (onlyCheck) {
                suspendCoroutine.resume(false)
                return@suspendCoroutine
            }
            PermissionUtils.permission(*photoPermission)
                .callback { isAllGranted, _, deniedForever, _ ->
                    suspendCoroutine.resume(isAllGranted)
                    if (jump2Setting) {
                        if (deniedForever.isNotEmpty()) PermissionUtils.launchAppDetailsSettings()
                    }
                }.request()
        }
    }

    suspend fun checkRecordPermission(
        onlyCheck: Boolean = true,
        permissionDialog: MutableState<Boolean>? = null
    ) = suspendCoroutine { suspendCoroutine ->
        val photoPermission = listOf(Manifest.permission.RECORD_AUDIO).toTypedArray()
        val granted = PermissionUtils.isGranted(*photoPermission)
        if (granted) {
            suspendCoroutine.resume(true)
        } else {
            if (onlyCheck) {
                suspendCoroutine.resume(false)
                return@suspendCoroutine
            }

            PermissionUtils.permission(*photoPermission)
                .callback { isAllGranted, _, deniedForever, _ ->
                    suspendCoroutine.resume(isAllGranted)
                    if (deniedForever.isNotEmpty()) permissionDialog?.value = true
                }.request()
        }
    }

}