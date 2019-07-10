package com.androidcheat

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import k.bs.androidcheat.cheat.Cheat
import k.bs.androidcheat.cheat.CheatActivity
import k.bs.androidcheat.permission.GrantPermissionCheckManager


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Cheat.register<MainActivity,CheatActivity>()
//        GrantPermissionCheckManager.check(this)
    }
}
