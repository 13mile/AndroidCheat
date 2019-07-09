package com.androidcheat.util

import android.os.Build
import android.text.Html
import android.text.Spanned
import com.androidcheat.BuildConfig
import com.androidcheat.rx.toDialogMessageElse
import kr.nextm.lib.AppInstance

fun CharSequence.wrapWith(): CharSequence {
    return wrapWith("(", ")")
}

fun CharSequence.wrapWith(beginEnd: CharSequence): CharSequence {
    require(beginEnd.length == 2)

    return wrapWith(beginEnd.take(1), beginEnd.takeLast(1))
}

fun CharSequence.wrapWith(begin: CharSequence, end: CharSequence): CharSequence {
    if (isEmpty())
        return ""

    return "$begin$this$end"
}

fun Int.getString(): String {
    return AppInstance.get().getString(this)
}

fun Int.getStringList(): List<String> {
    return AppInstance.get().resources.getStringArray(this).toList()
}

fun Int.getFormatString(vararg formatArgs: Any): String {
    return AppInstance.get().getString(this, *formatArgs)
}


fun String.toHtml(): Spanned {
    val newLineReplacedToBr = if (!contains("<br>"))
        replace("\n", "<br>")
    else
        this

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        Html.fromHtml(newLineReplacedToBr, Html.FROM_HTML_MODE_LEGACY)
    else
        Html.fromHtml(newLineReplacedToBr)
}

fun CharSequence?.toSafeInt(defaultValue: Int = 0): Int {
    return try {
        this!!.filter { char ->
            char.isDigit() ||
                    char == '-' ||
                    char == '.'
        }.toString().toInt()
    } catch (t: Throwable) {
        defaultValue
    }
}

fun CharSequence?.toSafeDouble(defaultValue: Double = 0.0): Double {
    return try {
        this!!.filter { char ->
            char.isDigit() ||
                    char == '-' ||
                    char == '.'
        }.toString().toDouble()
    } catch (t: Throwable) {
        defaultValue
    }
}


fun String?.defaultForEmpty(defaultValue: String): String {
    if (isNullOrEmpty())
        return defaultValue
    return this!!
}

fun String?.nonNull() = this ?: ""

fun String.addDebugSuffix(t: Throwable): String {
    return addDebugSuffix(toDialogMessageElse(t))
}

fun String.addDebugSuffix(debugMessage: String): String {
    if (BuildConfig.DEV) {
        return plus(debugMessage.wrapWith("\nDEBUG[", "]"))
    }
    return this
}


