package com.symphony.symphony.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.symphony.symphony.R
import com.symphony.symphony.TicketItemModel

class TickAdapter(private val activity: Activity) :
    RecyclerView.Adapter<TickAdapter.ViewHolder>() {


    private lateinit var ticketsList: List<TicketItemModel>

    fun setTickList(ticketsList: List<TicketItemModel>){
        this.ticketsList = ticketsList
    }
    private lateinit var tListener: onItemClickListener

    interface onItemClickListener {

        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        tListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false)
        return ViewHolder(view, tListener)


    }

    class ViewHolder(ItemView: View, listener: onItemClickListener) :
        RecyclerView.ViewHolder(ItemView) {

        val ticketValue: TextView = itemView.findViewById(R.id.txvTicketValue)
        val date: TextView = itemView.findViewById(R.id.txvDateValue)
        val ksh: TextView = itemView.findViewById(R.id.txvFaultValue)
        val customer: TextView = itemView.findViewById(R.id.txvCustomerValue)
//        val state: ImageView = itemView.findViewById(R.id.imgUrgencyColumn)
        val state2: ImageView = itemView.findViewById(R.id.imgUrgencyColumn2)
        val openedOn: TextView = itemView.findViewById(R.id.txvOpenedOnValue)
        val updatedOn: TextView = itemView.findViewById(R.id.txvUpdatedOnValue)
        val status: TextView = itemView.findViewById(R.id.txvStatusValue)
        val consLayout2: ConstraintLayout = itemView.findViewById(R.id.cnsConstraint2)
        val opened: TextView = itemView.findViewById(R.id.txvOpenedOn)
        val updated: TextView = itemView.findViewById(R.id.txvUpdatedOn)
        val stats: TextView = itemView.findViewById(R.id.txvStatus)
        val startTicket: View = itemView.findViewById(R.id.btnStartTicket)

        fun bind(data: TicketItemModel, activity: Activity){

            ticketValue.text = data.ticket
            date.text = data.date
            ksh.text = data.faultReported
            customer.text = data.customer
            openedOn.text = data.updatedOn
            updatedOn.text = data.updatedOn

        }

        fun collapseExpandedView() {
            openedOn.visibility = View.GONE
            updatedOn.visibility = View.GONE
            status.visibility = View.GONE
            opened.visibility = View.GONE
            updated.visibility = View.GONE
            stats.visibility = View.GONE
            startTicket.visibility = View.GONE
        }

        init {
            startTicket.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.ticketValue.text = ticketsList.get(position).ticket
//        holder.date.text = ticketsList.get(position).date
//        holder.customer.text = ticketsList.get(position).customer
//        holder.ksh.text = ticketsList.get(position).faultReported
//        holder.state.setImageResource(ticketsList.get(position).state)
//        holder.state2.setImageResource(ticketsList.get(position).state)
//        holder.openedOn.text = ticketsList.get(position).openedOn
//        holder.updatedOn.text = ticketsList.get(position).updatedOn
//        holder.status.text = ticketsList.get(position).status
        holder.bind(ticketsList.get(position), activity)

        val isExpandable: Boolean = ticketsList.get(position).isExpandable

        holder.openedOn.visibility = if (isExpandable) View.VISIBLE else View.GONE
        holder.updatedOn.visibility = if (isExpandable) View.VISIBLE else View.GONE
        holder.status.visibility = if (isExpandable) View.VISIBLE else View.GONE
        holder.opened.visibility = if (isExpandable) View.VISIBLE else View.GONE
        holder.updated.visibility = if (isExpandable) View.VISIBLE else View.GONE
        holder.stats.visibility = if (isExpandable) View.VISIBLE else View.GONE
        holder.startTicket.visibility = if (isExpandable) View.VISIBLE else View.GONE

        holder.consLayout2.setOnClickListener {
            isAnyItemExpanded(position)
            ticketsList.get(position).isExpandable = !ticketsList.get(position).isExpandable
            notifyItemChanged(position, Unit)
        }

    }

    private fun isAnyItemExpanded(position: Int) {
        val expanded = ticketsList.indexOfFirst {
            it.isExpandable
        }
        if (expanded >= 0 && expanded != position) {
            ticketsList[expanded].isExpandable = false
            notifyItemChanged(expanded, 0)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty() && payloads[0] == 0) {
            holder.collapseExpandedView()
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun getItemCount(): Int {
        return ticketsList.size
    }

}