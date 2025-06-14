package com.ethan.googlelogin

import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class GoogleAccount {

    var id: String? = null
    var name: String? = null
    var email: String? = null
    var token: String? = null
    var headUrl: String? = null
    var isExpired: Boolean? = null
    var type: String? = null

    fun createAccount(account: GoogleSignInAccount?): GoogleAccount {
        this.id = account?.id
        this.name = account?.displayName
        this.email = account?.email
        this.token = account?.idToken
        this.headUrl = account?.photoUrl.toString()
        this.isExpired = account?.isExpired
        this.type = "google"
        return this
    }

    fun isEmpty(): Boolean {
        return id == null && email == null && token == null
    }
}