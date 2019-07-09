package k.bs.androidcheat.rx

import android.app.Activity
import android.content.Intent
import k.bs.androidcheat.ActivityBase
import k.bs.androidcheat.exception.CanceledByUserException
import java.lang.reflect.Type


abstract class BaseRxActivity : ActivityBase() {
    inline fun <reified T> getInput(): T {
        return getInput(T::class.java)
    }

    fun <T> getInput(clazz: Type): T {
        return intent.getObject(clazz) as T
    }

    fun finishWithResponse(any: Any) {
        setResult(RESULT_OK, Intent().putObject(any))
        finish()
    }


    override fun onBackPressed() {
        finishWithCancel()
    }

    fun finishWithCancel() {
        finishWithException(CanceledByUserException())
    }

    fun finishWithException(t: Throwable) {
        setResult(Activity.RESULT_CANCELED, Intent().putObject(t))
        finish()
    }
}

