package com.zhao.androidexplore.rxjava

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhao.androidexplore.R

class RxMainActivity : AppCompatActivity() {
    private val TAG = "RxMainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rx_main)

        init()
    }

    private fun init() {
        supportFragmentManager.beginTransaction()
            .add(R.id.rxContainer, SearchFragment())
            .add(R.id.rxContainer, CountDownFragment())
            .add(R.id.rxContainer, InputCheckFragment())
            .add(R.id.rxContainer, OtherFragment())
            .add(R.id.rxContainer, ComposeFragment())
            .commit()
    }
}
