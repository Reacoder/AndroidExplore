package com.zhao.androidexplore.utils

import android.util.Log

object FFLog {
    const val TAG = "FFLog"

    fun d(msg: String, tag: String = TAG) {
        Log.d(tag, msg)
    }

    fun i(msg: String, tag: String = TAG) {
        Log.i(tag, msg)
    }

    fun e(msg: String, tag: String = TAG) {
        Log.e(tag, msg)
    }

    fun w(msg: String, tag: String = TAG) {
        Log.w(tag, msg)
    }
}