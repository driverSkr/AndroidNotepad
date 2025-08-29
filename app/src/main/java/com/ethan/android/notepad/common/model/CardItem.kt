package com.ethan.android.notepad.common.model

data class CardItem(
    val name: String,
    val isNeedEndIcon: Boolean,
    val isCompleted: Boolean = true,
    val onClick: () -> Unit = {}
)
