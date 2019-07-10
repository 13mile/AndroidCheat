package com.androidcheat.cheat

import android.view.Gravity
import android.view.View
import android.view.WindowManager
import k.bs.androidcheat.cheat.FloatingLogView
import k.bs.androidcheat.cheat.FloatingViewService

class FloatingLogViewService : FloatingViewService() {
    override fun setViewPosition(params: WindowManager.LayoutParams) {
        params.gravity = Gravity.LEFT or Gravity.CENTER_HORIZONTAL
    }

    override fun createClientView(params: WindowManager.LayoutParams): View {
        return FloatingLogView(this, params)
    }
}