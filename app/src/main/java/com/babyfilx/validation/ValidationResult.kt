package com.babyfilx.validation

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null

)
