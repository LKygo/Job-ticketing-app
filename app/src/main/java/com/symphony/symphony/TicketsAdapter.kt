package com.symphony.symphony

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TicketsAdapter(private var ticketsList: ArrayList<ItemsViewModel>) :
    RecyclerView.Adapter<TicketsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val ticketValue: TextView = itemView.findViewById(R.id.txvTicketValue)
        val date: TextView = itemView.findViewById(R.id.txvDateValue)
        val ksh: TextView = itemView.findViewById(R.id.txvFaultValue)
        val customer: TextView = itemView.findViewById(R.id.txvCustomerValue)
    }


    override fun onBindViewHolder(holder: TicketsAdapter.ViewHolder, position: Int) {
        holder.ticketValue.text = ticketsList.get(position).ticket
        holder.date.text = ticketsList.get(position).date
        holder.customer.text = ticketsList.get(position).customer
        holder.ksh.text = ticketsList.get(position).faultReported


    }

    override fun getItemCount(): Int {
        return ticketsList.size
    }
}