package org.transaction_file.service

import org.transaction_file.domain.model.Transaction
import org.springframework.batch.item.ItemProcessor

class TransactionItemProcessor: ItemProcessor<Transaction, Transaction?> {
    private var seenReferences = mutableSetOf<String>()

    override fun process(transaction: Transaction): Transaction? {
        return if(isValid(transaction)) transaction else null
    }

    private fun isValid(transaction: Transaction): Boolean {
//        return if (
//            !isReferenceUnique(transaction) ||
//            isEndBalanceValueNegative(transaction) ||
//            !isEndBalanceCalculationValid(transaction)
//        ) {
//            false
//        } else true

        if(!isReferenceUnique(transaction)) {
            return false
        }

//        if(isEndBalanceValueNegative(transaction)) {
//            return false
//        }

        if(!isEndBalanceCalculationValid(transaction)) {
            return false
        }

        return true
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