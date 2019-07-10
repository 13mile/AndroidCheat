package k.bs.androidcheat.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ScrollView
import android.widget.TextView
import k.bs.androidcheat.rx.toDialogMessage
import k.bs.androidcheat.rx.toJson
import k.bs.androidcheat.util.GsonHelper
import k.bs.androidcheat.util.TLog
import com.google.gson.Gson
import com.google.gson.JsonObject
import k.bs.androidcheat.BuildConfig

class LogView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {

    private var textView: TextView = TextView(context)

    init {
        textView.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)

        addView(textView)
    }

    fun println(s: String) {
        print(s + "\n")
    }

    fun print(message: Any?) {
        when {
            message is Throwable ->
                print(message.toDialogMessage())
            message is CharSequence ->
                print(message.toString())
            message != null ->
                print(message.toJson())
        }
    }

    fun print(s: String) {
        post {
            if (BuildConfig.DEV) {
                TLog.d("#LogView", s)
            }

            textView.text = "${textView.text}$s"
            post { fullScroll(View.FOCUS_DOWN) }
        }
    }

    fun println(e: Throwable) {
        println("cause:${e.cause} message:${e.message}")
    }

    fun println(map: Map<String, String>) {
        println(gson.toJson(map))
    }

    fun println(jsonObject: JsonObject) {
        println(jsonObject.toString())
    }

    fun println(message: Any?) {
        print(message)
        print("\n")
    }

    fun getTextView(): TextView = textView

    val gson: Gson
        get() = GsonHelper.pretty

    fun clear() {
        textView.text = ""
    }
}