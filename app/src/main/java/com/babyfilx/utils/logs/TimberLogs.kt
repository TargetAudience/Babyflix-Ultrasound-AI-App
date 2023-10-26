package com.babyfilx.utils.logs

import timber.log.Timber


fun loge(message: Any) {
    Timber.e(message.toString())
}