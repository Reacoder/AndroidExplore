package com.zhao.androidexplore.rxjava

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.zhao.androidexplore.R
import com.zhao.androidexplore.rxjava.misc.clicks
import com.zhao.androidexplore.utils.FFLog
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_rx_dispose.disposeClickBtn
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS

class DisposeFragment : Fragment() {

    val TAG = "DisposeFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_rx_dispose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initClick()
    }

    @SuppressLint("CheckResult")
    fun initClick() {
        disposeClickBtn.clicks()
            /**这个时间窗口内的点击只有第一次会推送*/
            .throttleFirst(800, MILLISECONDS)
            .subscribe {
                disposeTest()
            }
    }

    @SuppressLint("CheckResult")
    fun disposeTest() {
        Observable.interval(1, SECONDS)
            .map {
                FFLog.d(TAG, "map $it", true)
                it
            }
            .compose(lifecycleTransformer(this))
            .filter {
                FFLog.d(TAG, "filter $it", true)
                true
            }.subscribe(object : Observer<Long> {
                override fun onComplete() {
                    FFLog.d(TAG, "onComplete", true)
                }

                override fun onSubscribe(d: Disposable) {
                    FFLog.d(TAG, "onSubscribe", true)
                }

                override fun onNext(t: Long) {
                    FFLog.d(TAG, "onNext $t", true)
                }

                override fun onError(e: Throwable) {
                    FFLog.d(TAG, "onError $e", true)
                }
            })
    }
}

class LifeObserver : LifecycleObserver {
    val behaviorSubject: BehaviorSubject<Lifecycle.Event> = BehaviorSubject.create()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        /**behaviorSubject 先当做Observer 接收事件，然后再把事件发射出去*/
        behaviorSubject.onNext(Lifecycle.Event.ON_CREATE)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        behaviorSubject.onNext(Lifecycle.Event.ON_START)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        behaviorSubject.onNext(Lifecycle.Event.ON_RESUME)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        behaviorSubject.onNext(Lifecycle.Event.ON_PAUSE)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        behaviorSubject.onNext(Lifecycle.Event.ON_STOP)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDistory() {
        behaviorSubject.onNext(Lifecycle.Event.ON_DESTROY)
    }
}

fun <T> lifecycleTransformer(lifecycleOwner: LifecycleOwner): ObservableTransformer<T, T> {

    val TAG = "DisposeFragment"

    val lifeObserver = LifeObserver()
    lifecycleOwner.lifecycle.addObserver(lifeObserver)

    return ObservableTransformer { originalObservable ->
        /** 现在behaviorSubject 是Observable，能够发射事件 */
        val observableBehavior = lifeObserver.behaviorSubject
            .doOnEach {
                FFLog.d(TAG, "doOnEach $it", true)
            }
            .filter {
                FFLog.d(TAG, "filter ${it == Lifecycle.Event.ON_STOP}", true)
                it == Lifecycle.Event.ON_STOP
            }

        originalObservable
            /** takeUntil操作符：observableBehavior只要发射任何一个事件，originalObservable 就会终止 */
            .takeUntil(observableBehavior)
            .map {
                FFLog.d(TAG, "Transformer map $it", true)
                it
            }
    }
}
