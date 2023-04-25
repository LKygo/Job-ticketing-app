package com.symphony.symphony

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.AuthFailureError
import com.android.volley.NetworkError
import com.android.volley.NoConnectionError
import com.android.volley.ParseError
import com.android.volley.Response
import com.android.volley.ServerError
import com.android.volley.TimeoutError
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.michaelflisar.changelog.ChangelogBuilder
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
    private lateinit var btnreset: Button


    companion object {
        private const val IMAGE_PICK_CODE = 999
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityTechnicianDashboardBinding.inflate(layoutInflater)
            val view = binding.root
            setContentView(view)
        } catch (e: Exception) {
            Log.d("inflate", e.toString())
        }
        val packageName = packageName // get the package name of your app
        val packageInfo =
            packageManager.getPackageInfo(packageName, 0) // get package info for your app
        val versionName = packageInfo.versionName // get the version name of your app



        ChangelogBuilder()
            .withUseBulletList(true)
            .withMinVersionToShow(113)
            .withManagedShowOnStart(true)
            .withSummary(true, true)
            .withTitle("What's new in v${versionName}?")
            .buildAndShowDialog(this, false)

        // Load the profile picture from internal storage
        val file = getFileStreamPath("profile_image.jpg")
        if (file.exists()) {
            val uri = Uri.fromFile(file)
            binding.imgProfile.setImageURI(uri)
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
        val userName = sharedPref.getString("userName", null)

        binding.txvName.text = userName.toString()
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
                    greeting()
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


        binding.imgProfile.setOnClickListener {
            requestGalleryPermission()
        }

        binding.menuSort.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            popupMenu.inflate(R.menu.sort_menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.mnuNewestFirst -> {
                        // Handle the Settings menu item click
                        tickets.sortByDescending { it.openedOn }

                        tAdapter?.notifyDataSetChanged()

                        Toast.makeText(this, "Sort according to newest ticket", Toast.LENGTH_SHORT)
                            .show()
                        true
                    }

                    R.id.mnuOldestFirst -> {
                        // Handle the Help menu item click
                        tickets.sortBy { it.openedOn }

                        tAdapter?.notifyDataSetChanged()
                        Toast.makeText(this, "Sort according to oldest ticket", Toast.LENGTH_SHORT)
                            .show()
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
        }

        binding.menuButton.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            popupMenu.inflate(R.menu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_changelog -> {
                        // Handle the Settings menu item click
                        ChangelogBuilder()
                            .withUseBulletList(true)
                            .withManagedShowOnStart(false)
                            .withMinVersionToShow(113)
                            .withSummary(true, true)
                            .withTitle("What's new?")
                            .buildAndShowDialog(this, false)

                        true
                    }

                    R.id.action_resetpass -> {
                        // Handle the Help menu item click
                        showResetPassDialog()
                        true
                    }

                    R.id.action_logout -> {
                        // Handle the Help menu item click
                        showSignOutDialog()
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
        }

    }


    private fun requestGalleryPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                IMAGE_PICK_CODE
            )
        } else {
            openGallery()
        }
    }

    private fun showSignOutDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmation")
        builder.setMessage("Are you sure you want to sign out?")
        builder.setPositiveButton("Sign out") { dialog, which ->
            // User clicked Yes button
            logout()
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            // User clicked No button
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showResetPassDialog() {
        val dialogView = layoutInflater.inflate(R.layout.reset_password, null)

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
        alertDialog.show()

        alertDialog.setOnShowListener {
            val btnreset = dialogView.findViewById<Button>(R.id.btnResetP)
            btnreset?.setOnClickListener {
                val oldPass = alertDialog.findViewById<EditText>(R.id.oldPassReset)?.text.toString()
                val newPass = alertDialog.findViewById<EditText>(R.id.newPassReset)?.text.toString()
                val confirmPass =
                    alertDialog.findViewById<EditText>(R.id.confirmPassReset)?.text.toString()


                val sharedPref = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
                val id = sharedPref.getString("userId", null)

                if (id != null) {
                    if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    } else {

                        if (newPass !== oldPass) {
                            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
                        } else {
                            resetPass(id, oldPass, newPass)
                        }
                    }
                }
            }
        }


    }

    private fun resetPass(id: String, oldPass: String, newPass: String) {

        val btnResetPass = findViewById<Button>(R.id.btnResetP)
        val progressReset = findViewById<ProgressBar>(R.id.progressBReset)

//        Set disable views and show progress
        btnResetPass.isClickable = false
        btnResetPass.text = ""
        progressReset.visibility = View.VISIBLE


        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)

        // Set up the resetPass endpoint URL and request parameters.
        val url = "https://backend.api.symphony.co.ke/login"
        val params = HashMap<String, String>()
        params["id"] = id
        params["oldPass"] = oldPass
        params["newPass"] = newPass


        // Create a new POST request with the login endpoint URL and parameters.
        val stringRequest =
            object : StringRequest(Method.POST, url, Response.Listener<String> { response ->

                progressReset.visibility = View.GONE // hide the progress bar
                btnResetPass.text = "reset"
                btnResetPass.isClickable = true // enable the sign in button


                // Handle the API response on success.
                Toast.makeText(applicationContext, "Password successfully reset", Toast.LENGTH_LONG)
                    .show()
                Log.d("reset", response.toString())
            }, Response.ErrorListener { error ->


                progressReset.visibility = View.GONE // hide the progress bar
                btnResetPass.text = "reset"
                btnResetPass.isClickable = true // enable the sign in button

                // Handle the API response on error.
                val errorMessage = when (error) {
                    is NoConnectionError -> "No internet connection"
                    is TimeoutError -> "Connection timed out"
                    is ServerError -> "No user under this ID"
                    is AuthFailureError -> "Authentication failure"
                    is NetworkError -> "Network error"
                    is ParseError -> "Parse error"
                    else -> "Unknown error"
                }
                Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_LONG).show()
                Log.d("reset", error.toString())

            }) {

                // Set the POST request parameters.
                override fun getParams(): Map<String, String> {
                    return params
                }
            }

        // Add the request to the RequestQueue.
        queue.add(stringRequest)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == IMAGE_PICK_CODE && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            openGallery()
        } else {
            // Permission denied, handle the case here
            Toast.makeText(
                this,
                "Gallery permission required to take pictures",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun openGallery() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)

        } else {
            // Permission not granted, handle the case here
            Toast.makeText(
                this,
                "Gallery permission required to upload profile picture",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val uri = data?.data
            if (uri != null) {
                // Save the image to internal storage
                val inputStream = contentResolver.openInputStream(uri)
                val outputStream = openFileOutput("profile_image.jpg", Context.MODE_PRIVATE)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()

                // Set the profile picture
                binding.imgProfile.setImageURI(uri)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
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
                        val unformattedOpenedOn = ticket.getString("updated_at")
                        val openedOn = formatDateString(unformattedOpenedOn)
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

    fun logout() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()

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

            "Meeting" -> R.drawable.urgency_meeting


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



