package k.bs.androidcheat.cheat

import android.app.Application
import rx_activity_result2.RxActivityResult

object Cheat {
    lateinit var initActivity: Class<*>
    lateinit var cheatActivity: Class<*>
    inline fun <reified INIT_ACTIVITY, reified CHEAT_ACTIVITY> register(application: Application) {
        initActivity = INIT_ACTIVITY::class.java
        cheatActivity = CHEAT_ACTIVITY::class.java
        RxActivityResult.register(application)
    }
}