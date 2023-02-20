package com.symphony.symphony.Retrofit

import com.symphony.symphony.Data.Ticketmodel
import retrofit2.Call
import retrofit2.http.GET

interface RetroServiceInterface {
    @GET("/tickets")
    fun getTicketList(): Call<List<Ticketmodel>>
}