package k.bs.androidcheat

import android.app.Application
import k.bs.androidcheat.cheat.Cheat
import k.bs.androidcheat.cheat.CheatActivity
import rx_activity_result2.RxActivityResult

open class CheatApp:Application() {
    override fun onCreate() {
        super.onCreate()
        RxActivityResult.register(this)
    }
}