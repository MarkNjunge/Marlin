package com.marknjunge.marlin.ui.droplets

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.marknjunge.marlin.R
import com.marknjunge.marlin.data.model.Droplet
import com.marknjunge.marlin.utils.updatePadding
import kotlinx.android.synthetic.main.item_droplet.view.*
import java.util.*

class DropletsAdapter(private val context: Context, private val onClick: (Droplet) -> Unit) : RecyclerView.Adapter<DropletsAdapter.ViewHolder>() {
    private var data: List<Droplet> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_droplet, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, data[position], onClick)
    }

    fun setItems(data: List<Droplet>) {
        this.data = data
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(context: Context, droplet: Droplet, onClick: (Droplet) -> Unit) {
            itemView.run {
                if (droplet.status == "off") {
                    imgDropletStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.droplet_off))
                }

                tvName.text = droplet.name
                tvIpAddress.text = droplet.networks.v4[0].ipAddress

                val tags = droplet.tags.toString().replace("[", "").replace("]", "")
                tvTags.text = tags
                if (tvTags.text.isEmpty()) {
                    tvTags.visibility = View.GONE
                    tvName.updatePadding(bottom = 8)
                }

                rootLayout.setOnClickListener {
                    onClick(droplet)
                }
            }
        }
    }
}