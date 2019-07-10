package k.bs.androidcheat.prefs

import android.graphics.Point

data class Cheat(var floatingPoint: Point = Point(0, 0),
                 var floatingCheatView: Boolean = true) {


    companion object {
        val instance get() = Prefs.instance.cheat
    }
}
