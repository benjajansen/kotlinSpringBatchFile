package org.transaction_file.service

import org.transaction_file.domain.model.Transaction
import org.springframework.batch.item.ItemProcessor
import org.transaction_file.domain.model.ValidatedTransaction
import org.transaction_file.domain.model.ValidationResult

class ValidatedTransactionItemProcessor: ItemProcessor<Transaction, ValidatedTransaction> {
    private var seenReferences = mutableSetOf<String>()

    override fun process(transaction: Transaction): ValidatedTransaction {
        return validate(transaction)
    }

    private fun validate(transaction: Transaction): ValidatedTransaction {
        if(!isReferenceUnique(transaction)) {
            return ValidatedTransaction(transaction, ValidationResult.DUPLICATE_REFERENCE)
        }

//        if(isEndBalanceValueNegative(transaction)) {
//            return ValidatedTransaction(transaction, ValidationResult.WRONG_END_BALANCE_CALCULATION)
//        }

        if(!isEndBalanceCalculationValid(transaction)) {
            return ValidatedTransaction(transaction, ValidationResult.WRONG_END_BALANCE_CALCULATION)
        }

        return ValidatedTransaction(transaction, ValidationResult.VALID)
    }

    /**
     * Checks if the end balance value is negative for the given transaction.
     *
     * @param transaction The transaction to validate
     * @return True if the end balance of the transaction is negative, false otherwise
     */
    private fun isEndBalanceValueNegative(transaction: Transaction): Boolean {
        return transaction.endBalance.signum() == -1
    }

    /**
     * Checks if the end balance calculation is valid for the given transaction.
     *
     * @param transaction The transaction to validate
     * @return True if end balance of the transaction equals the sum of start balance and mutation, false otherwise
     */
    private fun isEndBalanceCalculationValid(transaction: Transaction): Boolean {
        return transaction.endBalance == transaction.startBalance + transaction.mutation
    }

    /**
     * Checks if the reference for the given transaction is unique.
     *
     * @param transaction The transaction to check
     * @return True if the reference of the transaction is not present in the seen references, false otherwise
     */
    private fun isReferenceUnique(transaction: Transaction): Boolean {
        return seenReferences.add(transaction.reference)
    }
}