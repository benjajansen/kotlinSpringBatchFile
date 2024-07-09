package org.transaction_file.utils

import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jeasy.random.FieldPredicates.*
import org.transaction_file.domain.model.Transaction
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random

class EasyRandomFactory {
    companion object {
        fun newTransactionInstance(): EasyRandom {
            val idPredicate = named("id")
                .and(ofType(Long::class.java))
                .and(inClass(Transaction::class.java))

            val referencePredicate = named("reference")
                .and(ofType(String::class.java))
                .and(inClass(Transaction::class.java))

            val accountNumberPredicate = named("accountNumber")
                .and(ofType(String::class.java))
                .and(inClass(Transaction::class.java))

            val startBalancePredicate = named("startBalance")
                .and(ofType(BigDecimal::class.java))
                .and(inClass(Transaction::class.java))

            val mutationPredicate = named("mutation")
                .and(ofType(BigDecimal::class.java))
                .and(inClass(Transaction::class.java))

            val endBalancePredicate = named("endBalance")
                .and(ofType(BigDecimal::class.java))
                .and(inClass(Transaction::class.java))


            val parameters = EasyRandomParameters()
                .excludeField(idPredicate)
                .randomize(referencePredicate) {
                    Random.nextInt(100000, 999999).toString()
                }
                .randomize(accountNumberPredicate) {
                    val accountNum = Random.nextInt(1, 10_0000_0000)
                    "NL93ABNA${String.format("%08d", accountNum)}"
                }
                .randomize(startBalancePredicate) {
                    Random.nextDouble(10.00, 100.00).toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)
                }
                .randomize(mutationPredicate) {
                    Random.nextDouble(10.00, 100.00).toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)
                }
                .excludeField(endBalancePredicate)

            return EasyRandom(parameters)
        }
    }
}