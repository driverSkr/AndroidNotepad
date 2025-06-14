package com.ethan.android.notepad.utils

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView

object TextSpanUtils {

    fun setSpanText(context: Context, content: String, spans: Array<Span>, textView: TextView) {
        if (spans.isEmpty()) {
            return
        }
        try {
            val spannableStringBuilder = SpannableStringBuilder()
            spannableStringBuilder.append(content)
            for (i in spans.indices) {
                val span = spans[i]
                val foregroundColorSpan = ForegroundColorSpan(context.resources.getColor(span.color))
                val clickableSpan = span.click
                val spanText = span.text
                val styleSpan = StyleSpan(Typeface.BOLD)
                val index = content.lastIndexOf(spanText)
                spannableStringBuilder.setSpan(clickableSpan, index, index + spanText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableStringBuilder.setSpan(foregroundColorSpan, index, index + spanText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableStringBuilder.setSpan(styleSpan, index, index + spanText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            textView.movementMethod = LinkMovementMethod.getInstance()
            textView.text = spannableStringBuilder
        } catch (e: Exception) {
            println(e.message)
        }
    }

    fun setAllSpanText(context: Context, content: String, spans: Array<Span>, textView: TextView) {
        if (spans.isEmpty()) {
            return
        }
        val spannableStringBuilder = SpannableStringBuilder()
        spannableStringBuilder.append(content)
        for (i in spans.indices) {
            val span = spans[i]
            val spanText = span.text
            val indexList = findAllMatches(content, spanText)
            indexList.forEach { index ->
                val foregroundColorSpan = ForegroundColorSpan(context.resources.getColor(span.color))
                val styleSpan = StyleSpan(Typeface.BOLD)
                val clickableSpan = span.click
                spannableStringBuilder.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        clickableSpan.onClick(widget)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = false // 取消下划线
                    }
                }, index, index + spanText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                //spannableStringBuilder.setSpan(clickableSpan, index, index + spanText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableStringBuilder.setSpan(foregroundColorSpan, index, index + spanText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableStringBuilder.setSpan(styleSpan, index, index + spanText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.text = spannableStringBuilder
    }

    data class Span(val text: String, val color: Int, val click: ClickableSpan)

    private fun findAllMatches(text: String, pattern: String): List<Int> {
        val regex = Regex(pattern)
        val matches = regex.findAll(text)
        return matches.map { it.range.first }.toList()
    }
}