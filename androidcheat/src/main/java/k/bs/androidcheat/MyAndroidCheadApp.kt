package k.bs.androidcheat

import android.app.Application
import k.bs.androidcheat.cheat.Cheat
import k.bs.androidcheat.cheat.CheatActivity
import rx_activity_result2.RxActivityResult

class MyAndroidCheadApp:Application() {
    override fun onCreate() {
        super.onCreate()
        RxActivityResult.register(this)
//        Cheat.register<MainActivity, CheatActivity>()
    }
}