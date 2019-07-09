package k.bs.androidcheat.cheat

import kr.nextm.lib.AppInstance
import rx_activity_result2.RxActivityResult

object Cheat {
    lateinit var initActivity: Class<*>
    lateinit var cheatActivity: Class<*>
    inline fun <reified INIT_ACTIVITY, reified CHEAT_ACTIVITY> register() {
        initActivity = INIT_ACTIVITY::class.java
        cheatActivity = CHEAT_ACTIVITY::class.java
        RxActivityResult.register(AppInstance.get())
    }
}