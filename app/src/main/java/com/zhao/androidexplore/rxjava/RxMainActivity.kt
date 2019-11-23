package com.zhao.androidexplore.rxjava

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhao.androidexplore.R
import com.zhao.androidexplore.utils.FFLog
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlin.concurrent.thread

class RxMainActivity : AppCompatActivity() {
    private val TAG = "RxMainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rx_main)

        init()
    }

    private fun init() {
        supportFragmentManager.beginTransaction()
            .add(R.id.rxContainer,SearchFragment())
            .add(R.id.rxContainer,CountDownFragment())
            .add(R.id.rxContainer,InputCheckFragment())
            .commit()
//        invokeTest()
//        threadSwitchTest()
    }

    @SuppressLint("CheckResult")
    fun invokeTest() {
        Observable.create<Any?> {
            it.onNext(1)
            it.onComplete()
        }
            .map {
                it
            }
            .subscribe(object : Observer<Any> {
                override fun onSubscribe(d: Disposable) {
                    FFLog.d(TAG, "onSubscribe")
                }

                override fun onError(e: Throwable) {
                    FFLog.d(TAG, "onError")
                }

                override fun onComplete() {
                    FFLog.d(TAG, "onComplete")
                }

                override fun onNext(t: Any) {
                    FFLog.d(TAG, "onNext")
                }
            })
    }

    /**
     * 线程切换测试，调度器里调用新线程。就像闭包一样，如果在别的线程发射数据，subscribeOn 并不起作用，
     * 数据流还是运行emitter 所在的线程，直到遇到observeOn 切换线程。
     *
     * 运行结果：
    D/RxMainActivity: 线程=RxCachedThreadScheduler-1:  filter=100
    D/RxMainActivity: 线程=RxNewThreadScheduler-1:  map=100
    D/RxMainActivity: 线程=自定义线程:  filter=200
    D/RxMainActivity: 线程=RxNewThreadScheduler-1:  map=200
    D/RxMainActivity: 线程=main:  onNext=100
    D/RxMainActivity: 线程=main:  onNext=200
     */
    @SuppressLint("CheckResult")
    fun threadSwitchTest() {
        Observable.create<Int> { emitter ->
            emitter.onNext(100)
            thread(name = "自定义线程") {
                emitter.onNext(200)
            }
        }
            .filter {
                FFLog.d(TAG, "filter=$it", true)
                true
            }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .map {
                FFLog.d(TAG, "map=$it", true)
                it
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                FFLog.d(TAG, "onNext=$it", true)
            }
    }
}
