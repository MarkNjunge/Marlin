package com.marknjunge.marlin.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.marknjunge.marlin.R
import com.marknjunge.marlin.data.model.Droplet
import kotlinx.android.synthetic.main.item_droplet_preview.view.*
import java.util.*


class DropletsPreviewAdapter(private val onClick: (Droplet) -> Unit) : RecyclerView.Adapter<DropletsPreviewAdapter.ViewHolder>() {
    private var data: List<Droplet> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_droplet_preview, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position], onClick)
    }

    fun setItems(data: List<Droplet>) {
        this.data = data
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(droplet: Droplet, onClick: (Droplet) -> Unit) {
            itemView.run {
                tvDropletName.text = droplet.name
                if (droplet.status == "off") {
                    imgDropletStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.droplet_off))
                }
                rootLayout.setOnClickListener { onClick(droplet) }
            }
        }
    }
}