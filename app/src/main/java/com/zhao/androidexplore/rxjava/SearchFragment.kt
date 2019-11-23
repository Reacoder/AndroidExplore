package com.zhao.androidexplore.rxjava

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.trello.rxlifecycle3.android.lifecycle.kotlin.bindToLifecycle
import com.zhao.androidexplore.R
import com.zhao.androidexplore.rxjava.misc.textChanges
import com.zhao.androidexplore.utils.FFLog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_rx_search.searchEditText
import java.util.concurrent.TimeUnit.MILLISECONDS

class SearchFragment : Fragment() {

    val TAG = "SearchFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_rx_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initSearch()
    }

    /**
     * 搜索功能
     */
    @SuppressLint("CheckResult")
    fun initSearch() {
        searchEditText.textChanges()
            .bindToLifecycle(this)
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
                FFLog.d(TAG, "发起网络请求: item=$item", true)
                Observable.create<String> { emitter ->
                    emitter.onNext("网络返回结果: item=$item")
                    emitter.onComplete()
                }.delay(800, MILLISECONDS)
            }
            /**take 操作会拆掉整个流，所以只能搜索一次*/
//            .take(1)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                FFLog.d(TAG, "result==>>$it", true)
            }
    }
}