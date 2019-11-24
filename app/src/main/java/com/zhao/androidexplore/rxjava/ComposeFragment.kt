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
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_rx_compose.composeClickBtn
import java.util.concurrent.TimeUnit.MILLISECONDS

class ComposeFragment : Fragment() {

    val TAG = "ComposeFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_rx_compose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initClick()
    }

    @SuppressLint("CheckResult")
    fun initClick() {
        composeClickBtn.clicks()
            .bindToLifecycle(this)
            /**这个时间窗口内的点击只有第一次会推送*/
            .throttleFirst(800, MILLISECONDS)
            .subscribe {
                composeTest()
            }
    }

    /**
     * >>> compose vs flatMap <<<
     *
     * compose 操作是更高层的抽象：它作用在整个流上，而不是作用在个别发射的item 上。具体来说有以下3点：
     *
     * 1、compose()是唯一从整个流中获取原始Observable 的方法。因此，影响整个流的操作如subscribeOn 和observeOn 需要用compose 操作符。
     *    与此相反，如果你在flapMap 中使用subscribeOn/observeOn，只会影响你在flapMap 中创建的Observable，而不会影响原始流。
     *
     * 2、如果你内联调用compose，它会在你创建流的时候立即执行。而flapMap 只有在调用onNext 的时候才会执行，也就说flapMap 转换每一个item，
     *   而compose 转换整个流。
     *
     * 3、因为flapMap 在每次上层流调用onNext 的会创建新的Observable，所以它的效率是比较低的，而compose 作用于整个流，因此效率较高。
     */
    @SuppressLint("CheckResult")
    fun composeTest() {
        Observable.just(100)
            .bindToLifecycle(this)
            .map {
                FFLog.d(TAG, "map $it", true)
                it
            }
//            .compose(io2MainScheduler())
            .flatMap {
                Observable.just(200)
                    .subscribeOn(Schedulers.computation())
                    .map {
                        FFLog.d(TAG, "内部 map $it", true)
                        it
                    }
                    .filter {
                        FFLog.d(TAG, "内部 filter $it", true)
                        true
                    }
            }
            .compose(io2MainScheduler())
            .filter {
                FFLog.d(TAG, "filter $it", true)
                true
            }.subscribe {
                FFLog.d(TAG, "subscribe $it", true)
            }
    }
}
/**
 * 日志如下：
 * D/ComposeFragment: 线程=RxCachedThreadScheduler-1: map 100
 * D/ComposeFragment: 线程=RxComputationThreadPool-2: 内部 map 200
 * D/ComposeFragment: 线程=RxComputationThreadPool-2: 内部 filter 200
 * D/ComposeFragment: 线程=main: filter 200
 * D/ComposeFragment: 线程=main: subscribe 200
 */

fun <T> io2MainScheduler(): ObservableTransformer<T, T> {
    return ObservableTransformer {
        it.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}
