package com.myprocessortransaction.Processor.model

data class TransactionResponse (
        val transacao_success : Boolean,
        val cartao_credito_mascarado : String,
        val total_parcelas : Int,
        val pagamentos : List<Payments>,
        val valor_compra : Double,
        val valor_pagamento : Double,
        val percentual_repasse : String
)