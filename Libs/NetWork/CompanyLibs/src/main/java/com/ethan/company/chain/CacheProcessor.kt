package com.ethan.company.chain

import android.content.Context


class CacheProcessor : AbstractProcessor() {
    override suspend fun getCountryCode(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("sp_country_code", Context.MODE_PRIVATE)
        val code = sharedPreferences.getString("country_code", "")
        return if (code?.isNotEmpty() == true) {
            code
        } else {
            next(context)
        }
    }
}