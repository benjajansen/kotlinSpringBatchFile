package org.transaction_file.service

import org.springframework.batch.item.file.FlatFileHeaderCallback
import org.transaction_file.domain.model.Transaction
import org.springframework.batch.item.file.FlatFileItemWriter
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor
import org.springframework.batch.item.file.transform.DelimitedLineAggregator
import org.springframework.core.io.FileSystemResource
import java.io.Writer

class TransactionItemWriter(private val outputPath: String) : FlatFileItemWriter<Transaction>() {
    init {
        this.setName("TransactionItemWriter")
        this.setResource(FileSystemResource(outputPath))
        this.setLineAggregator(TransactionalLineAggregator())
        this.setHeaderCallback(TransactionFileHeaderCallback())
    }
}

class TransactionalLineAggregator : DelimitedLineAggregator<Transaction>() {
    init {
        this.setDelimiter(",")
        this.setFieldExtractor(BeanWrapperFieldExtractor<Transaction>().apply {
            this.setNames(arrayOf("reference", "accountNumber", "description", "startBalance", "mutation", "endBalance"))
        })
    }
}

class TransactionFileHeaderCallback : FlatFileHeaderCallback {
    override fun writeHeader(writer: Writer) {
        writer.write("Reference,Account Number,Description,Start Balance,Mutation,End Balance")
    }
}