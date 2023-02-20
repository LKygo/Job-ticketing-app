package com.symphony.symphony.Data

import java.text.DateFormat
import java.util.Date

data class Ticketmodel(
    val id: Int,
    val ticket: Char,
    val date: DateFormat,
    val faultReported: String,
    val customer: String,
    val state: Int,
    val openedOn: Date,
    val updatedOn: Date,
    val status: String,
    var isExpandable: Boolean = false

)
