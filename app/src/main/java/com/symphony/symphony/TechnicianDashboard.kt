package com.symphony.symphony

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.symphony.symphony.databinding.ActivityTechnicianDashboardBinding
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Calendar


class TechnicianDashboard : AppCompatActivity() {
    private lateinit var binding: ActivityTechnicianDashboardBinding
    private lateinit var tickets: ArrayList<ItemsViewModel>
    private lateinit var tAdapter : TicketsAdapter
    private lateinit var pBar: ProgressBar
//    private lateinit var stateImage : CircleImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTechnicianDashboardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

//        stateImage = findViewById(R.id.imgCircularStatus)
        val recyclerView = binding.rcvRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        binding.txvHello.setText(greeting())
        pBar= binding.progressBar

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

                pBar.visibility= View.GONE
                try {

                    for (i in 0..response.length()) {
                        val ticket = response.getJSONObject(i)
                        val ticketNo = ticket.getString("ticketno")
                        val date = ticket.getString("ticketdate")
                        val faultReported = ticket.getString("faultreported")
                        val location = ticket.getString("location")
                        val urgency = ticket.getString("urgency")
                        val state = urgencyControl(urgency)

                        tickets.add(ItemsViewModel(ticketNo, date, faultReported, location, state))
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

private fun urgencyControl(urgency:String) :Int {


    return when(urgency){
        "Moderately Urgent"-> R.drawable.yellow_circle

        "Urgent"-> R.drawable.orange_circle

        "Very Urgent"-> R.drawable.red_circle

        else -> R.drawable.green_circle
    }
}

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



