package com.ethan.company.gson

import androidx.annotation.Keep

import com.google.gson.annotations.SerializedName


@Keep
data class PageDate(@SerializedName("msg") val msg: String, @SerializedName("page") val page: Page) {
    @Keep
    data class Page(@SerializedName("page_index") val pageIndex: Int)
}