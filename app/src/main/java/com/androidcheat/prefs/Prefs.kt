package com.androidcheat.prefs

import com.androidcheat.BuildConfig
import k.bs.androidcheat.exception.ErrorLogException
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import k.bs.androidcheat.prefs.Cheat
import k.bs.androidcheat.prefs.PreferencesHelper
import k.bs.androidcheat.prefs.Prefs
import kr.nextm.lib.TLog

class Prefs {

    var cheat = Cheat()

    companion object {
        private val version = BuildConfig.VERSION_SETTINGS
        private fun getPrefsKeyFor(version: Int) = "${Prefs::class.java.canonicalName}/$version"
        private val VERSION_KEY = "${Prefs::class.java.canonicalName}.VERSION"

        @JvmStatic
        val instance: Prefs
                by lazy {
                    val installedVersion = PreferencesHelper[VERSION_KEY, version]
                    if (installedVersion < version) {
                        (installedVersion until version).forEach { versionFrom ->
                            migrationPrefs(versionFrom)
                        }
                    }

                    val instance = PreferencesHelper.getObject(getPrefsKeyFor(version)) { Prefs() }

                    /**
                     * 치트 찌꺼기 남아 있어서 부작용 생길 경우 방지
                     */
                    instance.cheat = Cheat()

                    instance
                }

        private fun migrationPrefs(versionFrom: Int) {
            val obj = PreferencesHelper.getObject<JsonObject>(getPrefsKeyFor(versionFrom)) {
                throw ErrorLogException("Cannot create JsonObject from ${getPrefsKeyFor(versionFrom)}")
            }

            val settings = obj
                .getAsJsonObject("settings$$versionFrom")

            when (versionFrom) {
                1 -> {
                    /**
                     * terminal 부분 따로 분리함.
                     */
                    val terminal = JsonObject()
                    terminal.addProperty("mode", settings["terminalMode"].asString)
                    terminal.addProperty("playSoundOnNotifications", settings["playSoundOnNotifications"].asString)
                    terminal.addProperty("tdpayOrderSortedBy", settings["tdpayOrderSortedBy"].asString)

                    obj.add("terminal$1", terminal)
                }
                2 -> {
                    /**
                     * 터미널모드 -> 서비스모드 명칭 변경으로 인한 이름 변경
                     */
                    obj.add("service$1", obj["terminal$1"])
                    obj.remove("terminal$1")
                }
                3 -> {
                    /**
                     * KISCOMM -> NULL 로 되는 버그로 인한 업데이트 처리
                     */
                    val printerSettings = obj["printerSettings$1"].asJsonObject
                    val printers = printerSettings["printers"].asJsonArray
                    var newPrinters = JsonArray()
                    printers
                        .forEach { jsonElement ->
                            if (!jsonElement.asJsonObject.has("type")) {
                                jsonElement.asJsonObject.addProperty("type", "KIS")
                            }
                            newPrinters.add(jsonElement)
                        }

                    obj["printerSettings$1"].asJsonObject.remove("printers")
                    obj["printerSettings$1"].asJsonObject.add("printers", newPrinters)
                }
                4 -> {
                    /**
                     * inputSettings -> externalDeviceSettings 명칭 변경
                     */
                    obj.add("externalDeviceSettings$1", obj["inputSettings$1"])
                    obj.remove("inputSettings$1")
                }
            }

            val versionTo = versionFrom + 1
            PreferencesHelper.setObject(getPrefsKeyFor(versionTo), obj)
            PreferencesHelper[VERSION_KEY] = versionTo
        }

        fun save(functionPropertyChanging: (Prefs) -> Unit = {}) {
            functionPropertyChanging.invoke(instance)
            val timeBefore = System.currentTimeMillis()

            PreferencesHelper[VERSION_KEY] = version
            PreferencesHelper.setObject(getPrefsKeyFor(version), instance)

            val timeElapsed = System.currentTimeMillis() - timeBefore

            TLog.d("Prefs::save time elapsed in millis $timeElapsed")
        }

        fun reset() {
            save {
                it.cheat = Cheat()
            }
        }

        val cheat get() = instance.cheat
    }
}