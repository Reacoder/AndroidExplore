package com.zhao.androidexplore.rxjava

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zhao.androidexplore.R
import com.zhao.androidexplore.rxjava.misc.clicks
import com.zhao.androidexplore.utils.FFLog
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_rx_backpressure.backpressureClickBtn
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit.MILLISECONDS

class BackpressureFragment : Fragment() {

    val TAG = "BackpressureFragment"

    var mSubscription: Subscription? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_rx_backpressure, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initClick()
    }

    @SuppressLint("CheckResult")
    fun initClick() {
        backpressureClickBtn.clicks()
            /**这个时间窗口内的点击只有第一次会推送*/
            .throttleFirst(800, MILLISECONDS)
            .subscribe {
                test()
            }
    }

    @SuppressLint("CheckResult")
    fun test() {
        Flowable.create(FlowableOnSubscribe<String> { emitter ->
            try {
                val inputStream: InputStream? = activity?.assets?.open("test.txt")
                val reader = InputStreamReader(inputStream!!)
                val br = BufferedReader(reader)

                var str: String? = br.readLine()
                while (str != null && !emitter.isCancelled) {
                    while (emitter.requested() == 0L) {
                        if (emitter.isCancelled) {
                            break
                        }
                    }
                    emitter.onNext(str)
                    str = br.readLine()
                }

                br.close()
                reader.close()

                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }, BackpressureStrategy.ERROR)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .subscribe(object : Subscriber<String> {

                override fun onSubscribe(s: Subscription) {
                    FFLog.d(TAG, "onSubscribe $s")
                    mSubscription = s
                    s.request(1)
                }

                override fun onNext(string: String) {
                    FFLog.d(TAG, "onNext $string")
                    try {
                        Thread.sleep(1000)
                        mSubscription?.request(1)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }

                override fun onError(t: Throwable) {
                    FFLog.d(TAG, "onError $t")
                }

                override fun onComplete() {
                    FFLog.d(TAG, "onComplete")
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mSubscription?.cancel()
    }
}
