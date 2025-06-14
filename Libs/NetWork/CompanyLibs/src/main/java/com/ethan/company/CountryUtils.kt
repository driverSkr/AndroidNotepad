package com.ethan.company

import android.content.Context
import com.ethan.company.chain.AbstractProcessor
import com.ethan.company.chain.CacheProcessor
import com.ethan.company.chain.IpJsonProcessor
import com.ethan.company.chain.IpmeProcessor
import com.ethan.company.chain.LocaleProcessor
import com.ethan.company.chain.SimProcessor
import kotlinx.coroutines.withTimeoutOrNull
import java.util.*

object CountryUtils {

    private val processor: AbstractProcessor

    init {
        val cacheProcessor = CacheProcessor()
        val simProcessor = SimProcessor()
        val ipmeProcessor = IpmeProcessor()
        val ipJsonProcessor = IpJsonProcessor()
        val localeProcessor = LocaleProcessor()
        cacheProcessor.setNextProcessor(simProcessor)
        simProcessor.setNextProcessor(ipmeProcessor)
        ipmeProcessor.setNextProcessor(ipJsonProcessor)
        ipJsonProcessor.setNextProcessor(localeProcessor)
        processor = cacheProcessor
    }

    suspend fun getCountryCode(context: Context): String {
        var code: String? = null
        withTimeoutOrNull(4000) {
            code = processor.getCountryCode(context)
        }
        if (code == null) {
            code = Locale.getDefault().country.uppercase()
        } else {
            saveCache(context, code!!)
        }
        return code!!
    }

    private fun saveCache(context: Context, code: String) {
        // setCountryArea(context,AdsController.getCountryArea(context,code))
        val sharedPreferences = context.getSharedPreferences("sp_country_code", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("country_code", code)
        editor.apply()
    }

    //每次启动判断缓存是否是通过地区语言设置，如果是则清除重新获取一次
    suspend fun getCountryCodeTimeOut(context: Context): String {
        var code: String? = null
        if (!getCurrentCountryCode(context)) {
            val sharedPreferences = context.getSharedPreferences("sp_country_code", Context.MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()
        }
        withTimeoutOrNull(4000) {
            code = processor.getCountryCode(context)
        }
        if (code == null) {
            code = Locale.getDefault().country.uppercase()
            saveCache(context, code!!)
        } else {
            saveCache(context, code!!)
        }
        return code!!
    }


    fun setCurrentCountryCode(context: Context) {
        val sharedPreferences = context.getSharedPreferences("sp_ip_country_code", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putBoolean("ip_country_code", true)
        editor?.apply()
    }

    private fun getCurrentCountryCode(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("sp_ip_country_code", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("ip_country_code", false)
    }


    private fun setCountryArea(context: Context, area: String) {
        val sharedPreferences = context.getSharedPreferences("sp_country_area", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("country_area", area)
        editor.apply()
    }

    fun getCountryArea(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("sp_country_area", Context.MODE_PRIVATE)
        return sharedPreferences.getString("country_area", "T2") ?: "T2"
    }
}