package k.bs.androidcheat.rx

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import k.bs.androidcheat.util.GsonHelper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.exceptions.CompositeException
import io.reactivex.schedulers.Schedulers
import k.bs.androidcheat.R
import java.util.*
import java.util.concurrent.TimeUnit

fun <T> Observable<T>.networkThread(): Observable<T> {
    return subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.mainThread(): Observable<T> {
    return subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(AndroidSchedulers.mainThread())
}

fun <T : View> T.onClickWithAnimation(function: (view: T) -> Unit): T {
    return onClickWithAnimation(500, TimeUnit.MILLISECONDS, function)
}

fun <T : View> T.onClickWithAnimation(delayForClickable: Long, timeUnit: TimeUnit, function: (view: T) -> Unit): T {
    setOnClickListener { view ->

        if (!view.isClickable)
            return@setOnClickListener
        view.isClickable = false

        startAnimation(AnimationUtils.loadAnimation(context, R.anim.animation_button_click))

        val delayForAnimation = 200L
        if (timeUnit == TimeUnit.MILLISECONDS)
            require(delayForClickable >= delayForAnimation) { "딜레이가 최소 200 milliseconds 이상이어야 함. $delayForClickable" }

        Observable.timer(delayForAnimation, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                function.invoke(this)
            }

        Observable.timer(delayForClickable, timeUnit)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                view.isClickable = true
            }

    }

    return this
}

fun Any.toJson(pretty: Boolean = true): String {
    if (this is Bundle) {
        return toJsonBundle(this, pretty)
    }

    if (pretty)
        return GsonHelper.pretty.toJson(this)
    else
        return GsonHelper.toJson(this)
}

private fun toJsonBundle(bundle: Bundle, pretty: Boolean): String {

    val map = Hashtable<String, Any>()

    bundle.keySet().forEach {
        map[it] = bundle[it]
    }

    return map.toJson(pretty)
}

fun Throwable.toDialogMessage(): String {

    return when (this) {
        is CompositeException ->
            toCompositeExceptionMessage()

        else ->
            toDialogMessageElse(this)
    }
}

private fun CompositeException.toCompositeExceptionMessage(): String {
    return if (exceptions.size == 1) {
        exceptions.first().toDialogMessage()
    } else {
        exceptions.joinToString(",\n") { it.toDialogMessage() }
            .let { messages ->
                "[Composite Error]\n$messages"
            }
    }
}

fun toDialogMessageElse(throwable: Throwable): String {
    throwable.message?.let {
        return it
    }

    return throwable.javaClass.simpleName
}

fun <T> Observable<T>.ignoreInterruption(): Observable<T> {
    return onErrorResumeNext { t: Throwable ->
        if (t is InterruptedException) {
            return@onErrorResumeNext Observable.empty()
        }

        return@onErrorResumeNext Observable.error(t)
    }
}