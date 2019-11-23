package com.zhao.androidexplore.rxjava

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jakewharton.rxbinding3.widget.textChanges
import com.zhao.androidexplore.R
import com.zhao.androidexplore.utils.FFLog
import com.zhao.androidexplore.rxjava.misc.NetWorkUtil
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
                NetWorkUtil.fakeNetWork(item.toString())
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                FFLog.d(TAG, "result==>>$it", true)
            }
    }
}