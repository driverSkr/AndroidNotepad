package com.ethan.android.notepad.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

object LaunchUtils {
    fun launchWeb(context: Context?, url: String, title: String) {
        try {
            context?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: Exception) {
            // val webIntent = Intent(context, WebActivity::class.java)
            // webIntent.putExtra("title", title)
            // webIntent.putExtra("url", url)
            // context?.startActivity(webIntent)
            Log.d("ethan", "e: ${e.printStackTrace()}")
        }
    }

    fun launchPlayStore(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            Log.d("ethan", "包名：${context.packageName}")
            intent.data = Uri.parse("market://details?id=" + context.packageName)
            intent.setPackage("com.android.vending")
            context.startActivity(intent)
        } catch (e: Exception) {
            launchWeb(context, "https://play.google.com/store/apps/details?id=" + context.packageName, "app name")
        }
    }
}