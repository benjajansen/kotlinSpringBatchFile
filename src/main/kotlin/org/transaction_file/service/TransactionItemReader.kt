package org.transaction_file.service

import com.fasterxml.jackson.databind.MappingIterator
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.transaction_file.domain.model.Transaction
import org.springframework.batch.item.ExecutionContext
import org.springframework.batch.item.ItemStreamReader
import org.springframework.core.io.Resource
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class TransactionItemReader(
    private val inputFile: Resource
) : ItemStreamReader<Transaction>{
    companion object {
        private const val FILE_ENCODING = "ISO-8859-1"
    }
    private lateinit var reader: BufferedReader
    private val MAPPER = CsvMapper().registerModule(KotlinModule.Builder().build())
    private val csvMapper = MAPPER.readerFor(Transaction::class.java)
        .with(CsvSchema.emptySchema().withHeader())

    private lateinit var mappingIterator: MappingIterator<Transaction>

    override fun open(executionContext: ExecutionContext) {
        try {
            reader = BufferedReader(InputStreamReader(inputFile.inputStream, FILE_ENCODING))
            mappingIterator = csvMapper.readValues(reader)
        } catch (ex: IOException) {
            close()
            throw RuntimeException("Error in opening the resource", ex)
        }
    }

    override fun read(): Transaction? {
        return if (mappingIterator.hasNext()) {
            mappingIterator.next()
        } else {
            null
        }
    }

    override fun update(executionContext: ExecutionContext) {
        // This method can be used to update the context in case of restarts.
        // Not needed for this simple reader.
    }

    override fun close() {
        reader.close()
    }
}