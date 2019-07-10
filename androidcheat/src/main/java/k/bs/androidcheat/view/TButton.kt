package k.bs.androidcheat.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.support.v7.widget.AppCompatButton
import android.util.AttributeSet
import android.util.TypedValue
import k.bs.androidcheat.rx.ignoreInterruption
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import k.bs.androidcheat.R
import java.util.concurrent.TimeUnit

class TButton(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : AppCompatButton(context, attrs, defStyleAttr) {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, android.support.v7.appcompat.R.attr.buttonStyle)

    init {
        setBackgroundResource(R.drawable.btn_blue)
        setTextColor(Color.WHITE)

        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25f)

        setAttrs(attrs, defStyleAttr)
    }

    private fun setAttrs(attrs: AttributeSet?, defStyle: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TButton, defStyle, 0)
        setTypeArray(typedArray)
    }


    private fun setTypeArray(typedArray: TypedArray) {
        val blink = typedArray.getBoolean(R.styleable.TButton_blink, false)

        if (blink) {
            setBlink()
        }

        typedArray.recycle()
    }

    var disposable: Disposable? = null
    fun setBlink() {
        disposable?.dispose()

        disposable = Observable.interval(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .ignoreInterruption()
                .subscribe {
                    if (it.toInt() % 2 == 0) {
                        setBackgroundResource(R.drawable.btn_orange)
                    } else {
                        setBackgroundResource(R.drawable.btn_blue)
                    }
                }
    }

    override fun onDetachedFromWindow() {
        disposable?.dispose()
        super.onDetachedFromWindow()
    }



}