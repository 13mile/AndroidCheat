package k.bs.androidcheat.util

import android.util.Log
import com.google.gson.Gson
import k.bs.androidcheat.BuildConfig

object TLog {

    const val ENABLED = BuildConfig.DEV
    private val TAG = TLog::class.java.canonicalName

    @JvmStatic
    fun e(message: Any) {
        e(TAG, message)
    }

    @JvmStatic
    fun w(message: Any) {
        w(TAG, message)
    }

    @JvmStatic
    fun i(message: Any) {
        i(TAG, message)
    }

    @JvmStatic
    fun d(message: Any) {
        d(TAG, message)
    }

    @JvmStatic
    fun v(message: Any) {
        v(TAG, message)
    }

    @JvmStatic
    fun e(e: Throwable) {
        e(TAG, e)
    }

    @JvmStatic
    fun e(tag: String, message: Any?, e: Throwable) {
        if (ENABLED)
            Log.e(tag, convertToString(message), e)
    }

    @JvmStatic
    fun e(tag: String, message: Any?) {
        if (ENABLED)
            Log.e(tag, convertToString(message))
    }

    @JvmStatic
    fun w(tag: String, message: Any) {
        if (ENABLED)
            Log.w(tag, convertToString(message))
    }

    @JvmStatic
    fun i(tag: String, message: Any) {
        if (ENABLED)
            Log.i(tag, convertToString(message))
    }

    @JvmStatic
    fun d(tag: String, message: Any) {
        if (ENABLED)
            Log.d(tag, convertToString(message))
    }

    @JvmStatic
    fun v(tag: String, message: Any) {
        if (ENABLED)
            Log.v(tag, convertToString(message))
    }

    @JvmStatic
    fun e(tag: String, e: Throwable) {
        if (ENABLED)
            e(tag, e.message, e)
    }

    private fun convertToString(message: Any?): String {
        if (message == null) return "<null>"

        if (message is String) return message

        return Gson().toJson(message)
    }

}
