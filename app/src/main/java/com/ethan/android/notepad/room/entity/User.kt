package com.ethan.android.notepad.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String,
    var phone: Long,
    var password: String,
    var email: String,
    var address: String,
    var gender: String,
    var age: Int
)
