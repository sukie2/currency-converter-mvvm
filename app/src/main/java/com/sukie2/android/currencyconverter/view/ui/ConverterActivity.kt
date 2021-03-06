package com.sukie2.android.currencyconverter.view.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sukie2.android.currencyconverter.R

class ConverterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.converter_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container,
                    ConverterFragment.newInstance()
                )
                .commitNow()
        }
    }

}
