package com.symphony.symphony

data class ItemsViewModel(
    val ticket: String,
    val date: String,
    val faultReported: String,
    val customer: String,
    val state: Int,
    val openedOn: String,
    val updatedOn: String,
    val status:String,
    var isExpandable: Boolean = false


) {
}