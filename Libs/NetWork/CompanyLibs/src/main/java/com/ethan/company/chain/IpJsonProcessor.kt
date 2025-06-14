package com.ethan.company.chain

import android.content.Context
import com.ethan.company.BuildConfig
import com.ethan.company.CountryUtils
import com.ethan.company.core.HttpExecutor
import org.jsoup.Jsoup


class IpJsonProcessor : AbstractProcessor() {
    override suspend fun getCountryCode(context: Context): String {
        val ipAddress = try {
            val currentTimeMillis = System.currentTimeMillis()
            val doc = Jsoup.connect("https://ifconfig.me/ip").timeout(4000).get()
            val body = doc.body().text()
            CountryUtils.setCurrentCountryCode(context)
            body
        } catch (e: Exception) {
            null
        }
        return try {
            val server = HttpExecutor.getInstance()
                .init("https://ipwhois.app/json/", BuildConfig.DEBUG)
            val info = HttpExecutor.getInstance().asyncGetStringIp(server,ipAddress ?: "")
            if (info?.country_code?.isNotEmpty() == true) {
                info.country_code.uppercase() ?: ""
            } else {
                next(context)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            next(context)
        }
    }
}