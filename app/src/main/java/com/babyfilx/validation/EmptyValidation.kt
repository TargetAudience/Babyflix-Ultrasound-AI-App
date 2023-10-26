package com.babyfilx.validation

import android.util.Patterns
import com.babyfilx.data.enums.ValidationEnum
import javax.inject.Inject

class EmptyValidation @Inject constructor() {

    fun execute(text: String, type: ValidationEnum = ValidationEnum.First): ValidationResult {
        if (text.trim().isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Please enter your ${if (type == ValidationEnum.First) "First Name" else if (type == ValidationEnum.Phone) "Phone number" else if (type == ValidationEnum.Date) "Date" else if (type == ValidationEnum.clinicCode) "clinic code" else "Last Name"}."
            )
        } else if (type == ValidationEnum.Phone && text.length < 7) {
            return ValidationResult(
                successful = false,
                errorMessage = "Please enter at least 7 digit phone number."
            )
        }


        return ValidationResult(
            successful = false
        )
    }
}