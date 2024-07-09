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
import org.transaction_file.domain.model.ValidatedTransaction
import org.transaction_file.service.*


@Configuration
@EnableBatchProcessing
class ValidateTransactionsConfig(
    private val jobRepository: JobRepository,
    @Value("\${input.file}") private val inputFile: Resource,
    @Value("\${output.all}") private val validatedOutputDirectory: String,
) {
    @Bean
    fun validateTransactionReader(): ItemReader<Transaction> {
        return TransactionItemReader(inputFile)
    }

    @Bean
    fun validateTransactionProcessor(): ItemProcessor<Transaction, ValidatedTransaction> {
        return ValidatedTransactionItemProcessor()
    }

    @Bean
    fun validateTransactionWriter(): ItemWriter<ValidatedTransaction> {
        return ValidatedTransactionItemWriter(validatedOutputDirectory)
    }

//    @Bean
//    fun itemWriter(): ClassifierCompositeItemWriter<ValidatedTransaction> {
//        val classifier = Classifier<ValidatedTransaction, ItemWriter<in ValidatedTransaction>>
//        { validatedTransaction ->
//            if (validatedTransaction.validationResult == ValidationResult.VALID)
//                ValidTransactionItemWriter(validOutputDirectory)
//            else
//                InvalidTransactionItemWriter(invalidOutputDirectory)
//        }
//        val writer = ClassifierCompositeItemWriter<ValidatedTransaction>()
//        writer.setClassifier(classifier)
//        return writer
//
//        //return TransactionItemWriter(outputDirectory)
//    }

    @Bean
    protected fun validateTransactionsStep(
        transactionManager: PlatformTransactionManager?,
        validateTransactionReader: ItemReader<Transaction>?,
        validateTransactionProcessor: ItemProcessor<Transaction, ValidatedTransaction>?,
        validateTransactionWriter: ItemWriter<ValidatedTransaction>?
    ): Step {
        return StepBuilder(
            "validateTransactionsStep",
            jobRepository
        ).chunk<Transaction, ValidatedTransaction>(
            10000,
            transactionManager!!
        )
        .reader(validateTransactionReader!!)
        .processor(validateTransactionProcessor!!)
        .writer(validateTransactionWriter!!)
        .build()
    }

    @Bean
    fun validateTransactionsJob(
        transactionManager: PlatformTransactionManager?
    ): Job {
        return JobBuilder("validateTransactionsJob", jobRepository)
            .start(
                validateTransactionsStep(
                    transactionManager,
                    validateTransactionReader(),
                    validateTransactionProcessor(),
                    validateTransactionWriter()
                )
            ).build()
    }
}