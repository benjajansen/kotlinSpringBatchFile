package org.transaction_file.job

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import org.transaction_file.domain.model.Transaction
import org.transaction_file.service.BatchWriteProcessor
import org.transaction_file.service.TransactionItemWriter
import org.transaction_file.utils.EasyRandomFactory

@Configuration
@EnableBatchProcessing
class BatchWriteTestConfig(
    private val jobRepository: JobRepository,
    @Value("\${generated.output.directory}") private val outputDirectory: String
) {
    private var count = 0
    private val maxRecordsToGenerate = 1000000

    @Bean
    fun batchWriteReader(): ItemReader<Transaction> {
        val easyRandom = EasyRandomFactory.newTransactionInstance()
        return ItemReader {
            if (count < maxRecordsToGenerate) {
                count++
                easyRandom.nextObject(Transaction::class.java)
            } else {
                null
            }
        }
    }

//    @Bean
//    fun batchWriteReader(): ItemReader<Transaction> {
//        return ItemReader {
//            if (count < maxRecordsToGenerate) {
//                val easyRandom = EasyRandomFactory.newTransactionInstance()
//                count++
//                easyRandom.nextObject(Transaction::class.java)
//            } else {
//                null
//            }
//        }
//    }

    @Bean
    fun batchWriteProcessor(): ItemProcessor<Transaction, Transaction> {
        return BatchWriteProcessor()
    }

    @Bean
    fun batchWriteWriter(): ItemWriter<Transaction> {
        return TransactionItemWriter(outputDirectory)
    }

    @Bean
    protected fun generateTransactionsStep(
        transactionManager: PlatformTransactionManager?,
        batchWriteReader: ItemReader<Transaction>?,
        batchWriteProcessor: ItemProcessor<Transaction, Transaction>?,
        batchWriteWriter: ItemWriter<Transaction>?
    ): Step {
        return StepBuilder(
            "generateTransactionsStep",
            jobRepository
        ).chunk<Transaction, Transaction>(
            100000,
            transactionManager!!
        )
            .reader(batchWriteReader!!)
            .processor(batchWriteProcessor!!)
            .writer(batchWriteWriter!!)
            .build()
    }

    @Bean
    fun generateTransactionsJob(
        transactionManager: PlatformTransactionManager?
    ): Job {
        return JobBuilder("generateTransactionsJob", jobRepository)
            .start(
                generateTransactionsStep(
                    transactionManager,
                    batchWriteReader(),
                    batchWriteProcessor(),
                    batchWriteWriter()
                )
            ).build()
    }
}