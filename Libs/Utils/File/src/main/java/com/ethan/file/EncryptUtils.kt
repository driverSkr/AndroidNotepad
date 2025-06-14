package com.ethan.file

import android.content.Context


object EncryptUtils {
    private const val customTable = "0123456789!@#$%^&*()-_=+[]{}|;:'\",.<>/?`~ " //自定义映射表
    private const val replacementTable = "ZqZwZeZrZtZyZuZiZoZpZaZsZdZfZgZhZjZkZlZzZxZcZvZbZnZmZQZWZEZRZTZYZUZIZOZPZAZSZDZFZGZHZM" //需要
    private const val customCharset = "ABCDEFGHIJKLMNOPQRSTUVWXYabcdefghijklmnopqrstuvwxyz"
    private const val replacementCharset = "YXWVUTSRQPONMLKJIHGFEDCBAzyxwvutsrqponmlkjihgfedcba"

    private fun splitStringWithEqualSign(): List<String> {
        val result = mutableListOf<String>()
        var current = StringBuilder()

        for (char in replacementTable) {
            if (char == 'Z') {
                if (current.isNotEmpty()) {
                    result.add(current.toString())
                    current = StringBuilder()
                }
                current.append(char)
            } else {
                current.append(char)
            }
        }

        if (current.isNotEmpty()) {
            result.add(current.toString())
        }
        return result
    }


    //deepLink解密
    fun decryptCustom(encryptedData: String): String? {
        val decryptedData = StringBuilder()
        val result = encryptedData.replace(Regex("[0-9]"), "")
        try {
            var safe = false
            result.forEachIndexed { index, char ->
                if (safe) {
                    safe = false
                    return@forEachIndexed
                }
                if (char != 'Z') {
                    val charIndex = replacementCharset.indexOf(char)
                    decryptedData.append(customCharset[charIndex])
                } else {
                    safe = true
                    val nextChar = result[index + 1]
                    val replacementIndex = replacementTable.indexOf(nextChar)
                    val qwf = (replacementIndex + 1) / 2
                    val varChar = customTable[qwf - 1]
                    decryptedData.append(varChar)
                }
            }
            return decryptedData.toString()
        } catch (e: Exception) {
            return null
        }

    }

    //加密
    fun encryptCustom(data: String): String {
        val result = splitStringWithEqualSign()
        val encryptedData = StringBuilder()
        for (char in data) {
            when {
                customCharset.contains(char) -> {
                    val index = customCharset.indexOf(char)
                    encryptedData.append(replacementCharset[index])
                }

                customTable.contains(char) -> {
                    val index = customTable.indexOf(char)
                    encryptedData.append(result[index])
                }

                else -> {
                    encryptedData.append(char)
                }
            }
        }
        return "1657$encryptedData"
    }

    fun saveAppLink(context: Context, value: String?) {
        val decodedString = if (value == null) {
            null
        } else {
            decryptCustom(value)
        }
        LogWriter.append("deeplink:$decodedString")
        val sharedPreferences = context.getSharedPreferences("sp_unified_link_value_0913", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("unified_link_value", decodedString)
        editor.apply()
    }


}