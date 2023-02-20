package com.symphony.symphony.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.symphony.symphony.Data.Ticketmodel
import com.symphony.symphony.Retrofit.RetroInstance
import com.symphony.symphony.Retrofit.RetroServiceInterface
import retrofit2.Call
import retrofit2.Response

class TechnicianDashViewModel : ViewModel() {

    lateinit var liveDataList: MutableLiveData<List<Ticketmodel>>

    init {
        liveDataList = MutableLiveData<List<Ticketmodel>>()
    }

    fun getLivedataObserver(): MutableLiveData<List<Ticketmodel>> {
        return liveDataList
    }

    fun makeApiCall() {
        val retroInstance = RetroInstance.getRetroInstance()
        val retroService = retroInstance.create(RetroServiceInterface::class.java)
        val call = retroService.getTicketList()
        call.enqueue(object : retrofit2.Callback<List<Ticketmodel>> {
            override fun onResponse(
                call: Call<List<Ticketmodel>>,
                response: Response<List<Ticketmodel>>
            ) {
                liveDataList.postValue(response.body())
            }

            override fun onFailure(call: Call<List<Ticketmodel>>, t: Throwable) {
                liveDataList.postValue(null)
            }

        })
    }
}