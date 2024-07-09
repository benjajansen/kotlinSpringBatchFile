package org.transaction_file.service

import org.springframework.batch.item.file.FlatFileHeaderCallback
import org.springframework.batch.item.file.FlatFileItemWriter
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor
import org.springframework.batch.item.file.transform.DelimitedLineAggregator
import org.springframework.core.io.FileSystemResource
import org.transaction_file.domain.model.ValidatedTransaction
import java.io.Writer

class ValidatedTransactionItemWriter(private val outputPath: String) :
    FlatFileItemWriter<ValidatedTransaction>() {

    init {
        this.setName("TransactionItemWriter")
        this.setResource(FileSystemResource(outputPath))
        this.setLineAggregator(ValidatedTransactionalLineAggregator())
        this.setHeaderCallback(ValidatedTransactionFileHeaderCallback())
    }
}

class ValidatedTransactionalLineAggregator : DelimitedLineAggregator<ValidatedTransaction>() {
    init {
        this.setDelimiter(",")
        this.setFieldExtractor(BeanWrapperFieldExtractor<ValidatedTransaction>().apply {
            this.setNames(arrayOf(
                "transaction.reference",
                "transaction.accountNumber",
                "transaction.description",
                "transaction.startBalance",
                "transaction.mutation",
                "transaction.endBalance",
                "validationResult"
            ))
        })
    }
}

class ValidatedTransactionFileHeaderCallback : FlatFileHeaderCallback {
    override fun writeHeader(writer: Writer) {
        writer.write("Reference,Account Number,Description,Start Balance,Mutation,End Balance,Validation Result")
    }
}