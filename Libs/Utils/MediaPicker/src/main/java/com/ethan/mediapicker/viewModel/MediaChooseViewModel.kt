package com.ethan.mediapicker.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @author  PengHaiChen
 * @date    2024/1/26 9:42
 * @email   penghaichen@tenorshare.cn
 * 选择图片的回调vm
 */
class MediaChooseViewModel : ViewModel() {


    enum class State {
        SELECT, SUCCESS, ERROR,VIDEO_DURATION_ERROR
    }

    enum class From {
        CAMERA, SELECT
    }

    data class MediaChooseState(val selectState: State, val mediaBean: MediaBean? = null, val from: From = From.SELECT)

    var chooseMedia = MutableLiveData<MediaChooseState>()

    var paddingBottom = MutableLiveData<Int>()
}