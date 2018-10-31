package com.marknjunge.marlin.ui.droplets

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.marknjunge.marlin.R
import com.marknjunge.marlin.data.model.Droplet
import kotlinx.android.synthetic.main.item_droplet.view.*
import java.util.*

class DropletsAdapter(private val onClick: (Droplet) -> Unit) : RecyclerView.Adapter<DropletsAdapter.ViewHolder>() {
    private var data: List<Droplet> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_droplet, parent, false)
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
        @SuppressLint("SetTextI18n")
        fun bind(droplet: Droplet, onClick: (Droplet) -> Unit) {
            itemView.run {
                tvName.text = droplet.name
                tvIpAddress.text = droplet.networks.v4[0].ipAddress
                tvvCPUs.text = "${droplet.vcpus} vCPUs"
                tvDisk.text = "${droplet.disk} GB"
                tvTags.text = droplet.tags.toString().replace("[", "").replace("]", "")

                rootLayout.setOnClickListener {
                    onClick(droplet)
                }
            }
        }
    }
}