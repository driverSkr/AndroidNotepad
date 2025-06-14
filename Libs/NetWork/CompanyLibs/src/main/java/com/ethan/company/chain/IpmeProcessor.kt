package com.ethan.company.chain

import android.content.Context
import com.ethan.company.CountryUtils
import org.jsoup.Jsoup


class IpmeProcessor : AbstractProcessor() {
    override suspend fun getCountryCode(context: Context): String {
        try { // 创建Jsoup连接
            val currentTimeMillis = System.currentTimeMillis()
            val doc = Jsoup.connect("https://ip.me/").timeout(4000).get() // 选择包含<th>County Code:</th>的<tr>元素
            val time = System.currentTimeMillis() - currentTimeMillis
            val rows = doc.select("tr:has(th:contains(County Code:))") // 遍历每个<tr>元素
            for (row in rows) { // 获取<tr>元素里的<code>元素
                val codeElement = row.selectFirst("code") // 如果<code>元素不为null，则获取它的文本
                if (codeElement != null) {
                    val countryCode = codeElement.text().uppercase()
                    CountryUtils.setCurrentCountryCode(context)
                    return countryCode
                }
            }
            return next(context)
        } catch (e: Exception) {
            e.printStackTrace()
            return next(context)
        }
    }
}