package com.zhouppei.goalgo.ui.colorpicker

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zhouppei.goalgo.databinding.ItemColorButtonBinding

class ColorListAdapter(
    private val clickListener: ColorClickListener
) : ListAdapter<String, ColorListAdapter.ViewHolder>(ColorDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemColorButtonBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.button.apply {
            setBackgroundColor(if (item.isBlank()) Color.LTGRAY else Color.parseColor(item))
            isEnabled = item.isNotBlank()
            setOnClickListener { clickListener.onColorClick(item) }
        }
    }

    inner class ViewHolder(binding: ItemColorButtonBinding): RecyclerView.ViewHolder(binding.root) {
        val button: Button = binding.colorButton
    }
}

private class ColorDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}

interface ColorClickListener {
    fun onColorClick(color: String)
}