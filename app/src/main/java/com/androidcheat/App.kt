package com.androidcheat

import android.app.Application
import com.androidcheat.cheat.Cheat
import com.androidcheat.cheat.CheatActivity
import rx_activity_result2.RxActivityResult

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        RxActivityResult.register(this)
        Cheat.register<MainActivity, CheatActivity>()
    }
}