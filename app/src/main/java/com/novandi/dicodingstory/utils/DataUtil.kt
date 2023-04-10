package com.novandi.dicodingstory.utils

import android.content.res.Resources
import android.util.Patterns
import com.novandi.dicodingstory.R

fun CharSequence?.validateEmail(): String {
    val errorMessage = if (this.isNullOrEmpty()) {
        Resources.getSystem().getString(R.string.error_email_empty)
    } else if (!Patterns.EMAIL_ADDRESS.matcher(this).matches()) {
        Resources.getSystem().getString(R.string.error_email_invalid)
    } else {
        ""
    }

    return errorMessage
}

fun CharSequence?.validatePassword(): String {
    val errorMessage = if (this.isNullOrEmpty()) {
        Resources.getSystem().getString(R.string.error_password_empty)
    } else if (this.length < 8) {
        Resources.getSystem().getString(R.string.error_password_length)
    } else {
        ""
    }

    return errorMessage
}