package com.androidcheat.util.env

import android.os.Build

/**
 * Runtime 환경에 정해지는 속성들 정리
 */
object Env {
    private val serial: String
        get() = DeviceHardwareSerial.getSerial()

    fun getModelName(): String = Build.MODEL


    fun getRawSerial(): String {
        require(serial.length <= 20) {
            "Serial 길이 20 미만으로 제한 $serial"
        }

        return serial
    }
}
