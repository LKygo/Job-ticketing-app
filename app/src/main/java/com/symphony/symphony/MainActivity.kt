package com.symphony.symphony

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    var url = "https://backend.api.symphony.co.ke/tickets"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

//    private fun sendAndRequestResponse() {
////       Initialize request queue
//        val volley = Volley.newRequestQueue(this)
//
//        val stringRequest = StringRequest(
//            Request.Method.GET, url,
//            { response ->
//
//                for (i in 0..response.length){
//
//                    Log.d("API", response.toString())
//
//                }
//            }
//        ) { error -> Log.i("API", "Error :$error") }
//
//        volley.add(stringRequest)
//
//
//    }
}