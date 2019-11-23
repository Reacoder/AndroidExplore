package com.zhao.androidexplore.rxjava.misc

import com.zhao.androidexplore.utils.FFLog
import io.reactivex.Observable
import java.util.concurrent.TimeUnit.MILLISECONDS

object NetWorkUtil {

    val TAG = "NetWorkUtil"

    fun fakeNetWork(key: String): Observable<String> {
        FFLog.d(TAG, "发起网络请求: key=$key", true)
        return Observable.create<String> { emitter ->
            emitter.onNext("网络返回结果: key=$key")
            emitter.onComplete()
        }.delay(800, MILLISECONDS)
    }
}