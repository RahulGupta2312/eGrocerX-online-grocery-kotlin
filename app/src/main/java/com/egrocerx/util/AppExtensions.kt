package com.egrocerx.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*

fun View.showKeyboard(show: Boolean) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (show) {
        if (requestFocus()) imm.showSoftInput(this, 0)
    } else {
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}

fun View.showSnackbar(msg: String) {
    Snackbar.make(this, msg, Snackbar.LENGTH_SHORT).show()
}


@SuppressLint("ObsoleteSdkInt")
inline fun <reified T : Any> Activity.launchActivity(
    requestCode: Int = -1,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        startActivityForResult(intent, requestCode, options)
    } else {
        startActivityForResult(intent, requestCode)
    }

    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
}

@SuppressLint("ObsoleteSdkInt")
inline fun <reified T : Any> Context.launchActivity(
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        startActivity(intent, options)
    } else {
        startActivity(intent)
    }
}

inline fun <reified T : Any> newIntent(context: Context): Intent =
    Intent(context, T::class.java)

/**
 * Co-routine extension function for async job
 */
object Coroutines {
    fun io(work: suspend (() -> Unit)): Job =
        CoroutineScope(Dispatchers.IO).launch {
            work()
        }

    fun <T : Any> ioThenMain(work: suspend (() -> T?), callback: ((T?) -> Unit)): Job =
        CoroutineScope(Dispatchers.Main).launch {
            val data = CoroutineScope(Dispatchers.IO).async rt@{
                return@rt work()
            }.await()
            callback(data)
        }
}


fun Float.formatDecimal(numberOfDecimals: Int = 2): Double =
    "%.${numberOfDecimals}f".format(this).toDouble()