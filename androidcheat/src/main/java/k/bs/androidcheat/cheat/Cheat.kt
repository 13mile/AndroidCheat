package k.bs.androidcheat.cheat

import android.content.Context
import android.content.Intent
import k.bs.androidcheat.permission.GrantPermissionsActivity

object Cheat {
    var initActivity: Class<*>? = null
    var cheatActivity: Class<*>? = null
    inline fun <reified INIT_ACTIVITY, reified CHEAT_ACTIVITY> register() {
        initActivity = INIT_ACTIVITY::class.java
        cheatActivity = CHEAT_ACTIVITY::class.java
    }

    fun getPermission(context: Context) {
        context.startActivity(Intent(context, GrantPermissionsActivity::class.java))
    }

    fun showDebugInfoView(context: Context) {
        context.startService(Intent(context, FloatingViewService::class.java))
    }


    fun stopDebugInfoView(context: Context) {
        context.stopService(Intent(context, FloatingViewService::class.java))
    }


}