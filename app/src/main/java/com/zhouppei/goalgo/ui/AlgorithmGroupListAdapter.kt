package com.zhouppei.goalgo.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.zhouppei.goalgo.R
import com.zhouppei.goalgo.databinding.ItemAlgorithmGroupBinding
import com.zhouppei.goalgo.model.AlgorithmGroup

class AlgorithmGroupListAdapter(
    private val algorithmGroupList: MutableList<AlgorithmGroup>,
    private val clickListener: NameClickListener
) : RecyclerView.Adapter<AlgorithmGroupListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemAlgorithmGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(algorithmGroupList[position])
    }

    inner class ViewHolder(private val binding: ItemAlgorithmGroupBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(algorithmGroup: AlgorithmGroup) {
            binding.algorithmGroupName.text = algorithmGroup.name
            binding.algoRecyclerview.apply {
                adapter = AlgorithmNameListAdapter(clickListener).apply {
                    submitList(algorithmGroup.algorithmNameList)
                }
            }
            binding.startIcon.setImageResource(algorithmGroup.iconResourceId)
            binding.expandButton.apply {
                tag = false
                setOnClickListener {
                    binding.algoListLayout.visibility = if (it.tag as Boolean) View.GONE else View.VISIBLE
                    (it as ImageView).setImageResource(if (it.tag as Boolean) R.drawable.ic_arrow_down else R.drawable.ic_arrow_up)
                    it.tag = !(it.tag as Boolean)
                }
            }
        }
    }

    override fun getItemCount(): Int = algorithmGroupList.size
}