package com.ethan.mediapicker.db

interface IFaceImageStorage {
    suspend fun getFaceData(path: String): Boolean?
    suspend fun insertFaceData(path: String, timestamp: Long)
} 