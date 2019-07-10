package com.androidcheat.exception

import k.bs.androidcheat.util.TLog

open class ErrorLogException(val title: String, message: Any, cause: Throwable? = null) :
        RuntimeException(message.toString(), cause) {

    constructor(message: Any, cause: Throwable? = null) :
            this("ERROR", message, cause)

    constructor(throwable: Throwable) :
            this(throwable.message ?: throwable.javaClass.simpleName, throwable.cause)

    init {
        if (TLog.ENABLED) {
            printStackTrace()
            TLog.e("ExceptionWithLog", "[$title] $message")
        }

    }

}