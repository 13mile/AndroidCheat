package k.bs.androidcheat.permission

import android.content.Context
import android.content.Intent
import io.reactivex.Single
import k.bs.androidcheat.R
import k.bs.androidcheat.cheat.FloatingViewService
import k.bs.androidcheat.rx.startActivityForResult
import k.bs.androidcheat.rx.toIntent
import k.bs.androidcheat.util.DontCare
import k.bs.androidcheat.util.TLog
import kr.nextm.lib.AppInstance
import kr.nextm.lib.TToast
import rx_activity_result2.RxActivityResult

object GrantPermissionCheckManager {
    private fun init(){
        RxActivityResult.register(AppInstance.get())
    }
    fun check(context: Context) {
        init()
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
        init()

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