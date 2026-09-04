package com.optimus.iptv

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this)
        tv.text = "Login saved. Channel list coming in the next stage."
        tv.textSize = 24f
        setContentView(tv)
    }
}
