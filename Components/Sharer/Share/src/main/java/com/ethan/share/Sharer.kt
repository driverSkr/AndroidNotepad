package com.ethan.share

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import android.widget.Toast

object Sharer {

    /**
     * 系统分享
     */
    @SuppressLint("ObsoleteSdkInt")
    fun share(uri: Uri, mimeType: String, paddingIntent: PendingIntent? = null, text: String? = null): Intent? {
        checkExpose()
        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        text?.let {
            intent.putExtra(Intent.EXTRA_TEXT, it)
        }
        intent.type = mimeType
        return if (paddingIntent?.intentSender == null) {
            Intent.createChooser(intent, "share")
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                Intent.createChooser(intent, "share", paddingIntent.intentSender)
            } else {
                Intent.createChooser(intent, "share")
            }
        }
    }

    /**
     * 系统分享（指定包名）
     */
    @SuppressLint("ObsoleteSdkInt")
    fun share(context: Context, uri: Uri, mimeType: String, pkg: String, notice: String, paddingIntent: PendingIntent? = null, text: String? = null): Intent? {
        if (!isInstallApp(context, pkg, notice)) return null
        checkExpose()
        val intent = Intent(Intent.ACTION_SEND)
        intent.setPackage(pkg)
        // intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        text?.let {
            intent.putExtra(Intent.EXTRA_TEXT, it)
        }
        intent.type = mimeType
        return if (paddingIntent?.intentSender == null) {
            Intent.createChooser(intent, "share")
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                Intent.createChooser(intent, "share", paddingIntent.intentSender)
            } else {
                Intent.createChooser(intent, "share")
            }
        }
    }

    /**
     * 分享文本
     */
    fun shareText(text: String): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, text)
        return Intent.createChooser(intent, "share")
    }

    /**
     * 分享文本（指定包名）
     */
    fun shareText(context: Context, text: String, pkg: String, notice: String): Intent? {
        if (!isInstallApp(context, pkg, notice)) return null
        val intent = Intent(Intent.ACTION_SEND)
        intent.setPackage(pkg)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, text)
        return Intent.createChooser(intent, "share")
    }

    @SuppressLint("ObsoleteSdkInt")
    fun shareEmail(uri: Uri, content: String, paddingIntent: PendingIntent? = null): Intent? {
        checkExpose()
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, "")
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.putExtra(Intent.EXTRA_SUBJECT, "")
        intent.putExtra(Intent.EXTRA_TEXT, content)
        return if (paddingIntent?.intentSender == null) {
            Intent.createChooser(intent, "share")
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                Intent.createChooser(intent, "share", paddingIntent.intentSender)
            } else {
                Intent.createChooser(intent, "share")
            }
        }
    }

    fun shareEmail(context: Context, content: String): Boolean {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, "")
        intent.putExtra(Intent.EXTRA_SUBJECT, "")
        intent.putExtra(Intent.EXTRA_TEXT, content)
        context.startActivity(Intent.createChooser(intent, "share"))
        return true
    }

    private fun checkExpose() {
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        builder.detectFileUriExposure()
    }

    private fun isInstallApp(context: Context?, packageName: String, notice: String): Boolean {
        val infos = context?.packageManager?.getInstalledPackages(0)
        infos?.forEach {
            if (it.packageName.equals(packageName)) {
                return true
            }
        }
        Toast.makeText(context, notice, Toast.LENGTH_SHORT).show()
        return false
    }
}