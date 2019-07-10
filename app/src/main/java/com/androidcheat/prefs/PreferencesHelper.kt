package com.androidcheat.prefs


import android.content.SharedPreferences
import android.preference.PreferenceManager
import k.bs.androidcheat.util.GsonHelper
import kr.nextm.lib.AppInstance


object PreferencesHelper {

    private val sharedPreferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(AppInstance.get())

    /**
     * Need to restart application to avoid side effects after calling this method.
     */
    fun clear() {
        sharedPreferences
                .edit()
                .clear()
                .apply()
    }

    operator fun set(key: Any, anyValue: Any) {
        set(key, anyValue.toString())
    }

    operator fun set(key: Any, value: CharSequence) {
        sharedPreferences.edit()
                .putString(key.toString(), value.toString())
                .apply()
    }

    fun setObject(key: Any, obj: Any) {
        set(key, GsonHelper.toJson(obj))
    }

    operator fun get(any: Any, defaultValue: Any): String {
        return get(any, defaultValue.toString())
    }

    operator fun get(any: Any, defaultValue: Int): Int {
        return get(any, defaultValue.toString()).toInt()
    }

    @JvmOverloads
    operator fun get(key: Any, defaultValue: String = ""): String {
        return sharedPreferences.getString(key.toString(), defaultValue)
    }

    inline fun <reified T> getObject(key: Any, noinline functionDefaultObjectCreator: (t: Throwable) -> T): T {
        val json = get(key)
        return GsonHelper.fromJson(json, functionDefaultObjectCreator)
    }


    fun remove(key: Any) {
        sharedPreferences.edit()
                .remove(key.toString())
                .apply()
    }

    fun isEmpty(key: Any): Boolean {
        val value = get(key, "")
        return value.isEmpty()
    }

    fun isNotEmpty(key: String) = !isEmpty(key)

}