package com.symphony.symphony

import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class CustomAdapter(private val cList: List<ItemsViewModel>):
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {


    class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView) {
        val ticketValue : TextView = itemView.findViewById(R.id.txvTicketValue)
        val date: TextView = itemView.findViewById( R.id.txvDateValue)
        val ksh: TextView = itemView.findViewById( R.id.txvKshValue)
        val customer: TextView = itemView.findViewById( R.id.txvCustomerValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomAdapter.ViewHolder, position: Int) {
        val ItemsViewModel = cList[position]
        holder.ticketValue.text = ItemsViewModel.ticket
        holder.date.text = ItemsViewModel.date.toString()
        holder.ksh.text = ItemsViewModel.ksh.toString()
        holder.customer.text = ItemsViewModel.customer

    }

    override fun getItemCount(): Int {
      return cList.size
    }
}