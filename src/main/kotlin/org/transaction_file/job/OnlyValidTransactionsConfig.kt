package org.transaction_file.job

import org.transaction_file.domain.model.Transaction
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
import org.springframework.core.io.Resource
import org.springframework.transaction.PlatformTransactionManager
import org.transaction_file.service.*


@Configuration
@EnableBatchProcessing
class OnlyValidTransactionsConfig(
    private val jobRepository: JobRepository,
    @Value("\${input.file}") private val inputFile: Resource,
    @Value("\${output.valid}") private val validOutputDirectory: String,
) {
    @Bean
    fun onlyValidTransactionReader(): ItemReader<Transaction> {
        return TransactionItemReader(inputFile)
    }

    @Bean
    fun onlyValidTransactionProcessor(): ItemProcessor<Transaction, Transaction?> {
        return TransactionItemProcessor()
    }

    @Bean
    fun onlyValidTransactionWriter(): ItemWriter<Transaction> {
        return TransactionItemWriter(validOutputDirectory)
    }

    @Bean
    protected fun onlyValidTransactionsStep(
        transactionManager: PlatformTransactionManager?,
        onlyValidTransactionReader: ItemReader<Transaction>?,
        onlyValidTransactionProcessor: ItemProcessor<Transaction, Transaction?>?,
        onlyValidTransactionWriter: ItemWriter<Transaction>?
    ): Step {
        return StepBuilder(
            "onlyValidTransactionsStep",
            jobRepository
        ).chunk<Transaction, Transaction>(
            10000,
            transactionManager!!
        )
        .reader(onlyValidTransactionReader!!)
        .processor(onlyValidTransactionProcessor!!)
        .writer(onlyValidTransactionWriter!!)
        .build()
    }

    @Bean
    fun onlyValidTransactionsJob(
        transactionManager: PlatformTransactionManager?
    ): Job {
        return JobBuilder("onlyValidTransactionsJob", jobRepository)
            .start(
                onlyValidTransactionsStep(
                    transactionManager,
                    onlyValidTransactionReader(),
                    onlyValidTransactionProcessor(),
                    onlyValidTransactionWriter()
                )
            ).build()
    }
}