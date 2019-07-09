package k.bs.androidcheat.type

import k.bs.androidcheat.util.GsonHelper
import java.io.Serializable

open class JsonElementAs(val obj: Any, val outputType: OutputType) : Serializable {
    fun <T> get(): T = obj as T

    override fun toString() = GsonHelper.toJson(obj)
}
