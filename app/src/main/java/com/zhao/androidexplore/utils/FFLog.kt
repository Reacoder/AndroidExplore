package com.zhao.androidexplore.utils

import android.util.Log

object FFLog {
    const val TAG = "FFLog"

    fun d(tag: String = TAG, msg: String = "") {
        Log.d(tag, msg)
    }

    fun i(tag: String = TAG, msg: String = "") {
        Log.i(tag, msg)
    }

    fun e(tag: String = TAG, msg: String = "") {
        Log.e(tag, msg)
    }

    fun w(tag: String = TAG, msg: String = "") {
        Log.w(tag, msg)
    }
}