package com.babyfilx.validation

import javax.inject.Inject

class RepeatPasswordValidation @Inject constructor(){

    fun execute(password: String,repeatPassword:String): ValidationResult {
        if (repeatPassword.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = "The confirm password can't be blank."
            )
        }

        if (password !=repeatPassword) {
            return ValidationResult(
                successful = false,
                errorMessage = "The password doesn't match"
            )
        }


        return ValidationResult(
            successful = false
       )
    }
}