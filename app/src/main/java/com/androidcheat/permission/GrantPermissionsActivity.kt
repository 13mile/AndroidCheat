package com.androidcheat.permission

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.RequiresApi
import com.androidcheat.BuildConfig
import com.androidcheat.R
import com.androidcheat.rx.BaseRxActivity
import com.androidcheat.util.DontCare
import com.androidcheat.util.getString
import com.androidcheat.util.ignoreNotUsedWarning
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.layout_grant_permission.*
import kr.nextm.lib.TToast

class GrantPermissionsActivity : BaseRxActivity() {
    private val CODE_DRAW_OVER_OTHER_APP_PERMISSION = 10001
    private val REQUEST_CODE_PERMISSIONS = 10002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_grant_permission)

        startToCheckPermissions()
    }

    private fun startToCheckPermissions() {
        setMessage(R.string.check_permissions.getString())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions()
        } else {
            initializeDrawOverlays()
        }
    }

    private fun setMessage(message: CharSequence) {
        textMessage.text = message
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkPermissions() {

        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        val notPermitted = permissions
            .filter { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
            .toList()

        if (notPermitted.isEmpty()) {
            initializeDrawOverlays()
            return
        }

        if (shouldShowRequestPermissionRationale(notPermitted[0])) {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.need_permission_to, notPermitted[0]))
                .setPositiveButton(android.R.string.ok
                ) { dialog, _ ->
                    dialog.dismiss()
                    callApplicationDetailsSettings()
                }
                .create()
                .show()
        } else {
            RxPermissions(this)
                .requestEach(*notPermitted.toTypedArray())
                .all { permission -> permission.granted }
                .subscribe { acceptAll ->
                    TToast.show("Premission: $acceptAll")
                    if (acceptAll) {
                        initializeDrawOverlays()
                    } else {
                        checkPermissions()
                    }
                }
                .let { ignoreNotUsedWarning(it) }

//            requestPermissions(notPermitted.toTypedArray(), REQUEST_CODE_PERMISSIONS)
        }

    }

    private fun callApplicationDetailsSettings() {

        val nextIntent = Intent()

        nextIntent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        nextIntent.data = uri

        startActivityForResult(nextIntent, REQUEST_CODE_PERMISSIONS)
    }
//
//    @RequiresApi(Build.VERSION_CODES.M)
//    override fun onRequestPermissionsResult(requestCode: Int,
//                                            permissions: Array<String>, grantResults: IntArray) {
//        when (requestCode) {
//            REQUEST_CODE_PERMISSIONS -> {
//                // If request is cancelled, the result arrays are empty.
//                checkPermissions()
//            }
//        }// other 'case' lines to check for other
//        // permissions this app might request
//    }

    private fun initializeDrawOverlays() {
        val needToGetOverlayPermission = BuildConfig.DEV &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                !Settings.canDrawOverlays(this)

        if (needToGetOverlayPermission) {
            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName"))

            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION)
        } else {
            finishWithResponse(DontCare)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            CODE_DRAW_OVER_OTHER_APP_PERMISSION -> @RequiresApi(Build.VERSION_CODES.M) {
                //Check if the permission is granted or not.
                if (Settings.canDrawOverlays(this)) {
                    finishWithResponse(DontCare)
                } else { //Permission is not available
                    TToast.show("Draw over other app permission not available. Closing the application")
                    finish()
                }
            }
            else ->
                super.onActivityResult(requestCode, resultCode, data)
        }

        @RequiresApi(Build.VERSION_CODES.M)
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {

            //Check if the permission is granted or not.
            if (Settings.canDrawOverlays(this)) {
                finishWithResponse(DontCare)
            } else { //Permission is not available
                TToast.show("Draw over other app permission not available. Closing the application")
                finish()
            }
        } else {
        }

    }
}
