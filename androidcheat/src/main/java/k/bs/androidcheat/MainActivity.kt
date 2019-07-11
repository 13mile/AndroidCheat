package k.bs.androidcheat

import android.os.Bundle
import k.bs.androidcheat.cheat.Cheat

class MainActivity : CheatBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Cheat.getPermission(this)
    }

    override fun onResume() {
        super.onResume()
        showDebugInfoView()
    }

    private fun showDebugInfoView() {
        if (BuildConfig.DEV) {
            Cheat.showDebugInfoView(this)
        }
    }

    override fun initializeMenus() {
        rightMenu.addText("hello world cheat android")
        rightMenu.addButton("floating cheat start") {
            Cheat.showDebugInfoView(this)
        }

        rightMenu.addButton("floating cheat stop") {
            Cheat.stopDebugInfoView(this)
        }
    }
}
