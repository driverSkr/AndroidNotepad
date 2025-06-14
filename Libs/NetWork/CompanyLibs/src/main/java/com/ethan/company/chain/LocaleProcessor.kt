package com.ethan.company.chain

import android.content.Context
import java.util.*


class LocaleProcessor : AbstractProcessor() {
    override suspend fun getCountryCode(context: Context): String {
        return Locale.getDefault().country.uppercase()
    }
}