package com.myprocessortransaction.Processor.model

data class Payments (
        val data_pagamento : String,
        val valor : Double,
        val parcela : Int
)