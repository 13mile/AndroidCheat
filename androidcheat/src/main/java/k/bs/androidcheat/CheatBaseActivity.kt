package k.bs.androidcheat

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import k.bs.androidcheat.rx.networkThread
import k.bs.androidcheat.rx.onClickWithAnimation
import k.bs.androidcheat.util.TLog
import k.bs.androidcheat.view.TButton
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_cheat.*
import java.util.concurrent.TimeUnit

abstract class CheatBaseActivity : ActivityBase() {

    val rightMenu: LinearLayout
        get() = rightMenu

    val leftMenu: LinearLayout
        get() = rightMenu

    val bottomMenu: LinearLayout
        get() = rightMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_cheat)

        leftMenu.addButton("CLEAR") {
            Observable.timer(100, TimeUnit.MILLISECONDS)
                .networkThread()
                .subscribe {
                    logView.clear()
                }
        }

        initializeMenus()

        logView.isFocusable = true
        logView.isFocusableInTouchMode = true
        logView.requestFocus()
    }

    abstract fun initializeMenus()

    protected fun LinearLayout.addButton(title: String, functionOnClick: (button: Button) -> Unit = {}): Button {
        val button = TButton(context)
        button.text = title
        button.gravity = Gravity.CENTER
        button.setAllCaps(false)

        addView(button)

        button.layoutParams =
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        button.onClickWithAnimation {
            println("### $title pressed")
            functionOnClick(button)
        }

        return button
    }

    protected fun LinearLayout.addText(title: String): TextView {
        val textView = TextView(context)
        textView.text = title
        textView.gravity = Gravity.CENTER

        addView(textView)

        textView.layoutParams =
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        return textView
    }

    protected fun LinearLayout.addEdit(
        tag: Any,
        defaultValue: String,
        functionOnClick: ((button: EditText) -> Unit)? = null
    ): EditText {
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.HORIZONTAL
        layout.layoutParams =
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        addView(layout)

        val editText = EditText(context)

        run {
            val view = Button(context)
            view.text = tag.toString()
            view.gravity = Gravity.CENTER
            view.setAllCaps(false)
            view.onClickWithAnimation {
                editText.setText(defaultValue)
                view.requestFocus()
            }

            layout.addView(view)
            val layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            layoutParams.weight = 1f
            view.layoutParams = layoutParams
        }

        run {
            editText.tag = tag
            editText.setText(defaultValue)
            editText.hint = "Input $tag"
            editText.gravity = Gravity.LEFT

            layout.addView(editText)
            val layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.weight = 4f
            editText.layoutParams = layoutParams
        }

        run {
            val view = Button(context)
            view.text = functionOnClick?.let { "Run" } ?: "CLR"
            view.gravity = Gravity.CENTER
            //            button.setAllCaps(false);
            view.onClickWithAnimation { v ->
                view.requestFocus()

                functionOnClick?.let {
                    functionOnClick(editText)
                    return@onClickWithAnimation
                }

                editText.setText("")
            }

            layout.addView(view)
            view.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        return editText
    }


    fun print(message: Any?) {
        if (message is Throwable) {
            TLog.e(message)
        }
        logView.print(message)
    }

    fun println(message: Any?) {
        print(message)
        print("\n")
    }
}