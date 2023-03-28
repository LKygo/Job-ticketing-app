package com.symphony.symphony

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.AuthFailureError
import com.android.volley.NetworkError
import com.android.volley.ServerError
import com.android.volley.TimeoutError
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.symphony.symphony.databinding.ActivityTechnicianDashboardBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class TechnicianDashboard : AppCompatActivity() {
    private lateinit var binding: ActivityTechnicianDashboardBinding
    private lateinit var tickets: ArrayList<TicketItemModel>
    private var tAdapter: TicketsAdapter? = null
    private lateinit var pBar: ProgressBar
    private lateinit var userID: String
    private lateinit var searchView: SearchView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var errorLayout: View
    private lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityTechnicianDashboardBinding.inflate(layoutInflater)
            val view = binding.root
            setContentView(view)
        } catch (e: Exception) {
            Log.d("inflate", e.toString())
        }
        recyclerView = binding.rcvRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        searchView = binding.searchView
        swipeRefresh = binding.swipeRefresh

        errorLayout = binding.parentLayout

        binding.txvHello.text = greeting()
        pBar = binding.progressBar

//        Setting tickets list as Arraylist and mapping it as input to our adapter
        tickets = ArrayList()
        tAdapter = TicketsAdapter(tickets)
        recyclerView.adapter = tAdapter

        val sharedPref = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val userID = sharedPref.getString("userID", null)

        val bundle: Bundle? = intent.extras




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

                val currentTime = Date()
                val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val startTime = formatter.format(currentTime)

                intent.putExtra("startTime", startTime)
                startActivity(intent)
            }

        })
        recyclerView.adapter = tAdapter

        if (userID != null) {
            getData(userID)
        } else {
            Toast.makeText(
                this, "No userId found. Please logout and log in again", Toast.LENGTH_SHORT
            ).show()
        }

        swipeRefresh.setOnRefreshListener {

            Handler().postDelayed({
                if (userID != null) {
                    getData(userID)
                } else {
                    Toast.makeText(
                        this, "No userId found. Please logout and log in again", Toast.LENGTH_SHORT
                    ).show()
                }
                swipeRefresh.isRefreshing = false
            }, 1000L)
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(newText: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    filterList(newText)
                } else {
                    resetAdapter()
                }
                return true

            }
        })

        binding.imgLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }


    private fun getData(id: String) {

        val url = "https://backend.api.symphony.co.ke/tickets?user_id=$id"
        val queue = Volley.newRequestQueue(this)

        val request =
            JsonArrayRequest(com.android.volley.Request.Method.GET, url, null, { response ->

                pBar.visibility = View.GONE

                val parentView = findViewById<ViewGroup>(R.id.parent_layout)
                parentView.removeAllViews()
                parentView.visibility = View.GONE

                try {
                    tickets.clear()
                    for (i in 0 until response.length()) {
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
                    }
                    tAdapter?.notifyDataSetChanged()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, { error ->

                if (error is VolleyError) {
                    if (tickets.isEmpty()) {
                        val errorLayoutResId = getErrorLayout(error)
                        errorLayout = layoutInflater.inflate(errorLayoutResId, null)
                        val parentView = findViewById<ViewGroup>(R.id.parent_layout)
                        parentView.addView(errorLayout)
                        parentView.visibility = View.VISIBLE
                        pBar.visibility = View.GONE
                    } else {
                        Toast.makeText(
                            this,
                            "Unable to refresh tickets. Please try again later",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            })
        queue.add(request)

    }

    fun getErrorLayout(error: VolleyError): Int {


        return when (error) {

            is NetworkError, is AuthFailureError, is TimeoutError -> R.layout.network_error
            is ServerError -> {
                when (error.networkResponse?.statusCode) {
                    404 -> R.layout.error_404
                    500 -> R.layout.error_500
                    else -> R.layout.default_error
                }
            }

            else -> R.layout.default_error
        }
    }

    private fun resetAdapter() {
        tAdapter?.setFilteredList(tickets)
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
                    this, "Seems like there are no jobs under this company", Toast.LENGTH_LONG
                ).show()
            } else {
                tAdapter?.setFilteredList(filteredList)
            }
        } else {
            resetAdapter()
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
}


