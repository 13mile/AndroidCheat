package k.bs.androidcheat.cheat

import android.view.Gravity
import android.view.View
import android.view.WindowManager

class FloatingLogViewService : FloatingViewService() {
    override fun setViewPosition(params: WindowManager.LayoutParams) {
        params.gravity = Gravity.LEFT or Gravity.CENTER_HORIZONTAL
    }

    override fun createClientView(params: WindowManager.LayoutParams): View {
        return FloatingLogView(this, params)
    }
}