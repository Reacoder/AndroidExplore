package com.zhao.androidexplore.rxjava

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.trello.rxlifecycle3.android.lifecycle.kotlin.bindToLifecycle
import com.zhao.androidexplore.R
import com.zhao.androidexplore.rxjava.misc.clicks
import com.zhao.androidexplore.utils.FFLog
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_rx_other.otherClickBtn
import java.util.concurrent.TimeUnit.MILLISECONDS
import kotlin.concurrent.thread

class OtherFragment : Fragment() {

    val TAG = "OtherFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_rx_other, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initClick()
    }

    @SuppressLint("CheckResult")
    fun initClick() {
        otherClickBtn.clicks()
            .bindToLifecycle(this)
            /**这个时间窗口内的点击只有第一次会推送*/
            .throttleFirst(800, MILLISECONDS)
            .subscribe {
                invokeTest()
                threadSwitch()
            }
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
    fun threadSwitch() {
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