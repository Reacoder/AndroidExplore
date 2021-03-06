package com.zhao.androidexplore

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding3.view.clicks
import com.zhao.androidexplore.jetpack.JetpackActivity
import com.zhao.androidexplore.rxjava.RxMainActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    @SuppressLint("CheckResult")
    private fun init() {
        rxBtn.clicks().subscribe {
            startActivity(Intent(this, RxMainActivity::class.java))
        }
        jetpackBtn.clicks().subscribe {
            startActivity(Intent(this, JetpackActivity::class.java))
        }
    }
}
