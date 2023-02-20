package com.symphony.symphony

import org.json.JSONArray
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET

interface RetrofitTickets {
    @GET("/tickets")
    fun getTickets(@Body body: JSONArray): Call<ArrayList<TicketDets>>
}