package com.ethan.file

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * 对Asset下文件的操作
 */
object AssetUtils {
    /**
     * 读取Asset文件转为String
     *
     * @param context
     * @param fileName
     * @return
     */
    fun getTextFromAsset(context: Context, fileName: String?): String {
        try {
            val sb = StringBuilder()
            val reader = InputStreamReader(
                context.resources.assets.open(
                    fileName!!
                )
            )
            val buf = CharArray(1024 * 8)
            var len: Int
            while (reader.read(buf).also { len = it } > 0) {
                sb.append(buf, 0, len)
            }
            return sb.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 读取asset目录下的json文件
     *
     * @param fileName
     * @param context
     * @return
     */
    fun getJsonFromAssets(fileName: String?, context: Context): String {
        //将json数据变成字符串
        val stringBuilder = StringBuilder()
        var bf: BufferedReader? = null
        try {
            //获取assets资源管理器
            val assetManager = context.assets
            //通过管理器打开文件并读取
            bf = BufferedReader(
                InputStreamReader(
                    assetManager.open(fileName!!)
                )
            )
            var line: String?
            while (bf.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (bf != null) {
                try {
                    bf.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return stringBuilder.toString()
    }
}