package com.ethan.company.core


class CommonHeaders {
    companion object {
        private var commonHeaders: CommonHeaders? = null
            get() {
                if (field == null) {
                    field = CommonHeaders()
                }
                return field
            }

        @Synchronized
        fun getInstance(): CommonHeaders {
            return commonHeaders!!
        }
    }

    private val headers = mutableMapOf<String, Any>()


    fun putHeader(key: String, value: Any) {
        headers.put(key, value)
    }


    fun putAllHeaders(addParams: Map<String, Any>) {
        headers.putAll(addParams)
    }

    fun remove(key: String?) {
        headers.remove(key)
    }

    fun clearHeaders() {
        headers.clear()
    }

    fun getCommonHeaders(): MutableMap<String, Any> {
        return this.headers
    }
}