package com.androidcheat

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import k.bs.androidcheat.cheat.Cheat
import k.bs.androidcheat.cheat.CheatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Cheat.register<MainActivity, CheatActivity>()
        Cheat.getPermission(this)
    }

    override fun onResume() {
        super.onResume()
        Cheat.showDebugInfoView(this)
    }
}
