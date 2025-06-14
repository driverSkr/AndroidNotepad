package com.ethan.videoediting

import android.util.Log


class TablePrinter(private val columns: List<String>) {
    private val rows = mutableListOf<List<String>>()

    fun addRow(row: List<String>) {
        rows.add(row)
    }

    fun printTable() {
        val columnWidths = IntArray(columns.size)
        for (i in columns.indices) {
            columnWidths[i] = maxOf(columns[i].length, rows.maxOfOrNull { it[i].length } ?: 0)
        }

        val separator = buildSeparator(columnWidths)

        Log.i("MediaInfo", separator)
        Log.i("MediaInfo", buildRow(columns, columnWidths))
        Log.i("MediaInfo", separator)

        rows.forEach { row ->
            Log.i("MediaInfo", buildRow(row, columnWidths))
        }

        Log.i("MediaInfo", separator)
    }

    private fun buildRow(row: List<String>, columnWidths: IntArray): String {
        val builder = StringBuilder()
        builder.append("|")
        for (i in row.indices) {
            val cell = row[i].padEnd(columnWidths[i])
            builder.append(" $cell |")
        }
        return builder.toString()
    }

    private fun buildSeparator(columnWidths: IntArray): String {
        val builder = StringBuilder()
        builder.append("+")
        for (width in columnWidths) {
            builder.append("-".repeat(width + 2))
            builder.append("+")
        }
        return builder.toString()
    }
}
