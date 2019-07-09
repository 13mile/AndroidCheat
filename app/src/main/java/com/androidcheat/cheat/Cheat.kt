package com.androidcheat.cheat

object Cheat {
    lateinit var initActivity: Class<*>
    lateinit var cheatActivity: Class<*>
    inline fun <reified INIT_ACTIVITY, reified CHEAT_ACTIVITY> register() {
        initActivity = INIT_ACTIVITY::class.java
        cheatActivity = CHEAT_ACTIVITY::class.java
    }
}