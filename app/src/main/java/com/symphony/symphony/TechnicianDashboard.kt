package com.symphony.symphony

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.NetworkError
import com.android.volley.ServerError
import com.android.volley.TimeoutError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.symphony.symphony.databinding.ActivityTechnicianDashboardBinding
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


class TechnicianDashboard : AppCompatActivity() {
    private lateinit var binding: ActivityTechnicianDashboardBinding
    private lateinit var tickets: ArrayList<TicketItemModel>
    private var tAdapter: TicketsAdapter? = null
    private lateinit var pBar: ProgressBar
    private lateinit var userID: String
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTechnicianDashboardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

//        stateImage = findViewById(R.id.imgCircularStatus)
        val recyclerView = binding.rcvRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        searchView = binding.searchView

        val bundle: Bundle? = intent.extras
        /* userID = bundle?.getString("id").toString() */
        userID = "17"


        binding.txvHello.setText(greeting())
        pBar = binding.progressBar

//        Setting tickets list as Arraylist and mapping it as input to our adapter
        tickets = ArrayList()
        tAdapter = TicketsAdapter(tickets)

        tAdapter!!.setOnItemClickListener(object : TicketsAdapter.onItemClickListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemClick(position: Int) {
                val intent = Intent(this@TechnicianDashboard, TicketActivity::class.java)
                intent.putExtra("ticketNo", tickets[position].ticket)
                intent.putExtra("customer", tickets[position].customer)
                intent.putExtra("faultReported", tickets[position].faultReported)
                intent.putExtra("location", tickets[position].location)
                intent.putExtra("date", tickets[position].openedOn)
                intent.putExtra("userID", userID)

                val currentTime = LocalTime.now()
                val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                val startTime = currentTime.format(formatter)

                intent.putExtra("startTime", startTime)
                startActivity(intent)
            }

        })
        recyclerView.adapter = tAdapter

        getData("2")

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

        })
    }


    private fun getData(id: String) {

        val url = "https://backend.api.symphony.co.ke/tickets?user_id=$id"
        val queue = Volley.newRequestQueue(this)

        val request = JsonArrayRequest(com.android.volley.Request.Method.GET, url, null,
            { response ->

                pBar.visibility = View.GONE
                try {

                    for (i in 0..response.length()) {
                        val ticket = response.getJSONObject(i)
                        val id = ticket.getString("id")
                        val ticketNo = ticket.getString("ticketno")
                        val stringDate = ticket.getString("ticketdate")
                        val date = formatDateString(stringDate)
                        val faultReported = ticket.getString("faultreported")
                        val customer = ticket.getString("clientname")
                        val location = ticket.getString("location")
                        val urgency = ticket.getString("urgency")
                        val state = urgencyControl(urgency)
                        val createdAt = ticket.getString("created_at")
                        val openedOn = ticket.getString("updated_at")
                        val status = ticket.getString("status")

                        tickets.add(
                            TicketItemModel(
                                id,
                                ticketNo,
                                date,
                                faultReported,
                                customer,
                                state,
                                createdAt,
                                openedOn,
                                status,
                                location
                            )
                        )
                        tAdapter?.notifyDataSetChanged()

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, { error ->
//                Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_SHORT).show()
                if (error is NetworkError || error is AuthFailureError || error is TimeoutError) {
                    // Handle network or authentication errors here

                } else if (error is ServerError) {
                    // Handle server error here
                    Toast.makeText(applicationContext, "It appears there's a server error.Pplease contact admin", Toast.LENGTH_SHORT).show()

                    val statusCode = error.networkResponse?.statusCode
                    if (statusCode == 404) {
                        // Handle 404 error here
                        Toast.makeText(applicationContext, "It appears there are no open tickets. Check back later for new ones", Toast.LENGTH_SHORT).show()
                        pBar.visibility = View.GONE

                    }
                }
            })
        queue.add(request)

    }

    private fun filterList(query: String?) {
        if (query != null) {
            val filteredList = ArrayList<TicketItemModel>()
            for (i in tickets) {
                if (i.customer.lowercase(Locale.ROOT).contains(query)) {
                    filteredList.add(i)
                }
            }
            if (filteredList.isEmpty()) {
                Toast.makeText(
                    this,
                    "Seems like there are no jobs under this company",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                tAdapter?.setFilteredList(filteredList)
            }
        }
    }
}

    private fun urgencyControl(urgency: String): Int {


    return when (urgency) {
        "Moderately Urgent" -> R.drawable.urgency_orange

        "Urgent" -> R.drawable.urgency_yellow

        "Very Urgent" -> R.drawable.urgency_red

        else -> R.drawable.urgency_red
    }
}


    fun formatDateString(dateString: String): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val date = formatter.parse(dateString)
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val formattedDate = date?.let { dateFormatter.format(it) }
    val formattedTime = date?.let { timeFormatter.format(it) }
    return "$formattedDate, $formattedTime"
}


    fun greeting(): String {
    val calendar = Calendar.getInstance()
    val timeOfDay = calendar.get(Calendar.HOUR_OF_DAY)


    return when (timeOfDay) {
        in 0..11 -> "Good Morning"
        in 12..15 -> "Good Afternoon"
        in 16..23 -> "Good Evening"
        else -> "Hello"
    }
}



