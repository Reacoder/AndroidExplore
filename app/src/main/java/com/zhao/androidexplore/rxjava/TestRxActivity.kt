package com.zhao.androidexplore.rxjava

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding3.widget.textChanges
import com.zhao.androidexplore.R
import com.zhao.androidexplore.utils.FFLog
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_test.rxEditText

class TestRxActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        init()
    }

    private fun init() {
        rxEditText.textChanges()
            .map { it.toString() }
//            .skip(1)
            .subscribe(object : Observer<String> {
                override fun onNext(t: String) {
                    FFLog.d("onNext t=${t}")
                }

                override fun onComplete() {
                    FFLog.d("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    FFLog.d("onSubscribe d=${d}")
                }

                override fun onError(e: Throwable) {
                    FFLog.d("onError e=${e}")
                }
            })
    }
}
