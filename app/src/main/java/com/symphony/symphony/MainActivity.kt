package com.symphony.symphony

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    var url = "https://backend.api.symphony.co.ke/tickets"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        sendAndRequestResponse()
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