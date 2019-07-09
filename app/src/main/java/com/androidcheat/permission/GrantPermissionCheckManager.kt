package com.androidcheat.permission

import android.content.Context
import android.content.Intent
import com.androidcheat.BuildConfig
import com.androidcheat.R
import k.bs.androidcheat.cheat.FloatingViewService
import k.bs.androidcheat.rx.startActivityForResult
import k.bs.androidcheat.rx.toIntent
import k.bs.androidcheat.util.DontCare
import k.bs.androidcheat.util.TLog
import io.reactivex.Single
import k.bs.androidcheat.permission.GrantPermissionsActivity
import kr.nextm.lib.TToast

object GrantPermissionCheckManager {

    fun check(context: Context) {
        checkPermission(context)
            .subscribe({
                showDebugInfoView(context)
            }, { e ->
                TLog.e(e)
                TToast.show(R.string.app_will_run_after_getting_essential_permissions)
            })
            .let {  }
    }

    fun checkWithRx(context: Context): Single<DontCare> {
        return checkPermission(context)
    }

    private fun checkPermission(context: Context): Single<DontCare> {
        return GrantPermissionsActivity::class.java
            .toIntent()
            .startActivityForResult(context)
    }


    private fun showDebugInfoView(context: Context) {
        if (BuildConfig.DEV) {
            context.startService(Intent(context, FloatingViewService::class.java))
        }
    }
}