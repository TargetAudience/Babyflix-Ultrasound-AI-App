package com.babyfilx.validation

import javax.inject.Inject

class PasswordValidation @Inject constructor(){

    fun execute(password: String): ValidationResult {
        if (password.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Please enter password."
            )
        }

        if (password.length < 6) {
            return ValidationResult(
                successful = false,
                errorMessage = "The password need to consist of at least 6 characters."
            )
        }


        return ValidationResult(
            successful = false
        )
    }
}