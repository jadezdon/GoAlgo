package com.zhouppei.goalgo.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zhouppei.goalgo.databinding.ItemNameBinding

class AlgorithmNameListAdapter(
    private val clickListener: NameClickListener
) : ListAdapter<String, AlgorithmNameListAdapter.ViewHolder>(NameDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemNameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemNameBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(name: String) {
            binding.nameButton.apply {
                text = name
                setOnClickListener { clickListener.onClick(name) }
            }
        }
    }
}

private class NameDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}

interface NameClickListener {
    fun onClick(name: String)
}