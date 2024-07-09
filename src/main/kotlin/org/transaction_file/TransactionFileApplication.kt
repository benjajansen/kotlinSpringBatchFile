package org.transaction_file

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TransactionFileApplication(
    private val jobLauncher: JobLauncher,
    @Qualifier("onlyValidTransactionsJob") private val jobToRun: Job
) : CommandLineRunner {
    override fun run(vararg args: String) {
        val params = JobParametersBuilder()
            .addString("jobId", System.currentTimeMillis().toString())
            .toJobParameters()
        jobLauncher.run(jobToRun, params)
    }
}

fun main(args: Array<String>) {
    runApplication<TransactionFileApplication>(*args)
}
