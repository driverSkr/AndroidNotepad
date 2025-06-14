package com.ethan.company.core


class CommonParams {

    companion object {
        private var commonParams: CommonParams? = null
            get() {
                if (field == null) {
                    field = CommonParams()
                }
                return field
            }

        @Synchronized
        fun getInstance(): CommonParams {
            return commonParams!!
        }
    }

    private val params = mutableMapOf<String, Any>()


    fun putParam(key: String, value: Any) {
        params.put(key, value)
    }


    fun putAllParams(addParams: Map<String, Any>) {
        params.putAll(addParams)
    }

    fun remove(key: String?) {
        params.remove(key)
    }

    fun clearParams() {
        params.clear()
    }

    fun getCommonParams(): MutableMap<String, Any> {
        return this.params
    }
}