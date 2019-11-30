package com.zhao.androidexplore.jetpack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhao.androidexplore.R
import com.zhao.androidexplore.jetpack.lifecycle.LifecycleFragment

class JetpackActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jetpack_main)

        init()
    }

    private fun init() {
        supportFragmentManager.beginTransaction()
            .add(R.id.jetpackContainer, LifecycleFragment())
            .commit()
    }
}