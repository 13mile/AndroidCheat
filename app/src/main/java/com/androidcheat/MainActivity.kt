package com.androidcheat

import kotlinx.android.synthetic.main.activity_cheat.*

class MainActivity : CheatBaseActivity() {
    override fun initializeMenus() {
        rightMenu.addText("cheat test")
        rightMenu.addButton("println"){
            println("hello world cheat android")
        }
    }
}
