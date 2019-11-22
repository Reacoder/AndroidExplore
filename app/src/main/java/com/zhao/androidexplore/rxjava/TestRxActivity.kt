package com.zhao.androidexplore.rxjava

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.zhao.androidexplore.R
import com.zhao.androidexplore.utils.FFLog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_test.clickBtn
import kotlinx.android.synthetic.main.activity_test.searchEditText
import java.util.concurrent.TimeUnit.MILLISECONDS
import kotlin.concurrent.thread

class TestRxActivity : AppCompatActivity() {
    private val TAG = "TestRxActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        init()
    }

    private fun init() {
        searchTest()
//        threadSwitchTest()
    }

    fun clickTest(){
        clickBtn.clicks().throttleFirst(600,MILLISECONDS)
            .subscribe {

            }
    }

    /**
     * 搜索功能
     */
    @SuppressLint("CheckResult")
    fun searchTest() {
        searchEditText.textChanges()
            /**进入页面会发射view 的初始值，需要跳过*/
            .skip(1)
            /**防止输入内容立即搜索*/
            .debounce(400, MILLISECONDS)
            /**过滤掉不合法搜索数据*/
            .filter {
                it.isNotEmpty()
            }
            /**只推送(也就是发射)最后一次搜索结果*/
            .switchMap { item ->
                fakeNetWork(item.toString())
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                FFLog.d(TAG, "${threadPrefix()} result==>>$it")
            }
    }

    private fun fakeNetWork(key: String): Observable<String> {
        FFLog.d(TAG, "${threadPrefix()} 发起网络请求: key=$key")
        return Observable.create<String> { emitter ->
            emitter.onNext("网络返回结果: key=$key")
            emitter.onComplete()
        }.delay(800, MILLISECONDS)
    }

    /**
     * 线程切换测试，调度器里调用新线程。就像闭包一样，如果在别的线程发射数据，subscribeOn 并不起作用，
     * 数据流还是运行emitter 所在的线程，直到遇到observeOn 切换线程。
     *
     * 运行结果：
    D/TestRxActivity: 线程=RxCachedThreadScheduler-1:  filter=100
    D/TestRxActivity: 线程=RxNewThreadScheduler-1:  map=100
    D/TestRxActivity: 线程=自定义线程:  filter=200
    D/TestRxActivity: 线程=RxNewThreadScheduler-1:  map=200
    D/TestRxActivity: 线程=main:  onNext=100
    D/TestRxActivity: 线程=main:  onNext=200
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
                FFLog.d(TAG, "${threadPrefix()} filter=$it")
                true
            }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .map {
                FFLog.d(TAG, "${threadPrefix()} map=$it")
                it
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                FFLog.d(TAG, "${threadPrefix()} onNext=$it")
            }
    }

    private fun threadPrefix(): String {
        return "线程=${Thread.currentThread().name}: "
    }
}
