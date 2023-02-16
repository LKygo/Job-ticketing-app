package com.symphony.symphony

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.symphony.symphony.databinding.ActivityTechnicianDashboardBinding
import java.util.Calendar


class TechnicianDashboard : AppCompatActivity() {
    private lateinit var binding: ActivityTechnicianDashboardBinding
    private lateinit var tickets: ArrayList<ItemsViewModel>
    private lateinit var tAdapter : TicketsAdapter
    private lateinit var pBar: ProgressBar

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




    tAdapter.setOnItemClickListener(object: TicketsAdapter.onItemClickListener{
        override fun onItemClick(position: Int) {
            val intent = Intent(this@TechnicianDashboard, TicketActivity::class.java)
            intent.putExtra("ticketNo", tickets[position].ticket)
            intent.putExtra("customer", tickets[position].customer)
            intent.putExtra("faultReported", tickets[position].faultReported)
            intent.putExtra("date", tickets[position].date)
            startActivity(intent)



        }

    })
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
                        val id = ticket.getString("id")
                        val ticketNo = ticket.getString("ticketno")
                        val date = ticket.getString("ticketdate")
                        val faultReported = ticket.getString("faultreported")
                        val location = ticket.getString("location")
                        val urgency = ticket.getString("urgency")
                        val state = urgencyControl(urgency)
                        val createdAt = ticket.getString("created_at")
                        val openedOn = ticket.getString("updated_at")
                        val status = ticket.getString("status")

                        tickets.add(ItemsViewModel(id, ticketNo, date, faultReported, location, state, createdAt,openedOn,status))
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

    fun search(view: View) {}
}

private fun urgencyControl(urgency:String) :Int {


    return when(urgency){
        "Moderately Urgent"-> R.drawable.urgency_yellow

        "Urgent"-> R.drawable.urgency_orange

        "Very Urgent"-> R.drawable.urgency_red

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



