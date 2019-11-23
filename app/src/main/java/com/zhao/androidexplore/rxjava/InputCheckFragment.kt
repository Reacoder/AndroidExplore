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
import io.reactivex.Observable
import io.reactivex.functions.Function3
import kotlinx.android.synthetic.main.fragment_rx_input_check.inputNameEdit
import kotlinx.android.synthetic.main.fragment_rx_input_check.inputPhoneEdit
import kotlinx.android.synthetic.main.fragment_rx_input_check.inputSexEdit
import kotlinx.android.synthetic.main.fragment_rx_input_check.inputSubmit

class InputCheckFragment : Fragment() {

    val TAG = "InputCheckFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_rx_input_check, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initInputCheck()
    }

    @SuppressLint("CheckResult")
    private fun initInputCheck() {
        val observableName = inputNameEdit.textChanges().skip(1)
        val observablePhone = inputPhoneEdit.textChanges().skip(1)
        val observableSex = inputSexEdit.textChanges().skip(1)

        Observable.combineLatest(observableName, observablePhone, observableSex
            , Function3<CharSequence, CharSequence, CharSequence, Boolean> { name, phone, sex ->
                name.isNotEmpty() && phone.isNotEmpty() && sex.isNotEmpty()
            })
            .map {
                FFLog.d(TAG, "map $it")
                it
            }.subscribe {
                FFLog.d(TAG, "subscribe $it")
                inputSubmit.isEnabled = it
            }
    }

    @SuppressLint("CheckResult")
    private fun initInputCheck2() {
        val observableName = inputNameEdit.textChanges().skip(1)
        val observablePhone = inputPhoneEdit.textChanges().skip(1)
        val observableSex = inputSexEdit.textChanges().skip(1)

        val checkList = mutableListOf<Observable<CharSequence>>()
        checkList.add(observableName)
        checkList.add(observablePhone)
        checkList.add(observableSex)

        Observable.combineLatest(checkList) {
            val name = it[0] as CharSequence
            val phone = it[1] as CharSequence
            val sex = it[2] as CharSequence
            FFLog.d(TAG, "combineLatest")
            name.isNotEmpty() && phone.isNotEmpty() && sex.isNotEmpty()
        }.map {
            FFLog.d(TAG, "map $it")
            it
        }.subscribe {
            FFLog.d(TAG, "subscribe $it")
            inputSubmit.isEnabled = it
        }
    }
}