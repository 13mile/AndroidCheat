package com.androidcheat.rx

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.view.View
import k.bs.androidcheat.exception.CanceledByUserException
import k.bs.androidcheat.util.GsonHelper
import io.reactivex.Completable
import io.reactivex.Single
import kr.nextm.lib.AppInstance
import rx_activity_result2.Result
import rx_activity_result2.RxActivityResult
import java.lang.reflect.Type

val KEY_FOR_JSON_OBJECT = "json"

fun Bundle.putObject(obj: Any): Bundle {
    putString(KEY_FOR_JSON_OBJECT, obj.toJson())
    return this
}

fun Intent.putObject(obj: Any): Intent {
    putExtra(KEY_FOR_JSON_OBJECT, obj.toJson())
    return this
}

inline fun <reified T> Intent.getObject(): T {
    return getObject(T::class.java)
}

fun <T> Intent.getObject(clazz: Type): T {
    require(hasObject(), usageForRxActivityWithObject())
    return extras.getObject(clazz)
}

fun Intent.hasObject(): Boolean {
    require(extras != null, usageForRxActivityWithObject())
    return extras.containsKey(KEY_FOR_JSON_OBJECT)
}

inline fun <reified T> Bundle.getObject(): T {
    return getObject(T::class.java)
}

fun <T> Bundle.getObject(clazz: Type): T {
    require(containsKey(KEY_FOR_JSON_OBJECT), usageForRxActivityWithObject())

    val json = getString(KEY_FOR_JSON_OBJECT)
    return GsonHelper.fromJson(json, clazz)
}

fun usageForRxActivityWithObject() = {
    """Your bundle don't have extra for [$KEY_FOR_JSON_OBJECT]
    ******************************

    example)

    Intent(this, TargetActivity::class.java)
            .putObject(inputData))
            .startActivityForResult<ResponseData>(this)
            .subscribe()

    ******************************
    """.trimIndent()

}

fun <T : Activity> Class<T>.toIntent() = Intent(AppInstance.getApplicationContext(), this)

fun Intent.startActivity(context: Context): Completable {
    return startActivity(context as Activity)
}

fun Intent.startActivity(activity: Activity): Completable {
    return Completable.defer {
        RxActivityResult.on(activity)
                .startIntent(this)
                .ignoreElements()
    }
}

inline fun <reified T> Intent.startActivityForResult(context: Context): Single<T> {
    return startActivityForResult(context.getActivity())
}

fun <T> Intent.startActivityForResult(context: Context, clazz: Type): Single<T> {
    return startActivityForResult(context as Activity, clazz)
}

inline fun <reified T> Intent.startActivityForResult(activity: Activity): Single<T> {
    return startActivityForResult(activity, T::class.java)
}

fun <T> Intent.startActivityForResult(activity: Activity, clazz: Type): Single<T> {
    return Single.defer {
        RxActivityResult.on(activity)
                .startIntent(this)
                .firstOrError()
                .extractObject<T>(clazz)
    }
}

inline fun <reified T> Single<Result<Activity>>.extractObject(): Single<T> {
    return extractObject(T::class.java)
}

fun <T> Single<Result<Activity>>.extractObject(clazz: Type): Single<T> {
    return flatMap {
        if (it.resultCode() == Activity.RESULT_CANCELED) {
            val exception = extractExceptionFrom(it)
            Single.error(exception)
        } else {
            Single.just(it.data().getObject<T>(clazz))
        }
    }
}

fun extractExceptionFrom(result: Result<Activity>): Throwable {
    if (result.data()?.hasObject() == true) {
        return result.data().getObject()
    }
    return CanceledByUserException()
}

fun Context.getActivity(): Activity {
    var newContext = this

    while (newContext is ContextWrapper) {
        if (newContext is Activity)
            return newContext

        newContext = newContext.baseContext
    }


    throw RuntimeException("Failed to get activity from the view.")
}

fun View.getActivity(): Activity = context.getActivity()

