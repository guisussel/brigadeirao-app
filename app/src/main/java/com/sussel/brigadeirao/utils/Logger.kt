package com.sussel.brigadeirao.utils

import timber.log.Timber

open class Logger(val TAG: String) {

    open fun v(msg: String) = Timber.tag(TAG).v(msg)

    open fun d(msg: String) = Timber.tag(TAG).d(msg)

    open fun i(msg: String) = Timber.tag(TAG).i(msg)

    open fun w(msg: String) = Timber.tag(TAG).w(msg)

    open fun e(msg: String) = Timber.tag(TAG).e(msg)

    open fun e(msg: String, e: Exception) = Timber.tag(TAG).e(e, msg)

    open fun e(msg: String, t: Throwable) = Timber.tag(TAG).e(t, msg)

    open fun e(t: Throwable, msg: String) = Timber.tag(TAG).e(t, msg)

    open fun e(t: Throwable) = Timber.tag(TAG).e(t)
}