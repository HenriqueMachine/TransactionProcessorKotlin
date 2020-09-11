package com.myprocessortransaction.processor.controllers

import com.myprocessortransaction.processor.model.Payments
import com.myprocessortransaction.processor.model.SimpleError
import com.myprocessortransaction.processor.model.TransactionOperationRequest
import com.myprocessortransaction.processor.model.TransactionResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/transaction")
class TransactionOperationController {

    companion object {
        const val MAX_INSTALLMENTS = 12
        const val PERCENT_MASTERCARD = 0.05
        const val PERCENT_VISA = 0.03
        const val PERCENT_AMEX = 0.049
    }

    @PostMapping
    fun validateTransaction(@RequestBody transaction: TransactionOperationRequest): ResponseEntity<Any> {
        with(transaction) {
            if (parcelamento > MAX_INSTALLMENTS) {
                return ResponseEntity.badRequest().body(SimpleError(
                        "Ops, algo deu errado :(",
                        "O limite de parcelamento dessa compra é de 12x")
                )
            }
            if (data_compra < LocalDate.now().toString()) {
                return ResponseEntity.badRequest().body(SimpleError(
                        "Ops, algo deu errado :(",
                        "A data de compra é inferior a data atual")
                )
            }
        }
        return ResponseEntity.ok(buildJsonSuccess(transaction))
    }

    private fun buildJsonSuccess(transaction: TransactionOperationRequest) = TransactionResponse(
            true,
            cardMask(transaction.cartao_credito),
            transaction.parcelamento,
            generateListPayments(
                    transaction.data_compra,
                    transaction.parcelamento,
                    getValuePaymentByFlag(
                            transaction.bandeira,
                            transaction.valor_compra
                    )
            ),
            transaction.valor_compra,
            getValuePaymentByFlag(
                    transaction.bandeira,
                    transaction.valor_compra
            ),
            getFlagPercent(transaction.bandeira
            )

    )

    private fun cardMask(numberCard: String) = numberCard.subSequence(numberCard.length - 4, numberCard.length).toString()

    private fun generateListPayments(date: String, installments: Int, purchase: Double): List<Payments> {
        val day: Int = date.split("-")[2].toInt()
        var month: Int = date.split("-")[1].toInt()
        var year: Int = date.split("-")[0].toInt()
        val valueInstallment = purchase / installments
        val list = mutableListOf<Payments>()

        for (x in 1..installments) {
            month += 1
            if (month == MAX_INSTALLMENTS) {
                month = 0
                year += 1
            }
            list.add(
                    Payments(
                            "$year-$month-$day",
                            valueInstallment,
                            x
                    )
            )
        }
        return list
    }

    private fun getFlagPercent(flag: String): String {
        return when (FlagType.valueOf(flag)) {
            FlagType.MASTERCARD -> "5%"
            FlagType.VISA -> "3%"
            FlagType.AMEX -> "4.9%"
        }
    }

    private fun getValuePaymentByFlag(flag: String, purchaseValue: Double): Double {
        return when (FlagType.valueOf(flag)) {
            FlagType.MASTERCARD -> {
                purchaseValue - (purchaseValue * PERCENT_MASTERCARD)
            }
            FlagType.VISA -> {
                purchaseValue - (purchaseValue * PERCENT_VISA)
            }
            FlagType.AMEX -> {
                purchaseValue - (purchaseValue * PERCENT_AMEX)
            }
        }
    }

    enum class FlagType {
        MASTERCARD, VISA, AMEX
    }

}