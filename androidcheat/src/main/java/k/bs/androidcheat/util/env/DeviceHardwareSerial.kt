package k.bs.androidcheat.util.env

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import kr.nextm.lib.AppInstance
import kr.nextm.lib.PreferencesHelper
import java.util.*

object DeviceHardwareSerial {
    @SuppressLint("MissingPermission", "HardwareIds")
    fun get(): String {
        val telephonyManager = getTelephonyManager()

        try {
            val deviceId = telephonyManager.deviceId
            val simSerial = telephonyManager.simSerialNumber
            val androidId = Settings.Secure.getString(
                    AppInstance.get().contentResolver,
                    Settings.Secure.ANDROID_ID)
            val uuid = UUID(
                    androidId.hashCode().toLong(),
                    (deviceId.hashCode().toLong().shl(32)) or simSerial.hashCode().toLong())

            val result = uuid.toString()

            return result
        } catch (t: Throwable) {
            val key = "DEVICE_ID"
            if (PreferencesHelper.isNotEmpty(key))
                return PreferencesHelper[key]

            val uuid = UUID.randomUUID().toString()
            PreferencesHelper[key] = uuid

            return uuid
        }
    }

    private fun getTelephonyManager() =
            (AppInstance.get().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager)


    @SuppressLint("HardwareIds")
    fun getSerial(): String {

        val serial = Build.SERIAL

        if (Build.MODEL == "MGT101") {
            return "9LEB1$serial"
        }

        if (isValidSerial(serial))
            return serial

        return get()
    }

    private fun isValidSerial(serial: String): Boolean {
        return when {
            serial.isBlank() -> false
            serial.toLowerCase() in listOf("unknown") -> false
            else -> true
        }
    }


}