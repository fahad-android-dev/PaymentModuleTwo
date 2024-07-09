package com.orbits.paymentmoduletwo.mvvm.main.model

data class ClientDataModel(
    val desc : String ?= null,
    val amount : String ?= null,
    val client_id : String ?= null,
    val transaction_id : String ?= null,
    val time : String ?= null,
    val code : String ?= null,
    val currency : String ?= null,
    val transaction_type : String ?= null,
)
