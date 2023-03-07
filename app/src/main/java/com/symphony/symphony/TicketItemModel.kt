package com.symphony.symphony

data class TicketItemModel(
    val id: String,
    val ticket: String,
    val date: String,
    val faultReported: String,
    val customer: String,
    val state: Int,
    val openedOn: String,
    val updatedOn: String,
    val status: String,
    val location: String,
    var isExpandable: Boolean = false


)