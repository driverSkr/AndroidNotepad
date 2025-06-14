package com.ethan.company.logging

import okhttp3.Request
import java.io.IOException


interface BufferListener {
    @Throws(IOException::class)
    fun getJsonResponse(request: Request?): String?
}