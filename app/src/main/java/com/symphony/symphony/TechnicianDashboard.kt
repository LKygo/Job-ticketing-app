package com.symphony.symphony

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.symphony.symphony.databinding.ActivityTechnicianDashboardBinding
import java.util.Calendar


class TechnicianDashboard : AppCompatActivity() {
    private lateinit var binding: ActivityTechnicianDashboardBinding
    private lateinit var tickets: ArrayList<ItemsViewModel>
    private lateinit var tAdapter : TicketsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTechnicianDashboardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val recyclerView = binding.rcvRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        binding.txvHello.setText(greeting())

        tickets = ArrayList()
        tAdapter = TicketsAdapter(tickets)
        recyclerView.adapter = tAdapter

        getData()
    }




    private fun getData() {

        var url = "https://backend.api.symphony.co.ke/tickets"
        val queue = Volley.newRequestQueue(this)

        val request = JsonArrayRequest(com.android.volley.Request.Method.GET, url, null,
            { response ->

                try {

                    for (i in 0..response.length()) {
                        val ticket = response.getJSONObject(i)
                        val ticketNo = ticket.getString("ticketno")
                        val date = ticket.getString("ticketdate")
                        val faultReported = ticket.getString("faultreported")
                        val location = ticket.getString("location")

                        tickets.add(ItemsViewModel(ticketNo, date, faultReported, location))
                        tAdapter.notifyDataSetChanged()

                        Log.d("tickets", ticket.toString().trim())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, { error ->
                Toast.makeText(applicationContext, error.localizedMessage, Toast.LENGTH_SHORT)
                    .show()
                Log.d("tickets", error.toString().trim())

            })
        queue.add(request)

    }
}


//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val inflater: MenuInflater = menuInflater
//        inflater.inflate(R.menu.tech_menu, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.mnuHelp -> {
//                Toast.makeText(this, "${item.itemId} has been selected", Toast.LENGTH_SHORT).show()
//                true
//            }
//
//            R.id.mnuPrivacy -> {
//                Toast.makeText(this, "${item.itemId} has been selected", Toast.LENGTH_SHORT).show()
//                true
//            }
//
//            R.id.mnuSignOut -> {
//                Toast.makeText(this, "${item.itemId} has been selected", Toast.LENGTH_SHORT).show()
//                true
//            }
//
//            else -> {super.onOptionsItemSelected(item)}
//        }
//    }
//


fun greeting(): String {
    val calendar = Calendar.getInstance()
    val timeOfDay = calendar.get(Calendar.HOUR_OF_DAY)
    var greeting: String = "hello"


    return when (timeOfDay) {
        in 0..11 -> "Good Morning"
        in 12..15 -> "Good Afternoon"
        in 16..20 -> "Good Evening"
        in 21..23 -> "Good Night"
        else -> "Hello"
    }
}



