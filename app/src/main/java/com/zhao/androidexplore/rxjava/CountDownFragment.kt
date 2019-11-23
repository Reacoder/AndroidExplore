package com.zhao.androidexplore.rxjava

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zhao.androidexplore.R
import com.zhao.androidexplore.utils.FFLog
import com.zhao.androidexplore.rxjava.misc.clicks
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_rx_count_down.clickBtn
import kotlinx.android.synthetic.main.fragment_rx_count_down.timerBtn
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS

class CountDownFragment : Fragment() {

    val TAG = "CountDownFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_rx_count_down, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initClick()
        initCountDown()
//        initCountDown2()
    }

    /**
     * 倒计时获取验证码操作
     */
    @SuppressLint("CheckResult")
    fun initCountDown2() {
        var count = 10L
        timerBtn
            .clicks()
            .throttleFirst(800, MILLISECONDS)
            .doOnNext {
                timerBtn.isEnabled = false
                timerBtn.setBackgroundColor(Color.parseColor("#8039c6c1"))
            }
            .flatMap {
                /**每隔一秒发送，0、1、2、3。。。*/
                Observable.interval(0, 1, SECONDS)
            }
            /**最终会发射complete事件，而onComplete 响应的同时也会调用dispose，拆掉整个流*/
            .take(count + 1)
            .map {
                count - it
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Long> {
                override fun onSubscribe(d: Disposable) {
                    FFLog.d(TAG, "onSubscribe")
                }

                override fun onError(e: Throwable) {
                    FFLog.d(TAG, "onError")
                }

                override fun onComplete() {
                    timerBtn.text = "重新获取"
                    timerBtn.isEnabled = true
                    timerBtn.setBackgroundColor(Color.parseColor("#d1d1d1"))
                    /**重新建立链接*/
                    AndroidSchedulers.mainThread().scheduleDirect {
                        initCountDown2()
                    }
                }

                override fun onNext(t: Long) {
                    timerBtn.text = "$t"
                }
            })
    }

    /**
     * 倒计时操作
     */
    @SuppressLint("CheckResult")
    fun initCountDown() {
        var count = 10L
        timerBtn
            .clicks()
            .throttleFirst(800, MILLISECONDS)
            .subscribe {
                timerBtn.isEnabled = false
                timerBtn.setBackgroundColor(Color.parseColor("#39c6c1"))
                /**每隔一秒发送，0、1、2、3。。。*/
                Observable.interval(0, 1, SECONDS)
                    /**最终会发射complete事件，拆掉整个流*/
                    .take(count + 1)
                    .map {
                        count - it
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Long> {
                        override fun onSubscribe(d: Disposable) {
                            FFLog.d(TAG, "onSubscribe")
                        }

                        override fun onError(e: Throwable) {
                            FFLog.d(TAG, "onError")
                        }

                        override fun onComplete() {
                            timerBtn.text = "重新获取"
                            timerBtn.isEnabled = true
                            timerBtn.setBackgroundColor(Color.parseColor("#d1d1d1"))
                        }

                        override fun onNext(t: Long) {
                            timerBtn.text = "$t"
                        }
                    })
            }
    }

    /**
     * 点击去重
     */
    @SuppressLint("CheckResult")
    fun initClick() {
        clickBtn.clicks()
            /**这个时间窗口内的点击只有第一次会推送*/
            .throttleFirst(800, MILLISECONDS)
            .subscribe {
                FFLog.d(TAG, "result==>>$it", true)
            }
    }
}