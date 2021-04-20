package com.wiser.scrollmultitermview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MainAdapter(private val list: MutableList<String>): RecyclerView.Adapter<MainAdapter.MainHolder>() {

    class MainHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv:TextView? = null
        init {
            tv = itemView.findViewById(R.id.tv_content)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder = MainHolder(LayoutInflater.from(parent.context).inflate(R.layout.item,parent,false))

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val text = list[position % list.size]
        holder.tv?.text = text
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

}