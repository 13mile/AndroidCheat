package k.bs.androidcheat.permission

import android.content.Context
import android.content.Intent
import k.bs.androidcheat.cheat.FloatingViewService
import k.bs.androidcheat.rx.startActivityForResult
import k.bs.androidcheat.rx.toIntent
import k.bs.androidcheat.util.DontCare
import k.bs.androidcheat.util.TLog
import io.reactivex.Single
import k.bs.androidcheat.BuildConfig
import k.bs.androidcheat.R
import kr.nextm.lib.TToast

object GrantPermissionCheckManager {
    fun check(context: Context) {
        checkPermission(context)
            .subscribe({
                showDebugInfoView(context)
            }, { e ->
                TLog.e(e)
                TToast.show(
                    R.string.app_will_run_after_getting_essential_permissions
                )
            })
            .let { }
    }

    fun checkWithRx(context: Context): Single<DontCare> {
        return checkPermission(context)
    }

    private fun checkPermission(context: Context): Single<DontCare> {
        return GrantPermissionsActivity::class.java
            .toIntent()
            .startActivityForResult(context)
    }


    fun showDebugInfoView(context: Context) {
        context.startService(Intent(context, FloatingViewService::class.java))
    }
}