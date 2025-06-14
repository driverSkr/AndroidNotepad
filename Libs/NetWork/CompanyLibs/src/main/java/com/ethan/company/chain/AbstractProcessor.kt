package com.ethan.company.chain

import android.content.Context


abstract class AbstractProcessor {
    private var nextProcessor: AbstractProcessor? = null

    fun setNextProcessor(nextProcessor: AbstractProcessor) {
        this.nextProcessor = nextProcessor
    }

    abstract suspend fun getCountryCode(context: Context): String

    suspend fun next(context: Context): String {
        return nextProcessor?.getCountryCode(context) ?: ""
    }
}