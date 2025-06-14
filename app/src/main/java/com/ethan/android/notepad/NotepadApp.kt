package com.ethan.android.notepad

import com.ethan.base.component.BaseApp

class NotepadApp: BaseApp() {
    companion object {
        var INSTANCE: NotepadApp? = null
            private set
    }
    override fun initLibs() {
        INSTANCE = this
    }
}