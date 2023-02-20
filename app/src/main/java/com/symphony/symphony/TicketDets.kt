package com.symphony.symphony

data class TicketDets(
    val id: String,
    val ticket: String,
    val date: String,
    val faultReported: String,
    val customer: String,
    val state: Int,
    val openedOn: String,
    val updatedOn: String,
    val status: String
)
