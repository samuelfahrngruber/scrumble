package com.spogss.scrumble.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.spogss.scrumble.R

class CustomSimpleAdapter<T>(private val data: MutableList<T>, private val context: Context, private val callback: (item: T) -> Unit): RecyclerView.Adapter<CustomSimpleAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return CustomSimpleAdapter.ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_list_simple, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.textView.text = item.toString()
        holder.textView.setOnClickListener { callback(item) }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var textView = v.findViewById<View>(R.id.list_item_text_view) as TextView
    }
}