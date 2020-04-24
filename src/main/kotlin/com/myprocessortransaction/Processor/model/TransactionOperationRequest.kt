package com.myprocessortransaction.Processor.model

data class TransactionOperationRequest(
        val nome : String = "",
        val cartao_credito: String = "",
        val data_compra: String = "",
        val valor_compra : Double = 0.0,
        val parcelamento : Int = 0,
        val bandeira : String = "",
        val operacao : String
)