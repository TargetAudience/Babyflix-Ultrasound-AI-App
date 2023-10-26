package com.babyfilx.utils.exception

import java.io.IOException
import java.lang.RuntimeException

class NoInternetException(message: String) : IOException(message)