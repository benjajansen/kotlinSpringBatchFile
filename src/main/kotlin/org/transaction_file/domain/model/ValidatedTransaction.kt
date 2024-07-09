package org.transaction_file.domain.model

class ValidatedTransaction (
    val transaction: Transaction,
    val validationResult: ValidationResult
)

enum class ValidationResult {
    VALID,
    DUPLICATE_REFERENCE,
    WRONG_END_BALANCE_CALCULATION
}