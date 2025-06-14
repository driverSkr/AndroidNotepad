package com.ethan.company.chain

import android.content.Context
import android.telephony.TelephonyManager
import com.ethan.company.CountryUtils


class SimProcessor : AbstractProcessor() {
    override suspend fun getCountryCode(context: Context): String {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val simCountryIso = telephonyManager.simCountryIso
        return if (simCountryIso.isNullOrEmpty()) {
            next(context)
        } else {
            CountryUtils.setCurrentCountryCode(context)
            simCountryIso.uppercase()
        }
    }
}