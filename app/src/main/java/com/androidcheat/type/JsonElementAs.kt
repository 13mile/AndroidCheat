package com.androidcheat.type

import k.bs.androidcheat.type.OutputType
import k.bs.androidcheat.util.GsonHelper
import java.io.Serializable

open class JsonElementAs(val obj: Any, val outputType: OutputType) : Serializable {
    fun <T> get(): T = obj as T

    override fun toString() = GsonHelper.toJson(obj)
}
