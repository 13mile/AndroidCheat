package com.androidcheat.permission

import android.content.Context
import android.content.Intent
import com.androidcheat.BuildConfig
import com.androidcheat.R
import com.androidcheat.cheat.FloatingViewService
import com.androidcheat.rx.startActivityForResult
import com.androidcheat.rx.toIntent
import com.androidcheat.util.DontCare
import com.androidcheat.util.TLog
import io.reactivex.Single
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