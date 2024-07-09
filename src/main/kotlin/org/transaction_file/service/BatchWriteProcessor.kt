package org.transaction_file.service

import org.springframework.batch.item.ItemProcessor
import org.transaction_file.domain.model.Transaction
import java.math.BigDecimal

class BatchWriteProcessor: ItemProcessor<Transaction, Transaction> {

    override fun process(transaction: Transaction): Transaction {
        val endBalance: BigDecimal = transaction.startBalance + transaction.mutation
        return transaction.copy(endBalance = endBalance)
    }
}