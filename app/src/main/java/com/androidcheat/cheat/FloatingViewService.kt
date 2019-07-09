package com.androidcheat.cheat

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.androidcheat.prefs.Prefs
import com.androidcheat.view.CheatView
import kr.nextm.lib.TToast

open class FloatingViewService : Service() {

    private val windowManager: WindowManager
        get() = getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private lateinit var floatingView: View

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        val params: WindowManager.LayoutParams = createLayoutParams()

        setViewPosition(params)

        floatingView = createClientView(params)

        //Add the view to the window
        try {
            windowManager.addView(floatingView, params)
        } catch (t: Throwable) {
            TToast.showDebug(t)
        }
    }

    open fun createClientView(params: WindowManager.LayoutParams): View =
            CheatView(this, params)

    open fun setViewPosition(params: WindowManager.LayoutParams) {
        //Specify the view position
        params.gravity = Gravity.TOP or Gravity.LEFT        //Initially view will be added to top-left corner
        params.x = Prefs.cheat.floatingPoint.x
        params.y = Prefs.cheat.floatingPoint.y
    }

    private fun createLayoutParams(): WindowManager.LayoutParams {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT)
        } else {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(floatingView)
    }

}
