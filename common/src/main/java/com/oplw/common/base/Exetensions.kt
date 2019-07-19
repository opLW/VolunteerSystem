package com.oplw.volunteersystem.base

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

/**
 *
 *   @author opLW
 *   @date  2019/7/11
 */
fun Activity.isNetConnected(): Boolean {
    val manager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netWokInfo = manager.activeNetworkInfo
    return if (netWokInfo == null) {
        showToastInBottom("网络开小差")
        false
    } else {
        true
    }
}

fun Activity.hideKeyBroad() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
}

fun Activity.showToastInBottom(prompt: String?) {
    with(Toast.makeText(this, prompt, Toast.LENGTH_SHORT)) {
        setGravity(Gravity.BOTTOM, 0 , 0)
        show()
    }
}

fun Activity.showToastInCenter(prompt: String?) {
    with(Toast.makeText(this, prompt, Toast.LENGTH_SHORT)) {
        setGravity(Gravity.CENTER, 0 , 0)
        show()
    }
}