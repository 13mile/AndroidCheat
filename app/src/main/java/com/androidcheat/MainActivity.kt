package com.androidcheat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.androidcheat.cheat.FloatingViewService
import com.androidcheat.permission.GrantPermissionsActivity
import com.androidcheat.rx.startActivityForResult
import com.androidcheat.rx.toIntent
import com.androidcheat.util.DontCare
import com.androidcheat.util.TLog
import kotlinx.android.synthetic.main.activity_cheat.*
import kr.nextm.lib.TToast

class MainActivity : CheatBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GrantPermissionsActivity::class.java
            .toIntent()
            .startActivityForResult<DontCare>(this)
            .subscribe({
                showDebugInfoView()
            }, { e ->
                TLog.e(e)
                TToast.show(R.string.app_will_run_after_getting_essential_permissions)
            })
    }

    private fun showDebugInfoView() {
        if (BuildConfig.DEV) {
            startService(Intent(this, FloatingViewService::class.java))
        }
    }

    override fun initializeMenus() {
        rightMenu.addText("cheat test")
        rightMenu.addButton("println") {
            println("hello world cheat android")
        }
    }
}
