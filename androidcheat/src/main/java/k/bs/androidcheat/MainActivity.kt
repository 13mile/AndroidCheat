package k.bs.androidcheat

import android.content.Intent
import android.os.Bundle
import k.bs.androidcheat.cheat.FloatingViewService
import k.bs.androidcheat.permission.GrantPermissionsActivity
import k.bs.androidcheat.rx.startActivityForResult
import k.bs.androidcheat.rx.toIntent
import k.bs.androidcheat.util.DontCare
import k.bs.androidcheat.util.TLog
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
