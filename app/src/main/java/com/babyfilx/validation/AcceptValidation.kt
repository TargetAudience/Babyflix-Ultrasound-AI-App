package com.babyfilx.validation

import javax.inject.Inject

class AcceptValidation @Inject constructor(){

    fun execute(isAccept: Boolean): ValidationResult {
        if (!isAccept) {
            return ValidationResult(
                successful = false,
                errorMessage = "Please accept the terms & conditions."
            )
        }




        return ValidationResult(
            successful = false
        )
    }
}