package com.zhao.androidexplore

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding3.view.clicks
import com.zhao.androidexplore.rxjava.TestRxActivity
import kotlinx.android.synthetic.main.activity_main.mainBtn

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    @SuppressLint("CheckResult")
    private fun init() {
        mainBtn.clicks().subscribe {
            startActivity(Intent(this, TestRxActivity::class.java))
        }
    }
}
