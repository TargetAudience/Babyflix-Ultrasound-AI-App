package com.babyfilx.validation

import android.util.Patterns
import javax.inject.Inject

class EmailValidation @Inject constructor(){

    fun execute(email: String): ValidationResult {
        if (email.trim().isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Please enter your email."
            )
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Please enter your valid email address."
            )
        }

       return ValidationResult(
            successful = false
       )
    }
}