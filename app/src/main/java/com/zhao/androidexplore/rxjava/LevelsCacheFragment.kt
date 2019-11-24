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
import kotlinx.android.synthetic.main.fragment_rx_levels_cache.cacheClickBtn
import java.util.concurrent.TimeUnit.MILLISECONDS

class LevelsCacheFragment : Fragment() {

    val TAG = "LevelsCacheFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_rx_levels_cache, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initClick()
    }

    @SuppressLint("CheckResult")
    fun initClick() {
        cacheClickBtn.clicks()
            .bindToLifecycle(this)
            /**这个时间窗口内的点击只有第一次会推送*/
            .throttleFirst(800, MILLISECONDS)
            .subscribe {
                getData("memory")
            }
    }

    /** 获取数据 */
    @SuppressLint("CheckResult")
    private fun getData(url: String) {
        Observable.concat(getDataInMemory(url), getDataInDisk(url), getDataInNet(url))
            .first("default")
            .toObservable()
            .map {
                FFLog.d(TAG, "map $it")
                it
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onComplete() {
                    FFLog.d(TAG, "onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    FFLog.d(TAG, "onSubscribe $d")
                }

                override fun onNext(t: String) {
                    FFLog.d(TAG, "onNext $t")
                }

                override fun onError(e: Throwable) {
                    FFLog.d(TAG, "onError $e")
                }
            })
    }

    /** 从内存中获取 */
    private fun getDataInMemory(url: String): Observable<String> {
        val memoryCache = mutableMapOf<String, String>()
        memoryCache["memory"] = "Memory"

        return Observable.create { emitter ->
            if (memoryCache.containsKey(url)) {
                emitter.onNext(memoryCache[url]!!)
            }
            emitter.onComplete()
        }
    }

    /** 从硬盘中获取 */
    private fun getDataInDisk(url: String): Observable<String> {
        val diskCache = mutableMapOf<String, String>()
        diskCache["disk"] = "Disk"

        return Observable.create { emitter ->
            if (diskCache.containsKey(url)) {
                emitter.onNext(diskCache[url]!!)
            }
            emitter.onComplete()
        }
    }

    /** 从网络中获取 */
    private fun getDataInNet(url: String): Observable<String> {
        return Observable.create<String> { emitter ->
            emitter.onNext("Net")
            emitter.onComplete()
        }.subscribeOn(Schedulers.io())
    }
}