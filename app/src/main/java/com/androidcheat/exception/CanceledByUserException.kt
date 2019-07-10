package com.androidcheat.exception

import com.androidcheat.R
import k.bs.androidcheat.util.getString

open class CanceledByUserException(message: String = R.string.canceled_by_user.getString()) : RuntimeException(message) {

    constructor(throwable: Throwable) : this(throwable.message!!)
}