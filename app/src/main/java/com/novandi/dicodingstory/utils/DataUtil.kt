package com.novandi.dicodingstory.utils

import android.content.Context
import android.util.Patterns
import com.novandi.dicodingstory.R

fun CharSequence?.validateName(context: Context): String {
    val errorMessage = if (this.isNullOrEmpty()) {
        context.resources.getString(R.string.error_name_empty)
    } else {
        ""
    }

    return errorMessage
}

fun CharSequence?.validateEmail(context: Context): String {
    val errorMessage = if (this.isNullOrEmpty()) {
        context.resources.getString(R.string.error_email_empty)
    } else if (!Patterns.EMAIL_ADDRESS.matcher(this).matches()) {
        context.resources.getString(R.string.error_email_invalid)
    } else {
        ""
    }

    return errorMessage
}

fun CharSequence?.validatePassword(context: Context): String {
    val errorMessage = if (this.isNullOrEmpty()) {
        context.resources.getString(R.string.error_password_empty)
    } else if (this.toString().length < 8) {
        context.resources.getString(R.string.error_password_length)
    } else {
        ""
    }

    return errorMessage
}